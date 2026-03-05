package net.ronm19.sculky.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class SculkHuskEntity extends Husk implements Enemy {

    // ---- Variant ----
    public enum Variant {
        NORMAL(0),
        JUNGLE(1),
        WASTES(2);

        private final int id;
        Variant(int id) { this.id = id; }
        public int getId() { return id; }

        public static Variant fromId(int id) {
            for (Variant v : values()) {
                if (v.id == id) return v;
            }
            return NORMAL;
        }
    }

    private static final EntityDataAccessor<Integer> VARIANT =
            SynchedEntityData.defineId(SculkHuskEntity.class, EntityDataSerializers.INT);

    // Tuning knobs
    private static final double SPRINT_DETECT_RADIUS = 14.0D;
    private static final int SPEED_TICKS = 40;           // 2s
    private static final int RESIST_TICKS = 60;          // 3s
    private static final int TICK_CHECK_INTERVAL = 10;   // every 0.5s

    public SculkHuskEntity(EntityType<? extends Husk> type, Level level) {
        super(type, level);
    }

    // ---- Attributes ----
    public static AttributeSupplier.Builder createSculkHuskAttributes() {
        return Husk.createAttributes()
                .add(Attributes.MAX_HEALTH, 24.0D)              // 12 hearts
                .add(Attributes.ARMOR, 2.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.24D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.15D)
                .add(Attributes.FOLLOW_RANGE, 28.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VARIANT, Variant.NORMAL.getId());
    }

    public Variant getVariant() {
        return Variant.fromId(this.entityData.get(VARIANT));
    }

    public void setVariant(Variant variant) {
        this.entityData.set(VARIANT, variant.getId());
    }

    // ---- Guarantee no daylight burning ----
    @Override
    protected boolean isSunBurnTick() {
        return false;
    }

    // ---- Spawn: choose variant by biome and apply stats ----

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level,
                                        DifficultyInstance difficulty,
                                        MobSpawnType reason,
                                        @Nullable SpawnGroupData spawnData) {

        SpawnGroupData data = super.finalizeSpawn(level, difficulty, reason, spawnData);

        Variant v = pickVariantByBiome(level);
        setVariant(v);
        applyVariantStats(v);

        return data;
    }


    /**
     * Choose variant based on your biomes.
     * Replace the biome checks with your actual biome keys if you want it strict.
     */
    private Variant pickVariantByBiome(ServerLevelAccessor level) {
        // If you want strict biome-based variants, uncomment and use your keys:
        // var biomeHolder = level.getBiome(this.blockPosition());
        // if (biomeHolder.is(ModBiomes.SCULK_JUNGLE)) return Variant.JUNGLE;
        // if (biomeHolder.is(ModBiomes.SCULK_WASTES)) return Variant.WASTES;
        // if (biomeHolder.is(ModBiomes.SCULK_FOREST)) return Variant.NORMAL;

        // Fallback logic (works even without biome keys): pick by "sculkiness" around you
        // If on sculk and dry-ish (no easy check) -> wastes, else jungle chance.
        if (isOnSculk(level, this.blockPosition())) {
            // small bias: more jungle than wastes by default
            return this.random.nextFloat() < 0.25F ? Variant.WASTES : Variant.JUNGLE;
        }
        return Variant.NORMAL;
    }

    private void applyVariantStats(Variant v) {
        switch (v) {
            case JUNGLE -> {
                setBase((Holder<Attribute>) Attributes.MAX_HEALTH, 22.0D);
                setBase((Holder<Attribute>) Attributes.MOVEMENT_SPEED, 0.26D);
                setBase((Holder<Attribute>) Attributes.KNOCKBACK_RESISTANCE, 0.12D);
            }
            case WASTES -> {
                setBase((Holder<Attribute>) Attributes.MAX_HEALTH, 28.0D);
                setBase((Holder<Attribute>) Attributes.MOVEMENT_SPEED, 0.24D);
                setBase((Holder<Attribute>) Attributes.KNOCKBACK_RESISTANCE, 0.25D);
            }
            default -> {
                setBase((Holder<Attribute>) Attributes.MAX_HEALTH, 24.0D);
                setBase((Holder<Attribute>) Attributes.MOVEMENT_SPEED, 0.24D);
                setBase((Holder<Attribute>) Attributes.KNOCKBACK_RESISTANCE, 0.15D);
            }
        }
        this.setHealth(this.getMaxHealth());
    }


    private void setBase(Holder<Attribute> attribute, double value) {
        var inst = this.getAttribute(attribute);
        if (inst != null) inst.setBaseValue(value);
    }


    // ---- Behavior: vibration reaction + sculk synergy ----
    @Override
    public void tick() {
        super.tick();

        // Run logic less often to keep it lightweight
        if (this.tickCount % TICK_CHECK_INTERVAL != 0) return;

        // 1) Vibration-ish: nearby sprinting player => speed boost
        Player nearest = this.level().getNearestPlayer(this, SPRINT_DETECT_RADIUS);
        if (nearest != null && nearest.isSprinting()) {
            // only refresh if it's almost gone, to avoid constant spam
            MobEffectInstance cur = this.getEffect(MobEffects.MOVEMENT_SPEED);
            if (cur == null || cur.getDuration() < 10) {
                this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, SPEED_TICKS, 0, true, false));
            }
        }

        // 2) Sculk synergy: standing on sculk => resistance
        if (isOnSculk(this.level(), this.blockPosition())) {
            MobEffectInstance cur = this.getEffect(MobEffects.DAMAGE_RESISTANCE);
            if (cur == null || cur.getDuration() < 10) {
                this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, RESIST_TICKS, 0, true, false));
            }
        }

        // 3) Small ambience particles (client-only)
        if (this.level().isClientSide && this.random.nextFloat() < 0.25F) {
            Vec3 p = this.position().add((this.random.nextDouble() - 0.5) * 0.6, 1.0, (this.random.nextDouble() - 0.5) * 0.6);
            this.level().addParticle(ParticleTypes.SCULK_SOUL, p.x, p.y, p.z, 0.0, 0.0, 0.0);
        }
    }

    private static boolean isOnSculk(Level level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        return isSculkBlock(below.getBlock());
    }

    private static boolean isOnSculk(ServerLevelAccessor level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        return isSculkBlock(below.getBlock());
    }

    private static boolean isSculkBlock(Block b) {
        // Vanilla sculk family; add your own blocks/tags here if you want.
        return b == Blocks.SCULK
                || b == Blocks.SCULK_VEIN
                || b == Blocks.SCULK_CATALYST
                || b == Blocks.SCULK_SENSOR
                || b == Blocks.CALIBRATED_SCULK_SENSOR
                || b == Blocks.SCULK_SHRIEKER;
    }

    // ---- Save/load variant ----
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("SculkVariant", this.entityData.get(VARIANT));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("SculkVariant")) {
            this.entityData.set(VARIANT, tag.getInt("SculkVariant"));
        }
    }
}
