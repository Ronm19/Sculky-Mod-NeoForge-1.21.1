package net.ronm19.sculky.entity.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.ronm19.sculky.item.ModItems;
import net.ronm19.sculky.util.SculkFactionHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class SculkExecutionerEntity extends Monster implements Enemy {
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();

    private static final EntityDataAccessor<Integer> ATTACK_ANIMATION_TICKS =
            SynchedEntityData.defineId(SculkExecutionerEntity.class, EntityDataSerializers.INT);

    private int executionSlamCooldown = 20 * 7;

    public SculkExecutionerEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 25;

        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.SCULK_CLEAVER.get()));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ATTACK_ANIMATION_TICKS, 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 0.95D, false));

        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 0.45D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 12.0F));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    public static AttributeSupplier.Builder createSculkExecutionerAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 70.0D)
                .add(Attributes.ATTACK_DAMAGE, 12.0D)
                .add(Attributes.ARMOR, 6.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 2.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6D);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            if (this.getAttackAnimationTicks() > 0) {
                this.setAttackAnimationTicks(this.getAttackAnimationTicks() - 1);
            }
        } else {
            this.setupAnimationStates();
        }
    }

    private void setupAnimationStates() {
        this.idleAnimationState.startIfStopped(this.tickCount);

        if (this.getAttackAnimationTicks() > 0) {
            this.attackAnimationState.startIfStopped(this.tickCount);
        } else {
            this.attackAnimationState.stop();
        }
    }

    public int getAttackAnimationTicks() {
        return this.entityData.get(ATTACK_ANIMATION_TICKS);
    }

    public void setAttackAnimationTicks(int ticks) {
        this.entityData.set(ATTACK_ANIMATION_TICKS, ticks);
    }

    public void triggerAttackAnimation() {
        // 19 ticks = 0.95 seconds, matching the ATTACK animation length.
        this.setAttackAnimationTicks(19);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        if (this.executionSlamCooldown > 0) {
            this.executionSlamCooldown--;
        }

        LivingEntity target = this.getTarget();

        if (target != null && target.isAlive()
                && this.distanceToSqr(target) <= 5.5D * 5.5D
                && this.executionSlamCooldown <= 0) {
            this.executionSlam(serverLevel);
            this.executionSlamCooldown = 20 * 8;
        }

        if (this.tickCount % 30 == 0) {
            serverLevel.sendParticles(
                    ParticleTypes.SCULK_SOUL,
                    this.getX(),
                    this.getY() + 1.7D,
                    this.getZ(),
                    4,
                    0.25D,
                    0.35D,
                    0.25D,
                    0.01D
            );
        }
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity entity) {
        this.triggerAttackAnimation();

        boolean hurt = super.doHurtTarget(entity);

        if (hurt && entity instanceof LivingEntity target) {
            target.knockback(
                    1.35D,
                    this.getX() - target.getX(),
                    this.getZ() - target.getZ()
            );

            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.playSound(
                        null,
                        this.blockPosition(),
                        SoundEvents.WARDEN_ATTACK_IMPACT,
                        SoundSource.HOSTILE,
                        0.85F,
                        0.8F
                );

                serverLevel.sendParticles(
                        ParticleTypes.SCULK_SOUL,
                        target.getX(),
                        target.getY() + 0.9D,
                        target.getZ(),
                        12,
                        0.28D,
                        0.28D,
                        0.28D,
                        0.03D
                );
            }
        }

        return hurt;
    }

    private void executionSlam(ServerLevel level) {
        this.triggerAttackAnimation();

        double radius = 4.0D;
        float damage = 7.0F;
        double knockback = 1.65D;

        AABB area = this.getBoundingBox().inflate(radius);

        for (LivingEntity entity : level.getEntitiesOfClass(
                LivingEntity.class,
                area,
                this::isValidExecutionerTarget
        )) {
            entity.hurt(this.damageSources().mobAttack(this), damage);

            entity.knockback(
                    knockback,
                    this.getX() - entity.getX(),
                    this.getZ() - entity.getZ()
            );

            level.sendParticles(
                    ParticleTypes.SCULK_SOUL,
                    entity.getX(),
                    entity.getY() + 0.8D,
                    entity.getZ(),
                    8,
                    0.25D,
                    0.25D,
                    0.25D,
                    0.03D
            );
        }

        level.playSound(
                null,
                this.blockPosition(),
                SoundEvents.WARDEN_ATTACK_IMPACT,
                SoundSource.HOSTILE,
                1.0F,
                0.65F
        );

        level.playSound(
                null,
                this.blockPosition(),
                SoundEvents.SCULK_SHRIEKER_SHRIEK,
                SoundSource.HOSTILE,
                0.35F,
                1.2F
        );

        level.sendParticles(
                ParticleTypes.SONIC_BOOM,
                this.getX(),
                this.getY() + 1.0D,
                this.getZ(),
                1,
                0.0D,
                0.0D,
                0.0D,
                0.0D
        );

        level.sendParticles(
                ParticleTypes.SCULK_SOUL,
                this.getX(),
                this.getY() + 1.0D,
                this.getZ(),
                45,
                0.65D,
                0.35D,
                0.65D,
                0.055D
        );
    }

    @Override
    protected void dropCustomDeathLoot(@NotNull ServerLevel level,
                                       @NotNull DamageSource damageSource,
                                       boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);

        this.spawnAtLocation(new ItemStack(ModItems.SCULK_CLEAVER.get()));
    }

    private boolean isValidExecutionerTarget(LivingEntity entity) {
        return entity.isAlive()
                && entity != this
                && !SculkFactionHelper.isWildSculkAlly(entity);
    }

    @Override
    public boolean isAlliedTo(@NotNull Entity entity) {
        return super.isAlliedTo(entity) || SculkFactionHelper.isWildSculkAlly(entity);
    }

    @Override
    public boolean canAttack(@NotNull LivingEntity target) {
        if (SculkFactionHelper.isWildSculkAlly(target)) {
            return false;
        }

        return super.canAttack(target);
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (target != null && SculkFactionHelper.isWildSculkAlly(target)) {
            super.setTarget(null);
            return;
        }

        super.setTarget(target);
    }

    @Override
    protected @NotNull SoundEvent getAmbientSound() {
        return SoundEvents.WARDEN_AMBIENT;
    }

    @Override
    protected @NotNull SoundEvent getHurtSound( @NotNull DamageSource damageSource) {
        return SoundEvents.WARDEN_HURT;
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return SoundEvents.WARDEN_DEATH;
    }
}