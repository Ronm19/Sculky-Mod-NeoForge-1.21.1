package net.ronm19.sculky.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.ronm19.sculky.item.ModItems;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class SculkPhantomEntity extends Phantom implements Enemy, RangedAttackMob {
    public static final float FLAP_DEGREES_PER_TICK = 7.448451F;
    public static final int TICKS_PER_FLAP = Mth.ceil(24.166098F);
    private static final EntityDataAccessor<Integer> ID_SIZE;
    Vec3 moveTargetPoint;
    BlockPos anchorPoint;
    AttackPhase attackPhase;
    private static final int SONIC_BOOM_CHARGE_TICKS = 12;
    private static final int SONIC_BOOM_COOLDOWN_TICKS = 80;
    private int sonicBoomChargeTicks = 0;
    private int sonicBoomCooldown = 0;

    @Nullable
    private LivingEntity sonicBoomTarget;

    public SculkPhantomEntity(EntityType<? extends Phantom> entityType, Level level) {
        super(entityType, level);
        this.moveTargetPoint = Vec3.ZERO;
        this.anchorPoint = BlockPos.ZERO;
        this.attackPhase = SculkPhantomEntity.AttackPhase.CIRCLE;
        this.xpReward = 5;
        this.moveControl = new SculkPhantomMoveControl(this);
        this.lookControl = new SculkPhantomLookControl(this);

    }
    public boolean isFlapping() {
        return (this.getUniqueFlapTickOffset() + this.tickCount) % TICKS_PER_FLAP == 0;
    }

    protected @NotNull BodyRotationControl createBodyControl() {
        return new SculkPhantomEntity.PhantomBodyRotationControl(this);
    }


    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SculkPhantomEntity.PhantomAttackStrategyGoal());
        this.goalSelector.addGoal(2, new SculkPhantomEntity.PhantomSweepAttackGoal());
        this.goalSelector.addGoal(3, new SculkPhantomEntity.PhantomCircleAroundAnchorGoal());
        this.goalSelector.addGoal(4, new RangedAttackGoal(this, 1.0D, 40, 1.0F));

        this.targetSelector.addGoal(1, new SculkPhantomEntity.PhantomAttackPlayerTargetGoal());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, TamableAnimal.class, true));

    }

    public static AttributeSupplier.Builder createSculkPhantomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 34.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.27D)
                .add(Attributes.FLYING_SPEED, 0.21D)
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.ARMOR, 2.0D);
    }

    @Override
    protected boolean isSunBurnTick() {
        return false;
    }

    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ID_SIZE, 0);
    }

    public void setPhantomSize(int phantomSize) {
        this.entityData.set(ID_SIZE, Mth.clamp(phantomSize, 0, 64));
    }

    private void updatePhantomSizeInfo() {
        this.refreshDimensions();
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue((double)(6 + this.getSculkPhantomSize()));
    }

    public int getSculkPhantomSize() {
        return (Integer)this.entityData.get(ID_SIZE);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (ID_SIZE.equals(key)) {
            this.updatePhantomSizeInfo();
        }

        super.onSyncedDataUpdated(key);
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);

        // Phantom Membrane: 0 to 2
        int membraneCount = this.random.nextInt(3);
        if (membraneCount > 0) {
            this.spawnAtLocation(new ItemStack(Items.PHANTOM_MEMBRANE, membraneCount));
        }

        // Echo Conduit: 15% chance
        if (this.random.nextFloat() < 0.15F) {
            this.spawnAtLocation(new ItemStack(ModItems.ECHO_CONDUIT.get()));
        }
    }

    public int getUniqueFlapTickOffset() {
        return this.getId() * 3;
    }

    protected boolean shouldDespawnInPeaceful() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) {
            float f = Mth.cos((float)(this.getUniqueFlapTickOffset() + this.tickCount) * 7.448451F * ((float)Math.PI / 180F) + (float)Math.PI);
            float f1 = Mth.cos((float)(this.getUniqueFlapTickOffset() + this.tickCount + 1) * 7.448451F * ((float)Math.PI / 180F) + (float)Math.PI);
            if (f > 0.0F && f1 <= 0.0F) {
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.PHANTOM_FLAP, this.getSoundSource(),
                        0.95F + this.random.nextFloat() * 0.05F,
                        0.95F + this.random.nextFloat() * 0.05F,
                        false);
            }

            float f2 = this.getBbWidth() * 1.48F;
            float f3 = Mth.cos(this.getYRot() * ((float)Math.PI / 180F)) * f2;
            float f4 = Mth.sin(this.getYRot() * ((float)Math.PI / 180F)) * f2;
            float f5 = (0.3F + f * 0.45F) * this.getBbHeight() * 2.5F;

            this.level().addParticle(ParticleTypes.MYCELIUM, this.getX() + (double)f3, this.getY() + (double)f5, this.getZ() + (double)f4, 0.0D, 0.0D, 0.0D);
            this.level().addParticle(ParticleTypes.MYCELIUM, this.getX() - (double)f3, this.getY() + (double)f5, this.getZ() - (double)f4, 0.0D, 0.0D, 0.0D);
        } else {
            if (this.sonicBoomCooldown > 0) {
                --this.sonicBoomCooldown;
            }

            if (this.sonicBoomChargeTicks > 0) {
                --this.sonicBoomChargeTicks;

                // Optional: slow the phantom slightly while charging
                this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));

                if (this.sonicBoomChargeTicks == 0) {
                    if (this.sonicBoomTarget != null && this.sonicBoomTarget.isAlive() && this.hasLineOfSight(this.sonicBoomTarget)) {
                        this.fireSonicBoom(this.sonicBoomTarget);
                    }
                    this.sonicBoomTarget = null;
                }
            }
        }
    }

    private boolean canUseSonicBoom(LivingEntity target) {
        if (target == null || !target.isAlive()) {
            return false;
        }

        if (this.sonicBoomCooldown > 0 || this.sonicBoomChargeTicks > 0) {
            return false;
        }

        if (!this.hasLineOfSight(target)) {
            return false;
        }

        double distanceSqr = this.distanceToSqr(target);

        // Not too close, not too far
        return distanceSqr >= 49.0D && distanceSqr <= 400.0D;
    }

    private void startSonicBoom(LivingEntity target) {
        this.sonicBoomTarget = target;
        this.sonicBoomChargeTicks = SONIC_BOOM_CHARGE_TICKS;
        this.sonicBoomCooldown = SONIC_BOOM_COOLDOWN_TICKS;

        this.level().playSound(
                null,
                this.blockPosition(),
                SoundEvents.WARDEN_SONIC_CHARGE,
                SoundSource.HOSTILE,
                2.5F,
                1.15F
        );
    }

    private void fireSonicBoom(LivingEntity target) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        Vec3 start = this.position().add(0.0D, this.getBbHeight() * 0.5D, 0.0D);
        Vec3 end = target.getEyePosition();
        Vec3 direction = end.subtract(start).normalize();

        double distance = start.distanceTo(end);
        int steps = Mth.floor(distance * 2.0D);

        for (int i = 0; i <= steps; i++) {
            Vec3 point = start.add(direction.scale(i * 0.5D));
            serverLevel.sendParticles(ParticleTypes.SONIC_BOOM, point.x, point.y, point.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }

        this.playSound(SoundEvents.WARDEN_SONIC_BOOM, 3.0F, 1.2F);

        float damage = 6.0F + this.getSculkPhantomSize();

        // If your mappings support it, you can try:
        // target.hurt(this.damageSources().sonicBoom(this), damage);
        // Otherwise this is the safer option:
        target.hurt(this.damageSources().mobAttack(this), damage);

        Vec3 knockback = direction.scale(1.6D);
        target.push(knockback.x, 0.25D, knockback.z);
        target.hurtMarked = true;
    }

    public void aiStep() {
        super.aiStep();
    }

    protected void customServerAiStep() {
        super.customServerAiStep();
    }

    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        this.anchorPoint = this.blockPosition().above(5);
        this.setPhantomSize(0);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("AX")) {
            this.anchorPoint = new BlockPos(compound.getInt("AX"), compound.getInt("AY"), compound.getInt("AZ"));
        }

        this.setPhantomSize(compound.getInt("Size"));
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("AX", this.anchorPoint.getX());
        compound.putInt("AY", this.anchorPoint.getY());
        compound.putInt("AZ", this.anchorPoint.getZ());
        compound.putInt("Size", this.getPhantomSize());
    }

    public boolean shouldRenderAtSqrDistance(double distance) {
        return true;
    }

    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.PHANTOM_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.PHANTOM_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.PHANTOM_DEATH;
    }

    protected float getSoundVolume() {
        return 1.0F;
    }

    public boolean canAttackType(EntityType<?> type) {
        return true;
    }

    public @NotNull EntityDimensions getDefaultDimensions(Pose pose) {
        int i = this.getSculkPhantomSize();
        EntityDimensions entitydimensions = super.getDefaultDimensions(pose);
        return entitydimensions.scale(1.0F + 0.15F * (float)i);
    }

    static {
        ID_SIZE = SynchedEntityData.defineId(SculkPhantomEntity.class, EntityDataSerializers.INT);
    }

    @Override
    public void performRangedAttack(@NotNull LivingEntity livingEntity, float v) {
        if (livingEntity == null || !livingEntity.isAlive()) {
            return;
        }

        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        Vec3 start = this.position().add(0.0D, this.getBbHeight() * 0.5D, 0.0D);
        Vec3 end = livingEntity.getEyePosition();
        Vec3 direction = end.subtract(start).normalize();

        double distance = start.distanceTo(end);
        int steps = Mth.floor(distance * 2.0D);

        // Sonic boom particles along the path
        for (int i = 0; i <= steps; i++) {
            Vec3 point = start.add(direction.scale(i * 0.5D));
            serverLevel.sendParticles(ParticleTypes.SONIC_BOOM, point.x, point.y, point.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }

        // Sound
        this.playSound(SoundEvents.WARDEN_SONIC_BOOM, 3.0F, 1.2F);

        // Damage
        float damage = 6.0F + this.getSculkPhantomSize();
        livingEntity.hurt(this.damageSources().mobAttack(this), damage);

        // Knockback
        Vec3 knockback = direction.scale(1.6D);
        livingEntity.push(knockback.x, 0.25D, knockback.z);
        livingEntity.hurtMarked = true;
    }

    static enum AttackPhase {
        CIRCLE,
        SWOOP;
    }

    class PhantomAttackPlayerTargetGoal extends Goal {
        private final TargetingConditions attackTargeting = TargetingConditions.forCombat().range((double)64.0F);
        private int nextScanTick = reducedTickDelay(20);

        public boolean canUse() {
            if (this.nextScanTick > 0) {
                --this.nextScanTick;
                return false;
            } else {
                this.nextScanTick = reducedTickDelay(60);
                List<Player> list = SculkPhantomEntity.this.level().getNearbyPlayers(this.attackTargeting, SculkPhantomEntity.this, SculkPhantomEntity.this.getBoundingBox().inflate((double)16.0F, (double)64.0F, (double)16.0F));
                if (!list.isEmpty()) {
                    list.sort(Comparator.comparing(Entity::getYRot).reversed());

                    for(Player player : list) {
                        if (SculkPhantomEntity.this.canAttack(player, TargetingConditions.DEFAULT)) {
                            SculkPhantomEntity.this.setTarget(player);
                            return true;
                        }
                    }
                }

                return false;
            }
        }

        public boolean canContinueToUse() {
            LivingEntity livingentity = SculkPhantomEntity.this.getTarget();
            return livingentity != null ? SculkPhantomEntity.this.canAttack(livingentity, TargetingConditions.DEFAULT) : false;
        }
    }

    class PhantomAttackStrategyGoal extends Goal {
        private int nextSweepTick;

        public boolean canUse() {
            LivingEntity livingentity = SculkPhantomEntity.this.getTarget();
            return livingentity != null ? SculkPhantomEntity.this.canAttack(livingentity, TargetingConditions.DEFAULT) : false;
        }

        public void start() {
            this.nextSweepTick = this.adjustedTickDelay(10);
            SculkPhantomEntity.this.attackPhase = SculkPhantomEntity.AttackPhase.CIRCLE;
            this.setAnchorAboveTarget();
        }

        public void stop() {
            SculkPhantomEntity.this.anchorPoint = SculkPhantomEntity.this.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, SculkPhantomEntity.this.anchorPoint).above(10 + SculkPhantomEntity.this.random.nextInt(20));
        }

        @Override
        public void tick() {
            LivingEntity target = SculkPhantomEntity.this.getTarget();

            if (target != null && SculkPhantomEntity.this.attackPhase == SculkPhantomEntity.AttackPhase.CIRCLE) {
                if (SculkPhantomEntity.this.canUseSonicBoom(target)) {
                    SculkPhantomEntity.this.startSonicBoom(target);
                }
            }

            if (SculkPhantomEntity.this.attackPhase == SculkPhantomEntity.AttackPhase.CIRCLE) {
                --this.nextSweepTick;
                if (this.nextSweepTick <= 0) {
                    SculkPhantomEntity.this.attackPhase = SculkPhantomEntity.AttackPhase.SWOOP;
                    this.setAnchorAboveTarget();
                    this.nextSweepTick = this.adjustedTickDelay((8 + SculkPhantomEntity.this.random.nextInt(4)) * 20);
                    SculkPhantomEntity.this.playSound(SoundEvents.PHANTOM_SWOOP, 10.0F, 0.95F + SculkPhantomEntity.this.random.nextFloat() * 0.1F);
                }
            }
        }

        private void setAnchorAboveTarget() {
            SculkPhantomEntity.this.anchorPoint = SculkPhantomEntity.this.getTarget().blockPosition().above(20 + SculkPhantomEntity.this.random.nextInt(20));
            if (SculkPhantomEntity.this.anchorPoint.getY() < SculkPhantomEntity.this.level().getSeaLevel()) {
                SculkPhantomEntity.this.anchorPoint = new BlockPos(SculkPhantomEntity.this.anchorPoint.getX(), SculkPhantomEntity.this.level().getSeaLevel() + 1, SculkPhantomEntity.this.anchorPoint.getZ());
            }

        }
    }

    class PhantomBodyRotationControl extends BodyRotationControl {
        public PhantomBodyRotationControl(Mob mob) {
            super(mob);
        }

        public void clientTick() {
            SculkPhantomEntity.this.yHeadRot = SculkPhantomEntity.this.yBodyRot;
            SculkPhantomEntity.this.yBodyRot = SculkPhantomEntity.this.getYRot();
        }
    }

    class PhantomCircleAroundAnchorGoal extends SculkPhantomEntity.PhantomMoveTargetGoal {
        private float angle;
        private float distance;
        private float height;
        private float clockwise;

        public boolean canUse() {
            return SculkPhantomEntity.this.getTarget() == null || SculkPhantomEntity.this.attackPhase == SculkPhantomEntity.AttackPhase.CIRCLE;
        }

        public void start() {
            this.distance = 5.0F + SculkPhantomEntity.this.random.nextFloat() * 10.0F;
            this.height = -4.0F + SculkPhantomEntity.this.random.nextFloat() * 9.0F;
            this.clockwise = SculkPhantomEntity.this.random.nextBoolean() ? 1.0F : -1.0F;
            this.selectNext();
        }

        public void tick() {
            if (SculkPhantomEntity.this.random.nextInt(this.adjustedTickDelay(350)) == 0) {
                this.height = -4.0F + SculkPhantomEntity.this.random.nextFloat() * 9.0F;
            }

            if (SculkPhantomEntity.this.random.nextInt(this.adjustedTickDelay(250)) == 0) {
                ++this.distance;
                if (this.distance > 15.0F) {
                    this.distance = 5.0F;
                    this.clockwise = -this.clockwise;
                }
            }

            if (SculkPhantomEntity.this.random.nextInt(this.adjustedTickDelay(450)) == 0) {
                this.angle = SculkPhantomEntity.this.random.nextFloat() * 2.0F * (float)Math.PI;
                this.selectNext();
            }

            if (this.touchingTarget()) {
                this.selectNext();
            }

            if (SculkPhantomEntity.this.moveTargetPoint.y < SculkPhantomEntity.this.getY() && !SculkPhantomEntity.this.level().isEmptyBlock(SculkPhantomEntity.this.blockPosition().below(1))) {
                this.height = Math.max(1.0F, this.height);
                this.selectNext();
            }

            if (SculkPhantomEntity.this.moveTargetPoint.y > SculkPhantomEntity.this.getY() && !SculkPhantomEntity.this.level().isEmptyBlock(SculkPhantomEntity.this.blockPosition().above(1))) {
                this.height = Math.min(-1.0F, this.height);
                this.selectNext();
            }

        }

        private void selectNext() {
            if (BlockPos.ZERO.equals(SculkPhantomEntity.this.anchorPoint)) {
                SculkPhantomEntity.this.anchorPoint = SculkPhantomEntity.this.blockPosition();
            }

            this.angle += this.clockwise * 15.0F * ((float)Math.PI / 180F);
            SculkPhantomEntity.this.moveTargetPoint = Vec3.atLowerCornerOf(SculkPhantomEntity.this.anchorPoint).add((double)(this.distance * Mth.cos(this.angle)), (double)(-4.0F + this.height), (double)(this.distance * Mth.sin(this.angle)));
        }
    }

    static class SculkPhantomLookControl extends LookControl {
        public SculkPhantomLookControl(Mob mob) {
            super(mob);
        }

        public void tick() {
        }
    }

    class SculkPhantomMoveControl extends MoveControl {
        private float speed = 0.1F;

        public SculkPhantomMoveControl(Mob mob) {
            super(mob);
        }

        public void tick() {
            if (SculkPhantomEntity.this.horizontalCollision) {
                SculkPhantomEntity.this.setYRot(SculkPhantomEntity.this.getYRot() + 180.0F);
                this.speed = 0.1F;
            }

            double d0 = SculkPhantomEntity.this.moveTargetPoint.x - SculkPhantomEntity.this.getX();
            double d1 = SculkPhantomEntity.this.moveTargetPoint.y - SculkPhantomEntity.this.getY();
            double d2 = SculkPhantomEntity.this.moveTargetPoint.z - SculkPhantomEntity.this.getZ();
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            if (Math.abs(d3) > (double)1.0E-5F) {
                double d4 = (double)1.0F - Math.abs(d1 * (double)0.7F) / d3;
                d0 *= d4;
                d2 *= d4;
                d3 = Math.sqrt(d0 * d0 + d2 * d2);
                double d5 = Math.sqrt(d0 * d0 + d2 * d2 + d1 * d1);
                float f = SculkPhantomEntity.this.getYRot();
                float f1 = (float)Mth.atan2(d2, d0);
                float f2 = Mth.wrapDegrees(SculkPhantomEntity.this.getYRot() + 90.0F);
                float f3 = Mth.wrapDegrees(f1 * (180F / (float)Math.PI));
                SculkPhantomEntity.this.setYRot(Mth.approachDegrees(f2, f3, 4.0F) - 90.0F);
                SculkPhantomEntity.this.yBodyRot = SculkPhantomEntity.this.getYRot();
                if (Mth.degreesDifferenceAbs(f, SculkPhantomEntity.this.getYRot()) < 3.0F) {
                    this.speed = Mth.approach(this.speed, 1.8F, 0.005F * (1.8F / this.speed));
                } else {
                    this.speed = Mth.approach(this.speed, 0.2F, 0.025F);
                }

                float f4 = (float)(-(Mth.atan2(-d1, d3) * (double)180.0F / (double)(float)Math.PI));
                SculkPhantomEntity.this.setXRot(f4);
                float f5 = SculkPhantomEntity.this.getYRot() + 90.0F;
                double d6 = (double)(this.speed * Mth.cos(f5 * ((float)Math.PI / 180F))) * Math.abs(d0 / d5);
                double d7 = (double)(this.speed * Mth.sin(f5 * ((float)Math.PI / 180F))) * Math.abs(d2 / d5);
                double d8 = (double)(this.speed * Mth.sin(f4 * ((float)Math.PI / 180F))) * Math.abs(d1 / d5);
                Vec3 vec3 = SculkPhantomEntity.this.getDeltaMovement();
                SculkPhantomEntity.this.setDeltaMovement(vec3.add((new Vec3(d6, d8, d7)).subtract(vec3).scale(0.2)));
            }

        }
    }

    abstract class PhantomMoveTargetGoal extends Goal {
        public PhantomMoveTargetGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        protected boolean touchingTarget() {
            return SculkPhantomEntity.this.moveTargetPoint.distanceToSqr(SculkPhantomEntity.this.getX(), SculkPhantomEntity.this.getY(), SculkPhantomEntity.this.getZ()) < (double)4.0F;
        }
    }

    class PhantomSweepAttackGoal extends SculkPhantomEntity.PhantomMoveTargetGoal {
        private static final int CAT_SEARCH_TICK_DELAY = 20;
        private boolean isScaredOfCat;
        private int catSearchTick;

        public boolean canUse() {
            return SculkPhantomEntity.this.getTarget() != null && SculkPhantomEntity.this.attackPhase == SculkPhantomEntity.AttackPhase.SWOOP;
        }

        public boolean canContinueToUse() {
            LivingEntity livingentity = SculkPhantomEntity.this.getTarget();
            if (livingentity == null) {
                return false;
            } else if (!livingentity.isAlive()) {
                return false;
            } else {
                if (livingentity instanceof Player) {
                    Player player = (Player)livingentity;
                    if (livingentity.isSpectator() || player.isCreative()) {
                        return false;
                    }
                }

                if (!this.canUse()) {
                    return false;
                } else {
                    if (SculkPhantomEntity.this.tickCount > this.catSearchTick) {
                        this.catSearchTick = SculkPhantomEntity.this.tickCount + 20;
                        List<Cat> list = SculkPhantomEntity.this.level().getEntitiesOfClass(Cat.class, SculkPhantomEntity.this.getBoundingBox().inflate((double)16.0F), EntitySelector.ENTITY_STILL_ALIVE);

                        for(Cat cat : list) {
                            cat.hiss();
                        }

                        this.isScaredOfCat = !list.isEmpty();
                    }

                    return !this.isScaredOfCat;
                }
            }
        }

        public void start() {
        }

        public void stop() {
            SculkPhantomEntity.this.setTarget((LivingEntity)null);
            SculkPhantomEntity.this.attackPhase = SculkPhantomEntity.AttackPhase.CIRCLE;
        }

        public void tick() {
            LivingEntity livingentity = SculkPhantomEntity.this.getTarget();
            if (livingentity != null) {
                SculkPhantomEntity.this.moveTargetPoint = new Vec3(livingentity.getX(), livingentity.getY((double)0.5F), livingentity.getZ());
                if (SculkPhantomEntity.this.getBoundingBox().inflate((double)0.2F).intersects(livingentity.getBoundingBox())) {
                    SculkPhantomEntity.this.doHurtTarget(livingentity);
                    SculkPhantomEntity.this.attackPhase = SculkPhantomEntity.AttackPhase.CIRCLE;
                    if (!SculkPhantomEntity.this.isSilent()) {
                        SculkPhantomEntity.this.level().levelEvent(1039, SculkPhantomEntity.this.blockPosition(), 0);
                    }
                } else if (SculkPhantomEntity.this.horizontalCollision || SculkPhantomEntity.this.hurtTime > 0) {
                    SculkPhantomEntity.this.attackPhase = SculkPhantomEntity.AttackPhase.CIRCLE;
                }
            }

        }
    }
}
