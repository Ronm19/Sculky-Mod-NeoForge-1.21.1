package net.ronm19.sculky.entity.custom;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
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

    // ---- Tuning knobs (0.5 gameplay knobs) ----
    private static final int BLOOM_RADIUS = 4;
    private static final int BLOOM_CHECK_INTERVAL_TICKS = 20; // 1 second

    private static final int SPREAD_INTERVAL_NORMAL = 60;
    private static final int SPREAD_INTERVAL_NORMAL_NEAR_BLOOM = 45;
    private static final int SPREAD_INTERVAL_KING = 30;
    private static final int SPREAD_INTERVAL_KING_NEAR_BLOOM = 20;

    private static final float KING_SPREAD_BONUS = 0.15F;
    private static final float BLOOM_SPREAD_BONUS = 0.05F;

    // Base chances
    private static final float BASE_SCULK_CHANCE = 0.75F;
    private static final float BASE_GRASS_CHANCE = 0.90F;

    // Caps so bonuses can't make it "always convert"
    private static final float MAX_SCULK_CHANCE = 0.90F;
    private static final float MAX_GRASS_CHANCE = 0.98F;

    private static final float KING_NATURAL_CHANCE = 0.05F;
    private static final double KING_EXCLUSION_RADIUS = 12.0D;

    // For eggs/commands: allow King sometimes (testing-friendly)
    private static final float KING_ARTIFICIAL_ALLOW = 0.25F;

    // Cached bloom proximity
    private boolean cachedNearBloom = false;
    private int bloomCheckCooldown = 0;

    public SculkmiteEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    /* ========================= SYNC ========================= */

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VARIANT, 0);
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

    private boolean isAggressiveVariant() {
        // Later: add more variants here
        return isKing();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Variant", this.getTypeVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(VARIANT, tag.getInt("Variant"));

        // Important: if it loads as KING, re-apply KING stats after world reload
        if (!level().isClientSide && isKing()) {
            applyKingStats();
        }
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

        if (level().isClientSide) return;

        // Update movement speed based on terrain
        updateMoveSpeed();

        // Bloom proximity (cached for performance)
        boolean nearBloom = isNearBloomCached(BLOOM_RADIUS);

        // Spread interval depends on variant + bloom proximity
        int spreadInterval = getSpreadInterval(nearBloom);

        // Optional: King "presence" without spam
        if (isKing() && tickCount % 80 == 0) { // every 4 seconds
            spawnKingAura();
        }

        if (tickCount % spreadInterval == 0) {
            boolean spreadHappened = trySpreadSculk(nearBloom);

            // Pulse effects only when something actually changes
            if (spreadHappened) {
                if (nearBloom) spawnBloomAura();
                if (isKing()) spawnKingAura();
            }

            // Command only if King actually has a target (prevents pointless retarget loops)
            if (isKing() && getTarget() != null) {
                commandNearbyMites();
            }
        }
    }

    private void updateMoveSpeed() {
        AttributeInstance speed = getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) return;

        double base = isOnSculk()
                ? (isKing() ? 0.27D : 0.26D)
                : (isKing() ? 0.23D : 0.25D);

        speed.setBaseValue(base);
    }

    private int getSpreadInterval(boolean nearBloom) {
        if (isKing()) {
            return nearBloom ? SPREAD_INTERVAL_KING_NEAR_BLOOM : SPREAD_INTERVAL_KING;
        }
        return nearBloom ? SPREAD_INTERVAL_NORMAL_NEAR_BLOOM : SPREAD_INTERVAL_NORMAL;
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

    /* ========================= VISUAL FEEDBACK ========================= */

    private void spawnBloomAura() {
        if (!(level() instanceof ServerLevel server)) return;

        server.sendParticles(
                ParticleTypes.SCULK_SOUL,
                getX(), getY() + 0.2D, getZ(),
                2,
                0.25D, 0.15D, 0.25D,
                0.0D
        );

        // Reduced sound spam
        if (random.nextFloat() < 0.05F) {
            level().playSound(
                    null,
                    blockPosition(),
                    SoundEvents.SCULK_BLOCK_SPREAD,
                    SoundSource.HOSTILE,
                    0.2F,
                    1.0F + (random.nextFloat() - 0.5F) * 0.2F
            );
        }
    }

    private void spawnKingAura() {
        if (!(level() instanceof ServerLevel server)) return;

        server.sendParticles(
                ParticleTypes.SCULK_CHARGE_POP,
                getX(), getY() + 0.6D, getZ(),
                3,
                0.35D, 0.25D, 0.35D,
                0.0D
        );

        // Reduced sound spam
        if (random.nextFloat() < 0.03F) {
            level().playSound(
                    null,
                    blockPosition(),
                    SoundEvents.SCULK_SHRIEKER_SHRIEK,
                    SoundSource.HOSTILE,
                    0.15F,
                    1.6F
            );
        }
    }

    /* ========================= SCULK SPREAD ========================= */

    private boolean trySpreadSculk(boolean nearBloom) {
        float spreadBonus = isAggressiveVariant() ? KING_SPREAD_BONUS : 0.0F;
        if (nearBloom) spreadBonus += BLOOM_SPREAD_BONUS;

        BlockPos feet = blockPosition();
        BlockPos below = feet.below();

        BlockState belowState = level().getBlockState(below);
        BlockState aboveState = level().getBlockState(feet);

        if (!level().getFluidState(below).isEmpty()) return false;
        if (belowState.is(Blocks.BEDROCK)) return false;

        // Bloom placement on infested sculk
        if (belowState.is(ModBlocks.INFESTED_SCULK_GRASS_BLOCK.get())
                || belowState.is(ModBlocks.INFESTED_SCULK_DIRT_BLOCK.get())) {

            if (aboveState.isAir()) {
                level().setBlock(feet, ModBlocks.SCULKBLOOM.get().defaultBlockState(), Block.UPDATE_ALL);
                return true;
            }
            return false;
        }

        // Convert normal blocks
        if (belowState.is(ModTags.Blocks.SCULK_SPREADABLE)) {
            float roll = random.nextFloat();

            float sculkChance = Math.min(MAX_SCULK_CHANCE, BASE_SCULK_CHANCE + spreadBonus);
            float grassChance = Math.min(MAX_GRASS_CHANCE, BASE_GRASS_CHANCE + spreadBonus);

            if (roll < sculkChance) {
                level().setBlock(below, Blocks.SCULK.defaultBlockState(), Block.UPDATE_ALL);
                return true;
            } else if (roll < grassChance) {
                level().setBlock(below, ModBlocks.INFESTED_SCULK_GRASS_BLOCK.get().defaultBlockState(), Block.UPDATE_ALL);
                return true;
            }
        }

        return false;
    }

    private boolean isOnSculk() {
        BlockState below = level().getBlockState(blockPosition().below());
        return below.is(Blocks.SCULK)
                || below.is(ModBlocks.INFESTED_SCULK_GRASS_BLOCK.get())
                || below.is(ModBlocks.INFESTED_SCULK_DIRT_BLOCK.get());
    }

    private boolean isNearBloomCached(int radius) {
        if (--bloomCheckCooldown <= 0) {
            bloomCheckCooldown = BLOOM_CHECK_INTERVAL_TICKS;
            cachedNearBloom = isNearBloom(radius);
        }
        return cachedNearBloom;
    }

    private boolean isNearBloom(int radius) {
        BlockPos center = blockPosition();
        for (BlockPos p : BlockPos.betweenClosed(
                center.offset(-radius, -1, -radius),
                center.offset(radius, 1, radius)
        )) {
            if (level().getBlockState(p).is(ModBlocks.SCULKBLOOM.get())) {
                return true;
            }
        }
        return false;
    }

    /* ========================= SPAWN ========================= */

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level,
                                        DifficultyInstance difficulty,
                                        MobSpawnType spawnType,
                                        @Nullable SpawnGroupData spawnData) {

        // Artificial spawns: eggs / commands / dispensers -> random variants, allow King sometimes
        boolean artificial = spawnType == MobSpawnType.SPAWN_EGG
                || spawnType == MobSpawnType.COMMAND
                || spawnType == MobSpawnType.DISPENSER;

        if (artificial) {
            SculkmiteVariant variant = Util.getRandom(SculkmiteVariant.values(), random);

            // Make King rarer for artificial spawns
            if (variant == SculkmiteVariant.KING && random.nextFloat() > KING_ARTIFICIAL_ALLOW) {
                variant = SculkmiteVariant.DEFAULT;
            }

            setVariant(variant);
            if (variant == SculkmiteVariant.KING) applyKingStats();

            return super.finalizeSpawn(level, difficulty, spawnType, spawnData);
        }

        // Natural King spawn: only in Sculk Forest + rare + only one nearby
        if (isInSculkForest(level)
                && random.nextFloat() < KING_NATURAL_CHANCE
                && !hasNearbyKing(level)) {

            setVariant(SculkmiteVariant.KING);
            applyKingStats();

        } else {
            // Natural normal variants: random, but NEVER King
            SculkmiteVariant variant = Util.getRandom(SculkmiteVariant.values(), random);
            if (variant == SculkmiteVariant.KING) variant = SculkmiteVariant.DEFAULT;
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
                getBoundingBox().inflate(KING_EXCLUSION_RADIUS),
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
