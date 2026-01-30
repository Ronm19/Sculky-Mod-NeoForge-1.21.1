package net.ronm19.sculky.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.ronm19.sculky.effect.ModEffects;
import net.ronm19.sculky.entity.variant.CorruptedSculkSkeletonVariant;
import net.ronm19.sculky.item.ModItems;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;

import static net.ronm19.sculky.entity.custom.SculkZombieEntity.shouldSpawnCorrupted;

public class SculkSkeletonEntity extends WitherSkeleton {

    public static final EntityDataAccessor<Integer> CORRUPTED =
            SynchedEntityData.defineId(SculkSkeletonEntity.class, EntityDataSerializers.INT);

    public SculkSkeletonEntity(EntityType<? extends WitherSkeleton> type, Level level) {
        super(type, level);
    }

    /* ============================= */
    /*        CORE BEHAVIOR          */
    /* ============================= */

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);

        if (hit && this.getVariant() == CorruptedSculkSkeletonVariant.CORRUPTED) {
            if (target instanceof LivingEntity living) {

                if (this.random.nextFloat() < 0.35F) {
                    living.addEffect(new MobEffectInstance(
                            MobEffects.WITHER,
                            80, // 4 seconds
                            0
                    ));
                }

                if (this.random.nextFloat() < 0.25F) {
                    living.addEffect(new MobEffectInstance(
                            ModEffects.SCULK_INFECTION_EFFECT.getDelegate(),
                            100,
                            0
                    ));
                }
            }
        }

        return hit;
    }


    @Override
    protected boolean isSunBurnTick() {
        return false;
    }

    /* ============================= */
    /*        EQUIPMENT              */
    /* ============================= */

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND,
                new ItemStack(ModItems.INFESTED_SCULK_SWORD.get()));
    }

    @Override
    protected void populateDefaultEquipmentEnchantments(ServerLevelAccessor level, RandomSource random, DifficultyInstance difficulty) {
        // Intentionally empty
    }

    @Override
    public SpawnGroupData finalizeSpawn(
            ServerLevelAccessor level,
            DifficultyInstance difficulty,
            MobSpawnType spawnType,
            @Nullable SpawnGroupData spawnData
    ) {
        spawnData = super.finalizeSpawn(level, difficulty, spawnType, spawnData);

        boolean corrupted;

        if (spawnType == MobSpawnType.SPAWN_EGG) {
            corrupted = this.random.nextFloat() < 0.6F;
        } else {
            corrupted = shouldSpawnCorrupted(
                    level,
                    this.blockPosition(),
                    level.getRandom(),
                    difficulty
            );
        }

        if (corrupted) {
            this.setVariant(CorruptedSculkSkeletonVariant.CORRUPTED);
            applyCorruptedAttributes();
        } else {
            this.setVariant(CorruptedSculkSkeletonVariant.NORMAL);
        }

        return spawnData;
    }

    public static boolean shouldSpawnCorrupted(
            ServerLevelAccessor level,
            BlockPos pos,
            RandomSource random,
            DifficultyInstance difficulty
    ) {
        float baseChance;

        // Surface bias
        if (pos.getY() >= 60) {
            baseChance = 0.12F; // noticeable but not spammy
        }
        // Underground (rare but possible)
        else {
            baseChance = 0.04F;
        }

        // Difficulty scaling
        switch (difficulty.getDifficulty()) {
            case PEACEFUL -> baseChance = 0.0F;
            case EASY -> baseChance *= 0.6F;
            case NORMAL -> baseChance *= 1.0F;
            case HARD -> baseChance *= 1.4F;
        }

        return random.nextFloat() < baseChance;
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, source, recentlyHit);

        RandomSource random = this.getRandom();

        // ----- SCULK CHITIN (common drop) -----
        int chitinCount = random.nextInt(2) + 1; // 1–2
        this.spawnAtLocation(new ItemStack(ModItems.SCULK_CHITIN.get(), chitinCount));

        // ----- RARE DROP: INFESTED SCULK SWORD -----
        if (random.nextFloat() < 0.05F) { // 5% chance
            this.spawnAtLocation(new ItemStack(ModItems.INFESTED_SCULK_SWORD.get()));
        }
    }


    /* ============================= */
    /*        ATTRIBUTES             */
    /* ============================= */

    public static AttributeSupplier.Builder createSculkSkeletonAttributes() {
        return WitherSkeleton.createAttributes()
                .add(Attributes.MAX_HEALTH, 28.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.24D)
                .add(Attributes.FOLLOW_RANGE, 22.0D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D);
    }


    // ---------------------------------------------------------
    //               DATA
    // ---------------------------------------------------------

    @Override
    protected void defineSynchedData( SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(CORRUPTED, 0);
    }

    @Override
    public void addAdditionalSaveData( CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Variant", this.getTypeVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(CORRUPTED, tag.getInt("Variant"));

        if (getVariant() == CorruptedSculkSkeletonVariant.CORRUPTED) {
            applyCorruptedAttributes();
        }
    }


    /* ============================= */
    /*        VARIANT                */
    /* ============================= */

    private void applyCorruptedAttributes() {
        Objects.requireNonNull(this.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(36.0D);
        Objects.requireNonNull(this.getAttribute(Attributes.ATTACK_DAMAGE)).setBaseValue(9.0D);
        Objects.requireNonNull(this.getAttribute(Attributes.MOVEMENT_SPEED)).setBaseValue(0.30D);
        Objects.requireNonNull(this.getAttribute(Attributes.FOLLOW_RANGE)).setBaseValue(32.0D);
        Objects.requireNonNull(this.getAttribute(Attributes.ARMOR)).setBaseValue(1.0D);

        this.setHealth(this.getMaxHealth());
    }

    private int getTypeVariant() {
        return this.entityData.get(CORRUPTED);
    }

    public CorruptedSculkSkeletonVariant getVariant() {
        return CorruptedSculkSkeletonVariant.byId(this.getTypeVariant() & 255);
    }

    private void setVariant(CorruptedSculkSkeletonVariant variant) {
        this.entityData.set(CORRUPTED, variant.getId() & 255);
    }

    @Override
    protected void registerGoals() {

        /* ============================= */
        /*        ACTION GOALS           */
        /* ============================= */

            this.goalSelector.addGoal(0, new FloatGoal(this));
            this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
            this.goalSelector.addGoal(5, new RandomStrollGoal(this, 0.8D));
            this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
            this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

            /* ============================= */
            /*        TARGET GOALS           */
            /* ============================= */

            this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
            this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
            this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, TamableAnimal.class, true));
        }

        /* ============================= */
        /*        SOUNDS                 */
        /* ============================= */

    @Override
    protected @NotNull SoundEvent getAmbientSound() {
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
    protected void playStepSound( @NotNull BlockPos pos, @NotNull BlockState state) {
        this.playSound(
                SoundEvents.SCULK_BLOCK_STEP,
                0.15F,
                0.8F + this.random.nextFloat() * 0.2F
        );
    }
}
