package net.ronm19.sculky.entity.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreakDoorGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.minecraft.world.item.enchantment.providers.VanillaEnchantmentProviders;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.ronm19.sculky.item.ModItems;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.function.Predicate;

public class SculkSentryEntity extends Vindicator implements Enemy {
    private static final Predicate<Difficulty> DOOR_BREAKING_PREDICATE =
            difficulty -> difficulty == Difficulty.NORMAL || difficulty == Difficulty.HARD;

    private static final String TAG_GUARD_STANCE_TICKS = "GuardStanceTicks";

    private int guardStanceTicks;

    public SculkSentryEntity(EntityType<? extends SculkSentryEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SculkSentryBreakDoorGoal(this));
        this.goalSelector.addGoal(2, new RaiderOpenDoorGoal(this));
        this.goalSelector.addGoal(3, new HoldGroundAttackGoal(this, 10.0F));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 0.95D, false));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Raider.class).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));

        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.55D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    @Override
    protected void customServerAiStep() {
        if (!this.isNoAi() && GoalUtils.hasGroundPathNavigation(this)) {
            boolean isInRaid = ((ServerLevel) this.level()).isRaided(this.blockPosition());
            ((GroundPathNavigation) this.getNavigation()).setCanOpenDoors(isInRaid);
        }

        super.customServerAiStep();
    }

    public static AttributeSupplier.Builder createSculkSentryAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 38.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.30D)
                .add(Attributes.FOLLOW_RANGE, 18.0D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.ARMOR, 4.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.35D);
    }

    @Override
    public IllagerArmPose getArmPose() {
        if (this.isAggressive()) {
            return IllagerArmPose.ATTACKING;
        }

        return this.isCelebrating() ? IllagerArmPose.CELEBRATING : IllagerArmPose.CROSSED;
    }


    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, source, recentlyHit);

        RandomSource random = this.getRandom();

        int chitin = random.nextInt(2) + 1; // 1–2 chitin
        this.spawnAtLocation(new ItemStack(ModItems.SCULK_CORE.get(), chitin));
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType,
                                        @Nullable SpawnGroupData spawnGroupData) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);

        this.populateDefaultEquipmentSlots(level.getRandom(), difficulty);
        this.populateDefaultEquipmentEnchantments(level, level.getRandom(), difficulty);

        return data;
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.INFESTED_SCULK_SWORD.get()));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    @Override
    public void applyRaidBuffs(ServerLevel level, int wave, boolean unused) {
        ItemStack sword = new ItemStack(ModItems.INFESTED_SCULK_SWORD.get());

        Raid raid = this.getCurrentRaid();
        if (raid != null) {
            boolean shouldEnchant = this.random.nextFloat() <= raid.getEnchantOdds();

            if (shouldEnchant) {
                ResourceKey<EnchantmentProvider> provider =
                        wave > raid.getNumGroups(Difficulty.NORMAL)
                                ? VanillaEnchantmentProviders.RAID_VINDICATOR_POST_WAVE_5
                                : VanillaEnchantmentProviders.RAID_VINDICATOR;

                EnchantmentHelper.enchantItemFromProvider(
                        sword,
                        level.registryAccess(),
                        provider,
                        level.getCurrentDifficultyAt(this.blockPosition()),
                        this.random
                );
            }
        }

        this.setItemSlot(EquipmentSlot.MAINHAND, sword);
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity target) {
        boolean hit = super.doHurtTarget(target);

        if (hit && target instanceof LivingEntity living) {
            if (this.random.nextFloat() < 0.25F) {
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 0), this);
            }

            if (this.random.nextFloat() < 0.12F) {
                living.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60, 0), this);
            }

            this.guardStanceTicks = 60;

            if (!this.level().isClientSide && this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(
                        ParticleTypes.SCULK_SOUL,
                        this.getX(),
                        this.getY() + 1.0D,
                        this.getZ(),
                        6,
                        0.22D,
                        0.30D,
                        0.22D,
                        0.015D
                );
            }
        }

        return hit;
    }


    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide && this.guardStanceTicks > 0) {
            this.guardStanceTicks--;
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt(TAG_GUARD_STANCE_TICKS, this.guardStanceTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.guardStanceTicks = tag.getInt(TAG_GUARD_STANCE_TICKS);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.PILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.PILLAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PILLAGER_DEATH;
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.PILLAGER_CELEBRATE;
    }

    static class SculkSentryBreakDoorGoal extends BreakDoorGoal {
        public SculkSentryBreakDoorGoal(Mob mob) {
            super(mob, 6, SculkSentryEntity.DOOR_BREAKING_PREDICATE);
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            SculkSentryEntity sentry = (SculkSentryEntity) this.mob;
            return sentry.hasActiveRaid()
                    && sentry.random.nextInt(reducedTickDelay(10)) == 0
                    && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            SculkSentryEntity sentry = (SculkSentryEntity) this.mob;
            return sentry.hasActiveRaid() && super.canContinueToUse();
        }

        @Override
        public void start() {
            super.start();
            this.mob.setNoActionTime(0);
        }
    }
}