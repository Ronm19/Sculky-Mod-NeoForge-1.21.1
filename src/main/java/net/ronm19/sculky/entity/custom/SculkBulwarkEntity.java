package net.ronm19.sculky.entity.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.ronm19.sculky.item.ModItems;
import net.ronm19.sculky.util.SculkFactionHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SculkBulwarkEntity extends Monster {
    private static final EntityDataAccessor<Integer> BASH_ANIMATION_TICKS =
            SynchedEntityData.defineId(SculkBulwarkEntity.class, EntityDataSerializers.INT);

    private static final int BASH_ANIMATION_LENGTH = 20;
    private static final int SHIELD_BASH_COOLDOWN = 20 * 6;
    private static final int PROTECTION_AURA_INTERVAL = 40;

    private static final double SHIELD_BASH_RANGE = 4.2D;
    private static final double SHIELD_BASH_RADIUS = 3.2D;
    private static final double PROTECTION_AURA_RADIUS = 8.0D;

    private static final float SHIELD_BASH_DAMAGE = 6.0F;
    private static final double SHIELD_BASH_KNOCKBACK = 1.85D;

    private static final float FRONTAL_DAMAGE_MULTIPLIER = 0.35F;

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState bashAnimationState = new AnimationState();

    private int idleAnimationTimeout = 0;
    private int shieldBashCooldown = 20 * 3;

    public SculkBulwarkEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 30;
        this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(ModItems.SCULK_SHIELD.get()));
        this.setDropChance(EquipmentSlot.OFFHAND, 0.0F);
    }

    public static AttributeSupplier.Builder createSculkBulwarkAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 95.0D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.ARMOR, 12.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 4.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.18D)
                .add(Attributes.FOLLOW_RANGE, 30.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.9D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 0.85D, false));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 0.45D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());

        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(
                this,
                Player.class,
                10,
                true,
                false,
                this::canAttackTarget
        ));

        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(
                this,
                AbstractVillager.class,
                10,
                true,
                false,
                this::canAttackTarget
        ));

        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(
                this,
                IronGolem.class,
                10,
                true,
                false,
                this::canAttackTarget
        ));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(BASH_ANIMATION_TICKS, 0);
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
            this.idleAnimationTimeout = this.random.nextInt(40) + 80;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }

        if (this.getBashAnimationTicks() > 0) {
            this.bashAnimationState.startIfStopped(this.tickCount);
        } else {
            this.bashAnimationState.stop();
        }
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        if (this.shieldBashCooldown > 0) {
            this.shieldBashCooldown--;
        }

        if (this.getBashAnimationTicks() > 0) {
            this.setBashAnimationTicks(this.getBashAnimationTicks() - 1);
        }

        LivingEntity target = this.getTarget();

        if (target != null
                && this.canAttackTarget(target)
                && this.distanceToSqr(target) <= SHIELD_BASH_RANGE * SHIELD_BASH_RANGE
                && this.shieldBashCooldown <= 0) {
            this.shieldBash(target);
            this.shieldBashCooldown = SHIELD_BASH_COOLDOWN;
        }

        if (this.tickCount % PROTECTION_AURA_INTERVAL == 0) {
            this.applyProtectionAura();
        }
    }

    @Override
    protected void dropCustomDeathLoot(@NotNull ServerLevel level,
                                       @NotNull DamageSource damageSource,
                                       boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);

        this.spawnAtLocation(new ItemStack(ModItems.SCULK_SHIELD.get()));
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if (amount > 0.0F && this.isBlockingFromFront(source)) {
            amount *= FRONTAL_DAMAGE_MULTIPLIER;

            Entity attacker = source.getEntity();

            if (attacker instanceof LivingEntity livingAttacker) {
                livingAttacker.knockback(
                        0.45D,
                        this.getX() - livingAttacker.getX(),
                        this.getZ() - livingAttacker.getZ()
                );
            }

            if (!this.level().isClientSide()) {
                this.level().playSound(
                        null,
                        this.blockPosition(),
                        SoundEvents.SHIELD_BLOCK,
                        SoundSource.HOSTILE,
                        1.0F,
                        0.72F
                );

                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(
                            ParticleTypes.SCULK_SOUL,
                            this.getX(),
                            this.getY() + 1.5D,
                            this.getZ(),
                            10,
                            0.45D,
                            0.45D,
                            0.45D,
                            0.025D
                    );
                }
            }
        }

        return super.hurt(source, amount);
    }

    private boolean isBlockingFromFront(DamageSource source) {
        Entity sourceEntity = source.getDirectEntity() != null ? source.getDirectEntity() : source.getEntity();

        if (sourceEntity == null) {
            return false;
        }

        Vec3 toSource = sourceEntity.position().subtract(this.position());
        Vec3 flatToSource = new Vec3(toSource.x, 0.0D, toSource.z);

        if (flatToSource.lengthSqr() < 0.0001D) {
            return false;
        }

        Vec3 look = this.getLookAngle();
        Vec3 flatLook = new Vec3(look.x, 0.0D, look.z);

        if (flatLook.lengthSqr() < 0.0001D) {
            return false;
        }

        double dot = flatLook.normalize().dot(flatToSource.normalize());

        // Positive dot means the attacker/projectile is in front of the Bulwark.
        return dot > 0.25D;
    }

    private void shieldBash(LivingEntity mainTarget) {
        this.triggerBashAnimation();

        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        AABB area = this.getBoundingBox().inflate(SHIELD_BASH_RADIUS);

        List<LivingEntity> targets = serverLevel.getEntitiesOfClass(
                LivingEntity.class,
                area,
                this::canAttackTarget
        );

        for (LivingEntity target : targets) {
            if (target.distanceToSqr(this) > SHIELD_BASH_RADIUS * SHIELD_BASH_RADIUS) {
                continue;
            }

            target.hurt(this.damageSources().mobAttack(this), SHIELD_BASH_DAMAGE);

            target.knockback(
                    SHIELD_BASH_KNOCKBACK,
                    this.getX() - target.getX(),
                    this.getZ() - target.getZ()
            );

            target.setDeltaMovement(target.getDeltaMovement().add(0.0D, 0.16D, 0.0D));
        }

        serverLevel.playSound(
                null,
                this.blockPosition(),
                SoundEvents.WARDEN_ATTACK_IMPACT,
                SoundSource.HOSTILE,
                1.2F,
                0.58F
        );

        serverLevel.playSound(
                null,
                this.blockPosition(),
                SoundEvents.SHIELD_BLOCK,
                SoundSource.HOSTILE,
                0.9F,
                0.6F
        );

        serverLevel.sendParticles(
                ParticleTypes.SCULK_SOUL,
                this.getX(),
                this.getY() + 1.0D,
                this.getZ(),
                35,
                0.85D,
                0.35D,
                0.85D,
                0.045D
        );

        serverLevel.sendParticles(
                ParticleTypes.SONIC_BOOM,
                mainTarget.getX(),
                mainTarget.getY() + 0.8D,
                mainTarget.getZ(),
                1,
                0.0D,
                0.0D,
                0.0D,
                0.0D
        );
    }

    private void applyProtectionAura() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        AABB area = this.getBoundingBox().inflate(PROTECTION_AURA_RADIUS);

        List<LivingEntity> allies = serverLevel.getEntitiesOfClass(
                LivingEntity.class,
                area,
                this::isProtectedAlly
        );

        for (LivingEntity ally : allies) {
            ally.addEffect(new MobEffectInstance(
                    MobEffects.DAMAGE_RESISTANCE,
                    60,
                    0,
                    true,
                    true
            ));
        }

        if (!allies.isEmpty()) {
            serverLevel.sendParticles(
                    ParticleTypes.SCULK_SOUL,
                    this.getX(),
                    this.getY() + 1.4D,
                    this.getZ(),
                    8,
                    0.7D,
                    0.35D,
                    0.7D,
                    0.015D
            );
        }
    }

    private boolean isProtectedAlly(LivingEntity entity) {
        if (entity == this) {
            return false;
        }

        if (!entity.isAlive()) {
            return false;
        }

        // The King should not need Bulwark protection.
        if (entity instanceof SculkKingEntity) {
            return false;
        }

        return SculkFactionHelper.isWildSculkAlly(entity);
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity target) {
        this.triggerBashAnimation();

        boolean hurt = super.doHurtTarget(target);

        if (hurt && target instanceof LivingEntity livingTarget) {
            livingTarget.knockback(
                    0.95D,
                    this.getX() - livingTarget.getX(),
                    this.getZ() - livingTarget.getZ()
            );

            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(
                        ParticleTypes.SCULK_SOUL,
                        livingTarget.getX(),
                        livingTarget.getY() + 0.8D,
                        livingTarget.getZ(),
                        10,
                        0.25D,
                        0.25D,
                        0.25D,
                        0.025D
                );
            }

            this.playSound(SoundEvents.WARDEN_ATTACK_IMPACT, 0.8F, 0.75F);
        }

        return hurt;
    }

    private boolean canAttackTarget(@Nullable LivingEntity target) {
        if (target == null) {
            return false;
        }

        if (!target.isAlive()) {
            return false;
        }

        if (target == this) {
            return false;
        }

        if (SculkFactionHelper.isWildSculkAlly(target)) {
            return false;
        }

        return true;
    }

    @Override
    public boolean canAttack(@NotNull LivingEntity target) {
        if (!this.canAttackTarget(target)) {
            return false;
        }

        return super.canAttack(target);
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (target != null && !this.canAttackTarget(target)) {
            super.setTarget(null);
            return;
        }

        super.setTarget(target);
    }

    @Override
    public boolean isAlliedTo(@NotNull Entity entity) {
        return super.isAlliedTo(entity) || SculkFactionHelper.isWildSculkAlly(entity);
    }

    private void triggerBashAnimation() {
        this.setBashAnimationTicks(BASH_ANIMATION_LENGTH);
    }

    public int getBashAnimationTicks() {
        return this.entityData.get(BASH_ANIMATION_TICKS);
    }

    private void setBashAnimationTicks(int ticks) {
        this.entityData.set(BASH_ANIMATION_TICKS, Mth.clamp(ticks, 0, BASH_ANIMATION_LENGTH));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.WARDEN_AMBIENT;
    }

    @Override
    protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return SoundEvents.SCULK_CATALYST_BREAK;
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return SoundEvents.WARDEN_DEATH;
    }
}