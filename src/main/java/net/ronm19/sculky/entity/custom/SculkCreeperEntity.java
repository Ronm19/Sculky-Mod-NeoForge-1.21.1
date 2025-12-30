package net.ronm19.sculky.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
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
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SculkShriekerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.ronm19.sculky.entity.variant.CorruptedSculkCreeperVariant;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;

public class SculkCreeperEntity extends Creeper implements Enemy {

    /* ------------------------------------------------ */
    /* CONSTANTS                                        */
    /* ------------------------------------------------ */

    private static final float EXPLOSION_POWER = 3.5F;
    private static final float CHARGED_EXPLOSION_POWER = 5.5F;

    private static final int DARKNESS_TICKS = 200;
    private static final int CHARGED_DARKNESS_TICKS = 300;

    private static final int SCULK_RADIUS = 3;
    private static final int CHARGED_SCULK_RADIUS = 5;

    /* ------------------------------------------------ */
    /* ENTITY DATA                                      */
    /* ------------------------------------------------ */

    public static final EntityDataAccessor<Integer> CORRUPTED =
            SynchedEntityData.defineId(SculkCreeperEntity.class, EntityDataSerializers.INT);

    /* ------------------------------------------------ */
    /* CONSTRUCTOR                                      */
    /* ------------------------------------------------ */

    public SculkCreeperEntity(EntityType<? extends Creeper> type, Level level) {
        super(type, level);
    }

    /* ------------------------------------------------ */
    /* ATTRIBUTES                                       */
    /* ------------------------------------------------ */

    public static AttributeSupplier.Builder createSculkCreeperAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 24.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.27D)
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.ARMOR, 2.0D);
    }

    /* ------------------------------------------------ */
    /* AI GOALS                                         */
    /* ------------------------------------------------ */

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SwellGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, TamableAnimal.class, true));
    }

    /* ------------------------------------------------ */
    /* EXPLOSION (VANILLA SAFE)                          */
    /* ------------------------------------------------ */

    protected void explodeCreeper() {
        if (!(this.level() instanceof ServerLevel level)) return;

        boolean charged = this.isPowered();
        boolean corrupted = this.getVariant() == CorruptedSculkCreeperVariant.CORRUPTED;

        float power = charged
                ? (corrupted ? CHARGED_EXPLOSION_POWER + 1.0F : CHARGED_EXPLOSION_POWER)
                : (corrupted ? EXPLOSION_POWER + 0.8F : EXPLOSION_POWER);

        int sculkRadius = charged
                ? (corrupted ? CHARGED_SCULK_RADIUS + 2 : CHARGED_SCULK_RADIUS)
                : (corrupted ? SCULK_RADIUS + 1 : SCULK_RADIUS);

        BlockPos center = this.blockPosition();

        // 🌑 Effects BEFORE explosion
        applyInstantDarkness(level, center, charged || corrupted);
        spawnDarknessCloud(level, charged || corrupted);

        // 💥 Vanilla explosion
        level.explode(this, this.getX(), this.getY(), this.getZ(), power, Level.ExplosionInteraction.MOB);

        // 🪨 Aftermath
        spreadSculkAroundCrater(level, center, sculkRadius);
        tryPlaceShrieker(level, center, charged || corrupted);
        tryPlaceWardenShrieker(level, center, charged || corrupted);

        this.discard();
    }

    /* ------------------------------------------------ */
    /* DARKNESS                                         */
    /* ------------------------------------------------ */

    private void applyInstantDarkness(ServerLevel level, BlockPos center, boolean strong) {
        int duration = strong ? CHARGED_DARKNESS_TICKS : DARKNESS_TICKS;

        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, new AABB(center).inflate(6))) {
            if (!isSculkMob(entity)) {
                entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, duration, 0, true, true));
            }
        }
    }

    private void spawnDarknessCloud(ServerLevel level, boolean strong) {
        AreaEffectCloud cloud = new AreaEffectCloud(level, getX(), getY(), getZ());
        cloud.setRadius(strong ? 4.5F : 3.5F);
        cloud.setDuration(strong ? 160 : 120);
        cloud.addEffect(new MobEffectInstance(MobEffects.DARKNESS, strong ? 100 : 60, 0));
        level.addFreshEntity(cloud);
    }

    /* ------------------------------------------------ */
    /* SCULK SPREAD                                     */
    /* ------------------------------------------------ */

    private void spreadSculkAroundCrater(ServerLevel level, BlockPos center, int radius) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                pos.set(center.getX() + dx, center.getY(), center.getZ() + dz);

                if (!level.getBlockState(pos).isAir()) continue;

                BlockState below = level.getBlockState(pos.below());
                if (below.is(Blocks.STONE) || below.is(Blocks.DEEPSLATE)
                        || below.is(Blocks.DIRT) || below.is(Blocks.GRASS_BLOCK)) {

                    if (level.random.nextFloat() < 0.55F) {
                        level.setBlock(pos, Blocks.SCULK.defaultBlockState(), 3);
                    }
                }
            }
        }
    }

    /* ------------------------------------------------ */
    /* SHRIEKERS                                        */
    /* ------------------------------------------------ */

    private void tryPlaceShrieker(ServerLevel level, BlockPos center, boolean strong) {
        if (level.random.nextFloat() > (strong ? 0.35F : 0.15F)) return;

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < 20; i++) {
            pos.setWithOffset(center,
                    level.random.nextInt(-4, 5),
                    level.random.nextInt(-2, 3),
                    level.random.nextInt(-4, 5));

            if (level.getBlockState(pos).isAir() && level.getBlockState(pos.below()).is(Blocks.SCULK)) {
                level.setBlock(pos, Blocks.SCULK_SHRIEKER.defaultBlockState(), 3);
                break;
            }
        }
    }

    private void tryPlaceWardenShrieker(ServerLevel level, BlockPos center, boolean strong) {
        if (level.random.nextFloat() > (strong ? 0.35F : 0.15F)) return;

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < 25; i++) {
            pos.setWithOffset(center,
                    level.random.nextInt(-5, 6),
                    level.random.nextInt(-2, 3),
                    level.random.nextInt(-5, 6));

            if (level.getBlockState(pos).isAir() && level.getBlockState(pos.below()).is(Blocks.SCULK)) {
                level.setBlock(pos,
                        Blocks.SCULK_SHRIEKER.defaultBlockState()
                                .setValue(SculkShriekerBlock.CAN_SUMMON, true),
                        3);
                break;
            }
        }
    }

    /* ------------------------------------------------ */
    /* VARIANTS                                         */
    /* ------------------------------------------------ */

    public CorruptedSculkCreeperVariant getVariant() {
        return CorruptedSculkCreeperVariant.byId(this.entityData.get(CORRUPTED));
    }

    private void setVariant(CorruptedSculkCreeperVariant variant) {
        this.entityData.set(CORRUPTED, variant.getId());
    }

    private void applyCorruptedAttributes() {
        Objects.requireNonNull(getAttribute(Attributes.MAX_HEALTH)).setBaseValue(38.0D);
        Objects.requireNonNull(getAttribute(Attributes.MOVEMENT_SPEED)).setBaseValue(0.26D);
        Objects.requireNonNull(getAttribute(Attributes.FOLLOW_RANGE)).setBaseValue(32.0D);
        Objects.requireNonNull(getAttribute(Attributes.ARMOR)).setBaseValue(3.0D);
        this.setHealth(this.getMaxHealth());
    }

    @Override
    public SpawnGroupData finalizeSpawn(
            @NotNull ServerLevelAccessor level,
            @NotNull DifficultyInstance difficulty,
            @NotNull MobSpawnType spawnType,
            @Nullable SpawnGroupData spawnData
    ) {
        spawnData = super.finalizeSpawn(level, difficulty, spawnType, spawnData);

        boolean corrupted = decideCorruption(level, difficulty, spawnType);

        this.setVariant(
                corrupted
                        ? CorruptedSculkCreeperVariant.CORRUPTED
                        : CorruptedSculkCreeperVariant.NORMAL
        );

        if (corrupted) {
            applyCorruptedAttributes();
        }

        return spawnData;
    }

    /* ------------------------------------------------ */
    /* CORRUPTION LOGIC                                  */
    /* ------------------------------------------------ */

    private boolean decideCorruption(
            ServerLevelAccessor level,
            DifficultyInstance difficulty,
            MobSpawnType spawnType
    ) {
        // Spawn eggs → fixed low chance
        if (spawnType == MobSpawnType.SPAWN_EGG) {
            return this.random.nextFloat() < 0.15F;
        }

        // Natural spawning → environment based
        return shouldSpawnCorrupted(
                level,
                this.blockPosition(),
                level.getRandom(),
                difficulty
        );
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



    /* ------------------------------------------------ */
    /* DATA                                             */
    /* ------------------------------------------------ */

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CORRUPTED, 0);
    }

    /* ------------------------------------------------ */
    /* SOUNDS                                           */
    /* ------------------------------------------------ */

    @Override protected SoundEvent getAmbientSound() { return SoundEvents.SCULK_SENSOR_STEP; }
    @Override protected SoundEvent getHurtSound(@NotNull DamageSource source) { return SoundEvents.SCULK_BLOCK_BREAK; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.SCULK_BLOCK_BREAK; }

    private boolean isSculkMob(LivingEntity entity) {
        return entity.getType().is(TagKey.create(
                Registries.ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath("sculky", "sculk_mobs")));
    }
}
