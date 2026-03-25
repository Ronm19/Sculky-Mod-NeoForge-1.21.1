package net.ronm19.sculky.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.NotNull;

public class SculkBurrowerEntity extends Monster implements Enemy {

    private static final EntityDataAccessor<Boolean> ATTACKING =
            SynchedEntityData.defineId(SculkBurrowerEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> BURROWING =
            SynchedEntityData.defineId(SculkBurrowerEntity.class, EntityDataSerializers.BOOLEAN);

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();
    public final AnimationState burrowAnimationState = new AnimationState();
    public final AnimationState emergeAnimationState = new AnimationState();

    private int idleAnimationTimeout = 0;
    private int attackAnimationTimeout = 0;

    private int burrowCooldown = 0;
    private int burrowTicks = 0;
    private int emergeTicks = 0;

    private static final int BURROW_DURATION = 10;
    private static final int EMERGE_DURATION = 10;

    public SculkBurrowerEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 8;
    }

    public static AttributeSupplier.Builder createSculkBurrowerAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 28.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.30D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.ARMOR, 2.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 0.35D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new LeapAtTargetGoal(this, 0.25F));
        this.goalSelector.addGoal(2, new SculkBurrowerMeleeAttackGoal(this, 1.15D, false));
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.9D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, TamableAnimal.class, true));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ATTACKING, false);
        builder.define(BURROWING, false);
    }

    public boolean isAttacking() {
        return this.entityData.get(ATTACKING);
    }

    public void setAttacking(boolean attacking) {
        this.entityData.set(ATTACKING, attacking);
    }

    public boolean isBurrowing() {
        return this.entityData.get(BURROWING);
    }

    public void setBurrowing(boolean burrowing) {
        this.entityData.set(BURROWING, burrowing);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.burrowCooldown > 0) {
            this.burrowCooldown--;
        }

        if (!this.level().isClientSide) {
            this.handleBurrowLogic();
        } else {
            this.setupAnimationStates();
        }
    }

    private void handleBurrowLogic() {
        LivingEntity target = this.getTarget();

        if (target == null || !target.isAlive()) {
            return;
        }

        if (!this.isBurrowing() && this.emergeTicks <= 0) {
            double distanceSqr = this.distanceToSqr(target);

            if (distanceSqr > 25.0D && distanceSqr < 144.0D && this.burrowCooldown <= 0) {
                startBurrow();
            }
        }

        if (this.isBurrowing()) {
            this.burrowTicks--;

            if (this.burrowTicks == BURROW_DURATION / 2) {
                repositionNearTarget(target);
            }

            if (this.burrowTicks <= 0) {
                finishBurrow();
            }
        }

        if (this.emergeTicks > 0) {
            this.emergeTicks--;
        }
    }

    private void startBurrow() {
        this.setBurrowing(true);
        this.setAttacking(false);
        this.burrowTicks = BURROW_DURATION;
        this.burrowCooldown = 100 + this.random.nextInt(60);

        this.setDeltaMovement(0.0D, 0.0D, 0.0D);

        this.playSound(SoundEvents.GRAVEL_BREAK, 0.9F, 0.8F);
        spawnBurrowParticles();
    }

    private void finishBurrow() {
        this.setBurrowing(false);
        this.emergeTicks = EMERGE_DURATION;

        this.playSound(SoundEvents.SCULK_SHRIEKER_SHRIEK, 0.7F, 1.3F);
        this.playSound(SoundEvents.GRAVEL_PLACE, 0.8F, 0.9F);
        spawnBurrowParticles();
    }

    private void repositionNearTarget(LivingEntity target) {
        if (!(this.level() instanceof ServerLevel serverLevel)) return;

        for (int i = 0; i < 12; i++) {
            int offsetX = this.random.nextInt(7) - 3; // -3 to 3
            int offsetZ = this.random.nextInt(7) - 3; // -3 to 3

            BlockPos tryPos = target.blockPosition().offset(offsetX, 0, offsetZ);
            BlockPos groundPos = findGround(serverLevel, tryPos);

            if (groundPos == null) continue;

            this.moveTo(
                    groundPos.getX() + 0.5D,
                    groundPos.getY(),
                    groundPos.getZ() + 0.5D,
                    this.getYRot(),
                    this.getXRot()
            );

            this.setDeltaMovement(0.0D, 0.0D, 0.0D);
            this.fallDistance = 0.0F;
            return;
        }
    }

    private BlockPos findGround(ServerLevel level, BlockPos origin) {
        BlockPos surface = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, origin);
        BlockPos spawnPos = surface.above();

        BlockState below = level.getBlockState(surface);
        BlockState at = level.getBlockState(spawnPos);
        BlockState above = level.getBlockState(spawnPos.above());

        if (!below.isFaceSturdy(level, surface, net.minecraft.core.Direction.UP)) {
            return null;
        }

        if (below.is(BlockTags.LEAVES)) {
            return null;
        }

        if (!at.canBeReplaced() || !above.canBeReplaced()) {
            return null;
        }

        return spawnPos;
    }

    private void spawnBurrowParticles() {
        if (!(this.level() instanceof ServerLevel serverLevel)) return;

        for (int i = 0; i < 12; i++) {
            double x = this.getX() + (this.random.nextDouble() - 0.5D) * this.getBbWidth();
            double y = this.getY();
            double z = this.getZ() + (this.random.nextDouble() - 0.5D) * this.getBbWidth();

            serverLevel.sendParticles(
                    ParticleTypes.SCULK_SOUL,
                    x, y, z,
                    1,
                    0.0D, 0.05D, 0.0D,
                    0.0D
            );

            serverLevel.sendParticles(
                    ParticleTypes.POOF,
                    x, y, z,
                    1,
                    0.05D, 0.05D, 0.05D,
                    0.0D
            );
        }
    }

    private void setupAnimationStates() {
        boolean moving = this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6D;

        if (moving && !this.isBurrowing() && this.emergeTicks <= 0) {
            if (!this.walkAnimationState.isStarted()) {
                this.walkAnimationState.start(this.tickCount);
            }
        } else {
            this.walkAnimationState.stop();
        }

        if (!moving && !this.isAttacking() && !this.isBurrowing() && this.emergeTicks <= 0) {
            if (this.idleAnimationTimeout <= 0) {
                this.idleAnimationTimeout = 40 + this.random.nextInt(30);
                this.idleAnimationState.start(this.tickCount);
            } else {
                this.idleAnimationTimeout--;
            }
        } else {
            this.idleAnimationState.stop();
        }

        if (this.isAttacking() && !this.isBurrowing() && this.emergeTicks <= 0) {
            if (this.attackAnimationTimeout <= 0) {
                this.attackAnimationTimeout = 10;
                this.attackAnimationState.start(this.tickCount);
            } else {
                this.attackAnimationTimeout--;
            }
        } else {
            this.attackAnimationTimeout = 0;
            this.attackAnimationState.stop();
        }

        if (this.isBurrowing()) {
            if (!this.burrowAnimationState.isStarted()) {
                this.burrowAnimationState.start(this.tickCount);
            }
        } else {
            this.burrowAnimationState.stop();
        }

        if (this.emergeTicks > 0) {
            if (!this.emergeAnimationState.isStarted()) {
                this.emergeAnimationState.start(this.tickCount);
            }
        } else {
            this.emergeAnimationState.stop();
        }
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity entity) {
        boolean success = super.doHurtTarget(entity);

        if (success && entity instanceof LivingEntity livingTarget) {
            livingTarget.knockback(
                    0.45F,
                    livingTarget.getX() - this.getX(),
                    livingTarget.getZ() - this.getZ()
            );
        }

        return success;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        if (this.isBurrowing()) {
            return true;
        }
        return super.isInvulnerableTo(source);
    }

    @Override
    public boolean isPushable() {
        return !this.isBurrowing();
    }

    @Override
    public boolean canBeCollidedWith() {
        return !this.isBurrowing();
    }

    @Override
    public void tick() {
        super.tick();

        if (this.isBurrowing()) {
            this.noPhysics = true;
            this.setNoGravity(true);
        } else {
            this.noPhysics = false;
            this.setNoGravity(false);
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SPIDER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.SPIDER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SPIDER_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.SILVERFISH_STEP, 0.25F, 0.9F);
    }

    @Override
    protected float getSoundVolume() {
        return 0.8F;
    }

    static class SculkBurrowerMeleeAttackGoal extends MeleeAttackGoal {
        private final SculkBurrowerEntity burrower;

        public SculkBurrowerMeleeAttackGoal(SculkBurrowerEntity burrower, double speedModifier, boolean followingTargetEvenIfNotSeen) {
            super(burrower, speedModifier, followingTargetEvenIfNotSeen);
            this.burrower = burrower;
        }

        @Override
        public void start() {
            super.start();
            this.burrower.setAttacking(false);
        }

        @Override
        public void stop() {
            super.stop();
            this.burrower.setAttacking(false);
        }

        @Override
        public void tick() {
            super.tick();

            if (this.burrower.isBurrowing() || this.burrower.emergeTicks > 0) {
                this.burrower.setAttacking(false);
                return;
            }

            LivingEntity target = this.burrower.getTarget();
            if (target == null) {
                this.burrower.setAttacking(false);
                return;
            }

            double reachSqr = this.getAttackReachSqr(target);
            double distanceSqr = this.burrower.distanceToSqr(target);

            this.burrower.setAttacking(
                    distanceSqr <= reachSqr + 2.0D && this.getTicksUntilNextAttack() <= 8
            );
        }

        protected double getAttackReachSqr(LivingEntity target) {
            return (double)(this.mob.getBbWidth() * 1.8F * this.mob.getBbWidth() * 1.8F + target.getBbWidth());
        }
    }
}