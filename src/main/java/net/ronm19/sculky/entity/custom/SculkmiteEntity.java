package net.ronm19.sculky.entity.custom;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.ronm19.sculky.block.ModBlocks;
import net.ronm19.sculky.entity.variant.CorruptedSculkEndermanVariant;
import net.ronm19.sculky.entity.variant.SculkmiteVariant;
import net.ronm19.sculky.util.ModTags;
import net.ronm19.sculky.worldgen.biome.ModBiomes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class SculkmiteEntity extends Monster implements Enemy {

    /* ========================= DATA ========================= */

    public static final EntityDataAccessor<Integer> VARIANT =
            SynchedEntityData.defineId(SculkmiteEntity.class, EntityDataSerializers.INT);

    public SculkmiteEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    /* ========================= SYNC ========================= */

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(VARIANT, 0);
    }

    private int getTypeVariant() {
        return this.entityData.get(VARIANT);
    }

    public SculkmiteVariant getVariant() {
        return SculkmiteVariant.byId(this.getTypeVariant() & 255);
    }

    private void setVariant(SculkmiteVariant variant) {
        this.entityData.set(VARIANT, variant.getId() & 255);
    }

    public boolean isKing() {
        return getVariant() == SculkmiteVariant.KING;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Variant", this.getTypeVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.entityData.set(VARIANT, pCompound.getInt("Variant"));
    }

        /* ========================= ATTRIBUTES ========================= */

    public static AttributeSupplier.Builder createSculkmiteAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.FOLLOW_RANGE, 16.0D);
    }

    private void applyKingStats() {
        Objects.requireNonNull(getAttribute(Attributes.MAX_HEALTH)).setBaseValue(40.0D);
        Objects.requireNonNull(getAttribute(Attributes.ATTACK_DAMAGE)).setBaseValue(6.0D);
        Objects.requireNonNull(getAttribute(Attributes.MOVEMENT_SPEED)).setBaseValue(0.23D);
        Objects.requireNonNull(getAttribute(Attributes.KNOCKBACK_RESISTANCE)).setBaseValue(0.6D);
        setHealth(40.0F);
    }

    /* ========================= AI ========================= */

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new FloatGoal(this));
        goalSelector.addGoal(2, new FollowSculkmiteKingGoal(this, 1.1D, 10.0F, 3.0F));
        goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.1D, true));
        goalSelector.addGoal(4, new RandomStrollGoal(this, 1.0D));
        goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    /* ========================= TICK ========================= */

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide && tickCount % 40 == 0) {
            trySpreadSculk();
            if (isKing()) commandNearbyMites();
        }
    }

    private void commandNearbyMites() {
        List<SculkmiteEntity> mites = level().getEntitiesOfClass(
                SculkmiteEntity.class,
                getBoundingBox().inflate(12.0D),
                mite -> !mite.isKing()
        );

        for (SculkmiteEntity mite : mites) {
            mite.setTarget(getTarget());
        }
    }

    /* ========================= SCULK SPREAD ========================= */

    private void trySpreadSculk() {
        BlockPos feet = blockPosition();
        BlockPos below = feet.below();

        BlockState belowState = level().getBlockState(below);
        BlockState aboveState = level().getBlockState(feet);

        if (!level().getFluidState(below).isEmpty()) return;
        if (belowState.is(Blocks.BEDROCK)) return;

        // Standing on infested sculk
        if (belowState.is(ModBlocks.INFESTED_SCULK_GRASS_BLOCK.get())
                || belowState.is(ModBlocks.INFESTED_SCULK_DIRT_BLOCK.get())) {

            if (aboveState.isAir()) {
                level().setBlock(feet,
                        ModBlocks.SCULKBLOOM.get().defaultBlockState(),
                        Block.UPDATE_ALL);
            }
            return;
        }

        // Normal blocks
        if (belowState.is(ModTags.Blocks.SCULK_SPREADABLE)) {
            float roll = random.nextFloat();

            if (roll < 0.75F) {
                level().setBlock(below,
                        Blocks.SCULK.defaultBlockState(),
                        Block.UPDATE_ALL);
            } else if (roll < 0.90F) {
                level().setBlock(below,
                        ModBlocks.INFESTED_SCULK_GRASS_BLOCK.get().defaultBlockState(),
                        Block.UPDATE_ALL);
            }
        }
    }

    /* ========================= SPAWN ========================= */

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level,
                                        DifficultyInstance difficulty,
                                        MobSpawnType spawnType,
                                        @Nullable SpawnGroupData spawnData) {

        // Try to spawn KING first
        if (isInSculkForest(level)
                && random.nextFloat() < 0.05F
                && !hasNearbyKing(level)) {

            setVariant(SculkmiteVariant.KING);
            applyKingStats();

        } else {
            // Normal variants
            SculkmiteVariant variant =
                    Util.getRandom(SculkmiteVariant.values(), this.random);

            // Safety: never randomly pick KING
            if (variant == SculkmiteVariant.KING) {
                variant = SculkmiteVariant.DEFAULT; // or another base variant
            }

            setVariant(variant);
        }

        return super.finalizeSpawn(level, difficulty, spawnType, spawnData);
    }


    private boolean isInSculkForest(ServerLevelAccessor level) {
        return level.getBiome(blockPosition()).is(ModBiomes.SCULK_FOREST);
    }

    private boolean hasNearbyKing(ServerLevelAccessor level) {
        return !level.getEntitiesOfClass(
                SculkmiteEntity.class,
                getBoundingBox().inflate(32.0D),
                SculkmiteEntity::isKing
        ).isEmpty();
    }

    /* ========================= MISC ========================= */

    @Override
    public boolean canBeLeashed() {
        return false;
    }

    /* ========================= INNER GOAL ========================= */

    static class FollowSculkmiteKingGoal extends Goal {

        private final SculkmiteEntity mite;
        private final double speed;
        private final float maxDist;
        private final float minDist;
        private SculkmiteEntity king;
        private int recalc;

        public FollowSculkmiteKingGoal(SculkmiteEntity mite, double speed, float maxDist, float minDist) {
            this.mite = mite;
            this.speed = speed;
            this.maxDist = maxDist;
            this.minDist = minDist;
        }

        @Override
        public boolean canUse() {
            if (mite.isKing()) return false;

            List<SculkmiteEntity> kings = mite.level().getEntitiesOfClass(
                    SculkmiteEntity.class,
                    mite.getBoundingBox().inflate(16.0D),
                    SculkmiteEntity::isKing
            );

            if (kings.isEmpty()) return false;
            king = kings.get(0);
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return king != null && king.isAlive()
                    && mite.distanceToSqr(king) > minDist * minDist
                    && mite.distanceToSqr(king) < maxDist * maxDist;
        }

        @Override
        public void start() {
            recalc = 0;
        }

        @Override
        public void stop() {
            king = null;
        }

        @Override
        public void tick() {
            if (--recalc <= 0) {
                recalc = 10;
                mite.getNavigation().moveTo(king, speed);
            }
        }
    }

    /* ========================= SOUNDS ========================= */

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENDERMITE_AMBIENT;
    }

    @Override
    protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource src) {
        return SoundEvents.SCULK_BLOCK_BREAK;
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return SoundEvents.ENDERMITE_DEATH;
    }
}
