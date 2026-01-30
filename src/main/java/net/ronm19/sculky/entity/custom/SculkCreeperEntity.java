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
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.ronm19.sculky.api.interfaces.sculkSpreadingUtility;
import net.ronm19.sculky.entity.variant.CorruptedSculkCreeperVariant;
import net.ronm19.sculky.item.ModItems;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;

public class SculkCreeperEntity extends Creeper implements Enemy, sculkSpreadingUtility {

    /* ============================= */
    /* DATA                          */
    /* ============================= */

    public static final EntityDataAccessor<Integer> VARIANT =
            SynchedEntityData.defineId(SculkCreeperEntity.class, EntityDataSerializers.INT);

    /* ============================= */
    /* BALANCE                       */
    /* ============================= */

    private static final int FUSE_TICKS = 35;
    private final int maxSwell;

    public SculkCreeperEntity(EntityType<? extends Creeper> type, Level level) {
        super(type, level);

        // ✅ FIX: actually set creeper fuse
        this.maxSwell = FUSE_TICKS;
    }

    /* ============================= */
    /* AI                            */
    /* ============================= */

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
    }

    public static AttributeSupplier.Builder createSculkCreeperAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 24.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.27D)
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.ARMOR, 2.0D);
    }

    @Override
    public void explodeCreeper() {
        if (this.level().isClientSide) return;
        if (!(this.level() instanceof ServerLevel level)) return;

        boolean charged = this.isPowered();
        boolean corrupted = this.getVariant() == CorruptedSculkCreeperVariant.CORRUPTED;

        // ----------------------------
        // Explosion power
        // ----------------------------
        float explosionPower = 3.5F;

        if (charged) explosionPower += 2.0F;
        if (corrupted) explosionPower += 1.0F;

        // ----------------------------
        // Sculk radius
        // ----------------------------
        int sculkRadius = 3;

        if (charged) sculkRadius += 2;
        if (corrupted) sculkRadius += 1;

        BlockPos center = this.blockPosition();

        // ----------------------------
        // 1️⃣ VANILLA EXPLOSION
        // ----------------------------
        level.explode(
                this,
                this.getX(),
                this.getY(),
                this.getZ(),
                explosionPower,
                Level.ExplosionInteraction.MOB
        );

        // ----------------------------
        // 2️⃣ DARKNESS EFFECTS
        // ----------------------------
        applyInstantDarkness(level, center, charged || corrupted);
        spawnDarknessCloud(level, charged || corrupted);
        spawnLingeringCloud(level);

        // ----------------------------
        // 3️⃣ SCULK SPREAD
        // ----------------------------
        spreadSculk(level, center, sculkRadius);

        // ----------------------------
        // 4️⃣ CLEANUP
        // ----------------------------
        this.discard();
    }

    private void applyInstantDarkness(ServerLevel level, BlockPos center, boolean empowered) {
        int duration = empowered ? 300 : 200;

        AABB box = new AABB(center).inflate(6.0D);

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

    private void spawnDarknessCloud(ServerLevel level, boolean empowered) {
        AreaEffectCloud cloud = new AreaEffectCloud(
                level,
                this.getX(),
                this.getY(),
                this.getZ()
        );

        cloud.setRadius(empowered ? 4.5F : 3.5F);
        cloud.setRadiusOnUse(-0.4F);
        cloud.setWaitTime(10);
        cloud.setDuration(empowered ? 160 : 120);
        cloud.setRadiusPerTick(-cloud.getRadius() / cloud.getDuration());

        cloud.addEffect(new MobEffectInstance(
                MobEffects.DARKNESS,
                empowered ? 100 : 60,
                0
        ));

        level.addFreshEntity(cloud);
    }

    private void spawnLingeringCloud(ServerLevel level) {
        if (this.getActiveEffects().isEmpty()) return;

        AreaEffectCloud cloud = new AreaEffectCloud(
                level,
                this.getX(),
                this.getY(),
                this.getZ()
        );

        cloud.setRadius(2.5F);
        cloud.setRadiusOnUse(-0.5F);
        cloud.setWaitTime(10);
        cloud.setDuration(120);
        cloud.setRadiusPerTick(-cloud.getRadius() / cloud.getDuration());

        for (MobEffectInstance effect : this.getActiveEffects()) {
            cloud.addEffect(new MobEffectInstance(effect));
        }

        level.addFreshEntity(cloud);
    }

    private void spreadSculk(ServerLevel level, BlockPos center, int radius) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {

                    pos.set(
                            center.getX() + dx,
                            center.getY() + dy,
                            center.getZ() + dz
                    );

                    if (pos.distSqr(center) <= 4) continue;

                    BlockState state = level.getBlockState(pos);

                    if (state.is(Blocks.STONE)
                            || state.is(Blocks.DEEPSLATE)
                            || state.is(Blocks.DIRT)
                            || state.is(Blocks.GRASS_BLOCK)) {

                        if (level.random.nextFloat() < 0.55F) {
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
                                "sculky",
                                "sculk_mobs"
                        )
                )
        );
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, source, recentlyHit);

        RandomSource random = this.getRandom();

        int count = random.nextInt(2); // 0–1 base drop

        if (count > 0) {
            this.spawnAtLocation(new ItemStack(ModItems.ECHO_DUST.get(), count));
        }
    }




    /* ============================= */
    /* VARIANT / SAVE                */
    /* ============================= */

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VARIANT, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Variant", getVariant().getId());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setVariant(CorruptedSculkCreeperVariant.byId(tag.getInt("Variant")));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData data) {
        data = super.finalizeSpawn(level, difficulty, spawnType, data);

        boolean corrupted = spawnType == MobSpawnType.SPAWN_EGG
                ? random.nextFloat() < 0.6f
                : random.nextFloat() < (blockPosition().getY() < 40 ? 0.45f : 0.12f);

        setVariant(corrupted
                ? CorruptedSculkCreeperVariant.CORRUPTED
                : CorruptedSculkCreeperVariant.NORMAL);

        if (corrupted) applyCorruptedAttributes();
        return data;
    }

    private void applyCorruptedAttributes() {
        Objects.requireNonNull(getAttribute(Attributes.MAX_HEALTH)).setBaseValue(36.0D);
        Objects.requireNonNull(getAttribute(Attributes.MOVEMENT_SPEED)).setBaseValue(0.30D);
        Objects.requireNonNull(getAttribute(Attributes.FOLLOW_RANGE)).setBaseValue(32.0D);
        Objects.requireNonNull(getAttribute(Attributes.ARMOR)).setBaseValue(2.55D);
        setHealth(getMaxHealth());
    }

    public CorruptedSculkCreeperVariant getVariant() {
        return CorruptedSculkCreeperVariant.byId(entityData.get(VARIANT));
    }

    private void setVariant(CorruptedSculkCreeperVariant variant) {
        entityData.set(VARIANT, variant.getId());
    }

    /* ============================= */
    /* SOUNDS                        */
    /* ============================= */

    @Override protected SoundEvent getAmbientSound() { return SoundEvents.SCULK_SENSOR_STEP; }
    @Override protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource src) { return SoundEvents.SCULK_BLOCK_BREAK; }
    @Override protected @NotNull SoundEvent getDeathSound() { return SoundEvents.SCULK_BLOCK_BREAK; }
}
