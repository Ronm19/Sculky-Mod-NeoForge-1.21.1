package net.ronm19.sculky.entity.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.minecraft.world.item.enchantment.providers.VanillaEnchantmentProviders;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.ronm19.sculky.item.ModItems;

import javax.annotation.Nullable;

public class SculkVindicatorEntity extends Vindicator implements Enemy {
    private static final String TAG_SCULK_RAGE_TICKS = "SculkRageTicks";

    private int sculkRageTicks;

    public SculkVindicatorEntity(EntityType<? extends Vindicator> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createSculkVindicatorAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 34.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.37D)
                .add(Attributes.FOLLOW_RANGE, 18.0D)
                .add(Attributes.ATTACK_DAMAGE, 7.0D)
                .add(Attributes.ARMOR, 2.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.15D);
    }

    @Override
    public AbstractIllager.IllagerArmPose getArmPose() {
        if (this.isAggressive()) {
            return IllagerArmPose.ATTACKING;
        }

        return this.isCelebrating() ? IllagerArmPose.CELEBRATING : IllagerArmPose.CROSSED;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType,
                                        @Nullable SpawnGroupData spawnGroupData) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);

        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.INFESTED_SCULK_AXE.get()));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);

        return data;
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.INFESTED_SCULK_AXE.get()));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    @Override
    public void applyRaidBuffs(ServerLevel level, int wave, boolean unused) {
        ItemStack axe = new ItemStack(ModItems.INFESTED_SCULK_AXE.get());

        Raid raid = this.getCurrentRaid();
        if (raid != null && this.random.nextFloat() <= raid.getEnchantOdds()) {
            ResourceKey<EnchantmentProvider> provider =
                    wave > raid.getNumGroups(level.getDifficulty())
                            ? VanillaEnchantmentProviders.RAID_VINDICATOR_POST_WAVE_5
                            : VanillaEnchantmentProviders.RAID_VINDICATOR;

            EnchantmentHelper.enchantItemFromProvider(
                    axe,
                    level.registryAccess(),
                    provider,
                    level.getCurrentDifficultyAt(this.blockPosition()),
                    this.random
            );
        }

        this.setItemSlot(EquipmentSlot.MAINHAND, axe);
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);

        if (hit && !this.level().isClientSide && this.level() instanceof ServerLevel level && target instanceof LivingEntity living) {
            if (this.random.nextFloat() < 0.25F) {
                living.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 80, 0), this);
            }

            if (this.random.nextFloat() < 0.18F) {
                living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0), this);
            }

            level.sendParticles(
                    ParticleTypes.SCULK_SOUL,
                    this.getX(),
                    this.getY() + 1.0D,
                    this.getZ(),
                    8,
                    0.25D,
                    0.35D,
                    0.25D,
                    0.02D
            );
        }

        return hit;
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide && this.sculkRageTicks > 0) {
            this.sculkRageTicks--;
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt(TAG_SCULK_RAGE_TICKS, this.sculkRageTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.sculkRageTicks = tag.getInt(TAG_SCULK_RAGE_TICKS);
    }

    @Override
    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.VINDICATOR_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.VINDICATOR_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.VINDICATOR_DEATH;
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.VINDICATOR_CELEBRATE;
    }
}