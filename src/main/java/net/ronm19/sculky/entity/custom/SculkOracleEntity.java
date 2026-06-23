package net.ronm19.sculky.entity.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class SculkOracleEntity extends Monster {
    private static final EntityDataAccessor<Boolean> ATTACKING =
            SynchedEntityData.defineId(SculkOracleEntity.class, EntityDataSerializers.BOOLEAN);

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();

    private int idleAnimationTimeout = 0;
    private int attackAnimationTimeout = 0;

    public SculkOracleEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 12;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ATTACKING, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));

        // Main Oracle casting attack.
        this.goalSelector.addGoal(2, new OracleCastAttackGoal(this));

        // Keep movement simple. He still walks physically, but the model/renderer can make him look slightly hovering.
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.75D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, TamableAnimal.class, true));
    }

    public static AttributeSupplier.Builder createSculkOracleAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 34.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.21D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.FOLLOW_RANGE, 28.0D)
                .add(Attributes.ARMOR, 4.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.25D);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            this.setupAnimationStates();
        }
    }

    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = 40;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }

        if (this.isAttacking()) {
            if (this.attackAnimationTimeout <= 0) {
                this.attackAnimationTimeout = 20;
                this.attackAnimationState.start(this.tickCount);
            } else {
                --this.attackAnimationTimeout;
            }
        } else {
            this.attackAnimationState.stop();
            this.attackAnimationTimeout = 0;
        }
    }

    public void setAttacking(boolean attacking) {
        this.entityData.set(ATTACKING, attacking);
    }

    public boolean isAttacking() {
        return this.entityData.get(ATTACKING);
    }

    // Use this later in the renderer/model if you want a tiny visual hover.
    // Do NOT set noGravity unless you actually want him to fly.
    public float getHoverOffset(float partialTick) {
        return Mth.sin((this.tickCount + partialTick) * 0.12F) * 0.05F;
    }

    private void performOracleAttack(LivingEntity target) {
        if (target == null || !target.isAlive()) {
            return;
        }

        if (this.distanceToSqr(target) > 20.0D * 20.0D) {
            return;
        }

        DamageSource source = this.damageSources().mobAttack(this);
        target.hurt(source, 6.0F);

        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 0), this);
        target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 40, 0), this);

        this.level().playSound(null, target.blockPosition(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.HOSTILE, 0.7F, 1.45F);

        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.SCULK_SOUL,
                    target.getX(),
                    target.getY() + 0.6D,
                    target.getZ(),
                    14,
                    0.35D,
                    0.45D,
                    0.35D,
                    0.03D
            );

            serverLevel.sendParticles(
                    ParticleTypes.SONIC_BOOM,
                    target.getX(),
                    target.getY() + 1.0D,
                    target.getZ(),
                    1,
                    0.0D,
                    0.0D,
                    0.0D,
                    0.0D
            );
        }
    }

    private static class OracleCastAttackGoal extends Goal {
        private final SculkOracleEntity oracle;

        private LivingEntity target;
        private int castTicks;
        private int cooldownTicks;

        private static final int CAST_TIME = 20;
        private static final int RELEASE_TICK = 15;
        private static final int COOLDOWN = 45;
        private static final double ATTACK_RANGE = 18.0D;

        public OracleCastAttackGoal(SculkOracleEntity oracle) {
            this.oracle = oracle;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (this.cooldownTicks > 0) {
                --this.cooldownTicks;
                return false;
            }

            LivingEntity livingEntity = this.oracle.getTarget();

            if (livingEntity == null || !livingEntity.isAlive()) {
                return false;
            }

            if (this.oracle.distanceToSqr(livingEntity) > ATTACK_RANGE * ATTACK_RANGE) {
                return false;
            }

            return this.oracle.hasLineOfSight(livingEntity);
        }

        @Override
        public boolean canContinueToUse() {
            return this.target != null
                    && this.target.isAlive()
                    && this.castTicks < CAST_TIME
                    && this.oracle.distanceToSqr(this.target) <= 22.0D * 22.0D;
        }

        @Override
        public void start() {
            this.target = this.oracle.getTarget();
            this.castTicks = 0;

            this.oracle.setAttacking(true);
            this.oracle.getNavigation().stop();

            this.oracle.level().playSound(
                    null,
                    this.oracle.blockPosition(),
                    SoundEvents.SCULK_SHRIEKER_SHRIEK,
                    SoundSource.HOSTILE,
                    0.55F,
                    0.75F
            );
        }

        @Override
        public void stop() {
            this.oracle.setAttacking(false);
            this.target = null;
            this.castTicks = 0;
            this.cooldownTicks = COOLDOWN;
        }

        @Override
        public void tick() {
            if (this.target == null) {
                return;
            }

            this.oracle.getNavigation().stop();
            this.oracle.getLookControl().setLookAt(this.target, 30.0F, 30.0F);

            ++this.castTicks;

            if (this.castTicks == RELEASE_TICK) {
                this.oracle.performOracleAttack(this.target);
            }
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.WARDEN_TENDRIL_CLICKS;
    }

    @Override
    protected @NotNull SoundEvent getHurtSound( DamageSource damageSource) {
        return SoundEvents.WARDEN_HURT;
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return SoundEvents.WARDEN_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.75F;
    }

    @Override
    public float getVoicePitch() {
        return 1.35F;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 120;
    }
}