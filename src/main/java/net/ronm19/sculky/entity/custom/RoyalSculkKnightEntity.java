package net.ronm19.sculky.entity.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class RoyalSculkKnightEntity extends Monster {

    private static final EntityDataAccessor<Boolean> ENRAGED =
            SynchedEntityData.defineId(RoyalSculkKnightEntity.class, EntityDataSerializers.BOOLEAN);

    // Normal stats
    private static final double NORMAL_MAX_HEALTH = 60.0D;
    private static final double NORMAL_MOVEMENT_SPEED = 0.22D;
    private static final double NORMAL_ATTACK_DAMAGE = 8.0D;
    private static final double NORMAL_ARMOR = 10.0D;
    private static final double NORMAL_ARMOR_TOUGHNESS = 4.0D;
    private static final double NORMAL_KNOCKBACK_RESISTANCE = 0.65D;
    private static final double NORMAL_FOLLOW_RANGE = 24.0D;

    // Enraged stats
    private static final double ENRAGED_MOVEMENT_SPEED = 0.28D;
    private static final double ENRAGED_ATTACK_DAMAGE = 11.0D;
    private static final double ENRAGED_ARMOR = 12.0D;

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    public RoyalSculkKnightEntity( EntityType<? extends Monster> entityType, Level level ) {
        super(entityType, level);
        this.xpReward = 20;
    }

    public static AttributeSupplier.Builder createRoyalSculkKnightAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, NORMAL_MAX_HEALTH)
                .add(Attributes.MOVEMENT_SPEED, NORMAL_MOVEMENT_SPEED)
                .add(Attributes.ATTACK_DAMAGE, NORMAL_ATTACK_DAMAGE)
                .add(Attributes.ARMOR, NORMAL_ARMOR)
                .add(Attributes.ARMOR_TOUGHNESS, NORMAL_ARMOR_TOUGHNESS)
                .add(Attributes.KNOCKBACK_RESISTANCE, NORMAL_KNOCKBACK_RESISTANCE)
                .add(Attributes.FOLLOW_RANGE, NORMAL_FOLLOW_RANGE);
    }

    @Override
    protected void defineSynchedData( SynchedEntityData.Builder builder ) {
        super.defineSynchedData(builder);
        builder.define(ENRAGED, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            checkEnragedPhase();
            setupAnimationStates();
        }
    }

    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = this.random.nextInt(40) + 80;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }
    }

    private void checkEnragedPhase() {
        if (!this.isEnraged() && this.getHealth() <= this.getMaxHealth() * 0.5F) {
            this.setEnraged(true);
        }
    }

    public boolean isEnraged() {
        return this.entityData.get(ENRAGED);
    }

    public void setEnraged( boolean enraged ) {
        boolean wasEnraged = this.isEnraged();
        this.entityData.set(ENRAGED, enraged);

        if (!this.level().isClientSide && enraged && !wasEnraged) {
            onEnterEnragedState();
        }

        if (!this.level().isClientSide && !enraged) {
            applyNormalStats();
        }
    }

    private void onEnterEnragedState() {
        applyEnragedStats();
        this.heal(4.0F);
        this.playSound(SoundEvents.RAVAGER_ROAR, 1.2F, 0.85F);
    }

    private void applyNormalStats() {
        setAttributeBase(Attributes.MOVEMENT_SPEED, NORMAL_MOVEMENT_SPEED);
        setAttributeBase(Attributes.ATTACK_DAMAGE, NORMAL_ATTACK_DAMAGE);
        setAttributeBase(Attributes.ARMOR, NORMAL_ARMOR);
    }

    private void applyEnragedStats() {
        setAttributeBase(Attributes.MOVEMENT_SPEED, ENRAGED_MOVEMENT_SPEED);
        setAttributeBase(Attributes.ATTACK_DAMAGE, ENRAGED_ATTACK_DAMAGE);
        setAttributeBase(Attributes.ARMOR, ENRAGED_ARMOR);
    }

    private void setAttributeBase( net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute, double value ) {
        AttributeInstance instance = this.getAttribute(attribute);
        if (instance != null) {
            instance.setBaseValue(value);
        }
    }

    @Override
    public void addAdditionalSaveData( CompoundTag tag ) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Enraged", this.isEnraged());
    }

    @Override
    public void readAdditionalSaveData( CompoundTag tag ) {
        super.readAdditionalSaveData(tag);

        if (tag.contains("Enraged")) {
            boolean enraged = tag.getBoolean("Enraged");
            this.entityData.set(ENRAGED, enraged);

            if (enraged) {
                applyEnragedStats();
            } else {
                applyNormalStats();
            }
        }
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean success = super.doHurtTarget(target);

        if (success) {
            this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, this.isEnraged() ? 0.85F : 0.95F);

            if (this.isEnraged() && target instanceof LivingEntity livingTarget) {
                livingTarget.knockback(0.7F, livingTarget.getX() - this.getX(), livingTarget.getZ() - this.getZ());
            }
        }

        return success;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.WITHER_SKELETON_AMBIENT;
    }

    @Override
    protected @NotNull SoundEvent getHurtSound( @NotNull DamageSource damageSource) {
        return SoundEvents.WITHER_SKELETON_HURT;
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return SoundEvents.WITHER_SKELETON_DEATH;
    }

    @Override
    protected void playStepSound( @NotNull BlockPos pos, @NotNull BlockState blockState) {
        this.playSound(SoundEvents.NETHERITE_BLOCK_STEP, 0.9F, 0.85F);
    }
}