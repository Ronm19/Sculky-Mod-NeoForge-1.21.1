package net.ronm19.sculky.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SculkBruteEntity extends Monster implements Enemy {

    private static final EntityDataAccessor<Boolean> ATTACKING =
            SynchedEntityData.defineId(SculkBruteEntity.class, EntityDataSerializers.BOOLEAN);

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();

    private int idleAnimationTimeout = 0;
    private int attackAnimationTimeout = 0;

    public SculkBruteEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 12;
    }

    public static AttributeSupplier.Builder createSculkBruteAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 75.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.22D)
                .add(Attributes.ATTACK_DAMAGE, 9.0D)
                .add(Attributes.ARMOR, 6.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.45D)
                .add(Attributes.FOLLOW_RANGE, 28.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.goalSelector.addGoal(1, new SculkBruteMeleeAttackGoal(this, 1.0D, false));

        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D, 1.0F));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, TamableAnimal.class, true));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ATTACKING, false);
    }

    public boolean isAttacking() {
        return this.entityData.get(ATTACKING);
    }

    public void setAttacking(boolean attacking) {
        this.entityData.set(ATTACKING, attacking);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.level().isClientSide()) {
            this.setupAnimationStates();
        }
    }

    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = 60 + this.random.nextInt(40);
            this.idleAnimationState.start(this.tickCount);
        } else {
            this.idleAnimationTimeout--;
        }

        if (this.isAttacking()) {
            if (this.attackAnimationTimeout <= 0) {
                this.attackAnimationTimeout = 8;
                this.attackAnimationState.start(this.tickCount);
            } else {
                this.attackAnimationTimeout--;
            }
        } else {
            this.attackAnimationTimeout = 0;
            this.attackAnimationState.stop();
        }
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity entity) {
        boolean success = super.doHurtTarget(entity);

        if (success && entity instanceof LivingEntity livingTarget) {
            livingTarget.knockback(
                    0.8F,
                    livingTarget.getX() - this.getX(),
                    livingTarget.getZ() - this.getZ()
            );
        }

        return success;
    }

    public boolean shouldGlowEyes() {
        if (this.level() == null) return false;

        long time = this.level().getDayTime() % 24000L;
        return time >= 13000L && time <= 23000L;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.RAVAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(net.minecraft.world.damagesource.DamageSource damageSource) {
        return SoundEvents.RAVAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.RAVAGER_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.IRON_GOLEM_STEP, 0.7F, 0.8F);
    }

    @Override
    protected float getSoundVolume() {
        return 1.0F;
    }

    static class SculkBruteMeleeAttackGoal extends MeleeAttackGoal {
        private final SculkBruteEntity brute;

        public SculkBruteMeleeAttackGoal(SculkBruteEntity brute, double speedModifier, boolean followingTargetEvenIfNotSeen) {
            super(brute, speedModifier, followingTargetEvenIfNotSeen);
            this.brute = brute;
        }

        @Override
        public void start() {
            super.start();
            this.brute.setAttacking(false);
        }

        @Override
        public void stop() {
            super.stop();
            this.brute.setAttacking(false);
        }

        private double getAttackReachSqr(LivingEntity pEnemy) {
            return this.mob.getBbWidth() * this.mob.getBbWidth() + pEnemy.getBbWidth();
        }

        @Override
        public void tick() {
            super.tick();

            LivingEntity target = this.brute.getTarget();
            if (target == null) {
                this.brute.setAttacking(false);
                return;
            }

            double reachSqr = this.getAttackReachSqr(target);
            double distanceSqr = this.brute.distanceToSqr(target);

            this.brute.setAttacking(
                    distanceSqr <= reachSqr + 4.0D && this.getTicksUntilNextAttack() <= 12
            );
        }
    }
}