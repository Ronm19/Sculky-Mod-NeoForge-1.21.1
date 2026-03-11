package net.ronm19.sculky.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.ronm19.sculky.item.ModItems;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class SalvatoreEntity extends Monster implements Enemy {

    private static final EntityDataAccessor<Boolean> ATTACKING =
            SynchedEntityData.defineId(SalvatoreEntity.class, EntityDataSerializers.BOOLEAN);

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    public final AnimationState attackAnimationState = new AnimationState();
    public int attackAnimationTimeout = 0;

    private int attackTicks = 0;

    public SalvatoreEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 15;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));

        // Custom melee goal so the hit lands mid-animation instead of instantly.
        this.goalSelector.addGoal(1, new SalvatoreMeleeAttackGoal(this, 1.0D));

        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, TamableAnimal.class, true));

    }

    public static AttributeSupplier.Builder createSalvatoreAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 108.0D)
                .add(Attributes.ARMOR, 6.0D)
                .add(Attributes.ATTACK_DAMAGE, 11.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.35D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, source, recentlyHit);

        RandomSource random = this.getRandom();

        int count = random.nextInt(1); // 0–1 base drop

        if (count > 0) {
            this.spawnAtLocation(new ItemStack(ModItems.SCULK_EDGE.get(), count));
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ATTACKING, false);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            setupAnimationStates();
        }

        if (!this.level().isClientSide()) {
            if (this.attackTicks > 0) {
                --this.attackTicks;

                if (this.attackTicks <= 0) {
                    this.setAttacking(false);
                }
            }
        }
    }

    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = this.random.nextInt(40) + 80;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }

        if (this.isAttacking() && this.attackAnimationTimeout <= 0) {
            this.attackAnimationTimeout = 16; // matches 0.8 second animation
            this.attackAnimationState.start(this.tickCount);
        } else if (this.attackAnimationTimeout > 0) {
            --this.attackAnimationTimeout;
        }

        if (!this.isAttacking()) {
            this.attackAnimationState.stop();
        }
    }

    public boolean isAttacking() {
        return this.entityData.get(ATTACKING);
    }

    public void setAttacking(boolean attacking) {
        this.entityData.set(ATTACKING, attacking);
    }

    /**
     * Call this when the attack animation begins.
     */
    public void startAttackAnimation() {
        this.attackTicks = 16; // same as client animation timeout
        this.setAttacking(true);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.WARDEN_AMBIENT;
    }

    @Override
    protected @NotNull SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.WARDEN_HURT;
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return SoundEvents.WARDEN_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockState) {
        this.playSound(SoundEvents.WARDEN_STEP, 0.55F, 0.9F);
    }

    /**
     * Custom attack goal:
     * - starts animation
     * - waits a few ticks
     * - then applies damage on the "hit frame"
     */
    private static class SalvatoreMeleeAttackGoal extends Goal {
        private final SalvatoreEntity mob;
        private final double speedModifier;

        private int attackCooldown = 0;
        private int attackWarmup = 0;
        private int repathDelay = 0;

        public SalvatoreMeleeAttackGoal(SalvatoreEntity mob, double speedModifier) {
            this.mob = mob;
            this.speedModifier = speedModifier;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.mob.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = this.mob.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public void start() {
            this.attackCooldown = 0;
            this.attackWarmup = 0;
            this.repathDelay = 0;
        }

        @Override
        public void stop() {
            this.mob.getNavigation().stop();
            this.mob.setAttacking(false);
            this.attackWarmup = 0;
            this.attackCooldown = 0;
        }

        @Override
        public void tick() {
            LivingEntity target = this.mob.getTarget();
            if (target == null) return;

            this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

            double distanceSqr = this.mob.distanceToSqr(target);

            if (--this.repathDelay <= 0) {
                this.repathDelay = 4 + this.mob.getRandom().nextInt(7);

                // Keep moving unless we're very close to the hit frame
                if (this.attackWarmup <= 2) {
                    this.mob.getNavigation().moveTo(target, this.speedModifier);
                }
            }

            if (this.attackCooldown > 0) {
                --this.attackCooldown;
            }

            if (this.attackWarmup > 0) {
                --this.attackWarmup;

                if (this.attackWarmup == 0) {
                    double hitDistanceSqr = this.mob.distanceToSqr(target);

                    // Slightly more forgiving reach
                    if (target.isAlive() && hitDistanceSqr <= this.getAttackReachSqr(target) + 1.7D) {
                        this.mob.doHurtTarget(target);
                    }
                }
            }

            if (distanceSqr <= this.getAttackReachSqr(target) + 1.0D && this.attackCooldown <= 0 && this.attackWarmup <= 0) {
                this.attackCooldown = 16; // matches animation better
                this.attackWarmup = 6;    // hit lands near visible slash
                this.mob.startAttackAnimation();
            }
        }

        private double getAttackReachSqr(LivingEntity target) {
            float width = this.mob.getBbWidth() * 1.7F;
            return (double) (width * width + target.getBbWidth());
        }
    }
}