package net.ronm19.sculky.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.ronm19.sculky.entity.variant.CorruptedSculkCreeperVariant;
import net.ronm19.sculky.entity.variant.CorruptedSculkSkeletonVariant;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;

public class SculkCreeperEntity extends Creeper implements Enemy {

    public static final EntityDataAccessor<Integer> CORRUPTED =
            SynchedEntityData.defineId(SculkCreeperEntity.class, EntityDataSerializers.INT);

    // ===== BALANCE VALUES =====
    private static final int FUSE_TICKS = 35;            // vanilla = 30
    private static final int CHARGED_FUSE_TICKS = 30;

    private static final float EXPLOSION_POWER = 3.5f;  // vanilla = 3.0
    private static final float CHARGED_EXPLOSION_POWER = 5.5f;

    private static final int DARKNESS_TICKS = 200;       // 10s
    private static final int CHARGED_DARKNESS_TICKS = 300;

    private static final int SCULK_RADIUS = 3;
    private static final int CHARGED_SCULK_RADIUS = 5;

    private int maxSwell = 35;
    private int explosionRadius = 3;

    public SculkCreeperEntity(EntityType<? extends Creeper> type, Level level) {
        super(type, level);
        this.explosionRadius = (int) EXPLOSION_POWER;
        this.maxSwell = FUSE_TICKS;
    }

    @Override
    protected void registerGoals() {
        // Core movement / behavior
        this.goalSelector.addGoal(1, new FloatGoal(this));

        // Swell & explode (Creeper core behavior)
        this.goalSelector.addGoal(2, new SwellGoal(this));

        // Melee approach (important for fuse triggering)
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, false));

        // Wander
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));

        // Look behaviors
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        // ===== TARGETING =====

        // React if hurt
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));

        // Aggro players
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, TamableAnimal.class, true));
    }

    public static AttributeSupplier.Builder createSculkCreeperAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 24.0D)          // Slightly tankier
                .add(Attributes.MOVEMENT_SPEED, 0.27D)      // Slightly faster
                .add(Attributes.FOLLOW_RANGE, 24.0D)        // Better tracking
                .add(Attributes.ARMOR, 2.0D);                // Mild resistance
    }




    protected void explodeCreeper() {
        if (!(this.level() instanceof ServerLevel level)) return;

        boolean charged = this.isPowered();

        float explosionPower = charged ? CHARGED_EXPLOSION_POWER : EXPLOSION_POWER;
        int sculkRadius = charged ? CHARGED_SCULK_RADIUS : SCULK_RADIUS;

        BlockPos center = this.blockPosition();

        // 1️⃣ Do explosion FIRST (vanilla-style)
        level.explode(
                this,
                this.getX(),
                this.getY(),
                this.getZ(),
                explosionPower,
                Level.ExplosionInteraction.MOB
        );

        // 2️⃣ Apply darkness effects
        applyInstantDarkness(level, center, charged);
        spawnDarknessCloud(level, charged);

        // 3️⃣ Spread sculk AROUND the crater (IMPORTANT)
        spreadSculkAroundCrater(level, center, sculkRadius + 2);

        tryPlaceShrieker(level, center, charged);
        tryPlaceWardenShrieker(level, center, charged);


        explodeCreeper();

        // 4️⃣ Kill creeper
        this.discard();
    }


    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide && this.isIgnited() && this.tickCount % 10 == 0) {
            this.level().playSound(
                    null,
                    this.blockPosition(),
                    SoundEvents.SCULK_SENSOR_HIT,
                    SoundSource.HOSTILE,
                    this.isPowered() ? 1.2F : 0.8F,
                    this.isPowered() ? 0.7F : 0.9F
            );
        }
    }


    // ============================
    // INSTANT DARKNESS
    // ============================
    private void applyInstantDarkness(ServerLevel level, BlockPos center, boolean charged) {
        int duration = charged ? CHARGED_DARKNESS_TICKS : DARKNESS_TICKS;

        AABB box = new AABB(center).inflate(6);
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, box)) {
            if (isSculkMob(entity)) continue;

            entity.addEffect(new MobEffectInstance(
                    MobEffects.DARKNESS,
                    duration,
                    0,
                    true,
                    true
            ));
        }
    }

    // ============================
    // LINGERING DARKNESS CLOUD
    // ============================
    private void spawnDarknessCloud(ServerLevel level, boolean charged) {
        AreaEffectCloud cloud = new AreaEffectCloud(level, this.getX(), this.getY(), this.getZ());

        cloud.setRadius(charged ? 4.5f : 3.5f);
        cloud.setRadiusOnUse(-0.4f);
        cloud.setWaitTime(10);
        cloud.setDuration(charged ? 160 : 120);
        cloud.setRadiusPerTick(-cloud.getRadius() / cloud.getDuration());

        cloud.addEffect(new MobEffectInstance(
                MobEffects.DARKNESS,
                charged ? 100 : 60,
                0
        ));

        level.addFreshEntity(cloud);
    }

    // ============================
    // SCULK SPREAD
    // ============================
    private void spreadSculkAroundCrater(ServerLevel level, BlockPos center, int radius) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {

                    pos.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);

                    // Skip inside the blast core
                    if (pos.distSqr(center) <= 4) continue;

                    BlockState state = level.getBlockState(pos);

                    // Only convert solid natural blocks
                    if (state.is(Blocks.STONE)
                            || state.is(Blocks.DEEPSLATE)
                            || state.is(Blocks.DIRT)
                            || state.is(Blocks.GRASS_BLOCK)) {

                        if (level.random.nextFloat() < 0.55f) {
                            level.setBlock(pos, Blocks.SCULK.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }
    }

    private boolean isSculkMob(LivingEntity entity) {
        return entity.getType().is(
                net.minecraft.tags.TagKey.create(
                        net.minecraft.core.registries.Registries.ENTITY_TYPE,
                        net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                                "sculky", "sculk_mobs"
                        )));
    }

    private void tryPlaceShrieker(ServerLevel level, BlockPos center, boolean charged) {
        float chance = charged ? 0.35f : 0.15f;
        if (level.random.nextFloat() > chance) return;

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int i = 0; i < 20; i++) {
            pos.set(
                    center.getX() + level.random.nextInt(-4, 5),
                    center.getY() + level.random.nextInt(-2, 3),
                    center.getZ() + level.random.nextInt(-4, 5)
            );

            BlockState state = level.getBlockState(pos);
            BlockState below = level.getBlockState(pos.below());

            if (state.isAir() && below.is(Blocks.SCULK)) {
                level.setBlock(pos, Blocks.SCULK_SHRIEKER.defaultBlockState(), 3);
                break;
            }
        }
    }

    private void tryPlaceWardenShrieker(ServerLevel level, BlockPos center, boolean charged) {
        float chance = charged ? 0.35f : 0.15f;
        if (level.random.nextFloat() > chance) return;

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int i = 0; i < 25; i++) {
            pos.set(
                    center.getX() + level.random.nextInt(-5, 6),
                    center.getY() + level.random.nextInt(-2, 3),
                    center.getZ() + level.random.nextInt(-5, 6)
            );

            // Avoid blast core
            if (pos.distSqr(center) <= 9) continue;

            BlockState state = level.getBlockState(pos);
            BlockState below = level.getBlockState(pos.below());

            // Place ONLY on sculk
            if (state.isAir() && below.is(Blocks.SCULK)) {

                BlockState shrieker = Blocks.SCULK_SHRIEKER
                        .defaultBlockState()
                        .setValue(
                                net.minecraft.world.level.block.SculkShriekerBlock.CAN_SUMMON,
                                true
                        );

                level.setBlock(pos, shrieker, 3);
                break;
            }
        }
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
            this.setVariant(CorruptedSculkCreeperVariant.CORRUPTED);
            applyCorruptedAttributes();
        } else {
            this.setVariant(CorruptedSculkCreeperVariant.NORMAL);
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

        if (getVariant() == CorruptedSculkCreeperVariant.CORRUPTED) {
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
        Objects.requireNonNull(this.getAttribute(Attributes.ARMOR)).setBaseValue(2.55D);

        this.setHealth(this.getMaxHealth());
    }

    private int getTypeVariant() {
        return this.entityData.get(CORRUPTED);
    }

    public CorruptedSculkCreeperVariant getVariant() {
        return CorruptedSculkCreeperVariant.byId(this.getTypeVariant() & 255);
    }

    private void setVariant(CorruptedSculkCreeperVariant variant) {
        this.entityData.set(CORRUPTED, variant.getId() & 255);
    }



    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SCULK_SENSOR_STEP;
    }

    @Override
    protected @NotNull SoundEvent getHurtSound( @NotNull DamageSource source) {
        return SoundEvents.SCULK_BLOCK_BREAK;
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return SoundEvents.SCULK_BLOCK_BREAK;
    }
}