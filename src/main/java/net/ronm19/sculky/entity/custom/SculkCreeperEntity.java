package net.ronm19.sculky.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class SculkCreeperEntity extends Creeper implements Enemy {

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

        // Optional: target non-sculk mobs (if you want chaos)
        // this.targetSelector.addGoal(3,
        //        new NearestAttackableTargetGoal<>(this, Mob.class, 10, false, false,
        //                mob -> !mob.getType().is(SCULK_MOBS)));
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

        // Custom explosion
        this.level().explode(
                this,
                this.getX(),
                this.getY(),
                this.getZ(),
                charged ? CHARGED_EXPLOSION_POWER : EXPLOSION_POWER,
                Level.ExplosionInteraction.MOB
        );

        BlockPos center = this.blockPosition();

        applyInstantDarkness(level, center, charged);
        spawnDarknessCloud(level, charged);
        spreadSculk(level, center, charged);

        this.discard();
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
    private void spreadSculk(ServerLevel level, BlockPos center, boolean charged) {
        int radius = charged ? CHARGED_SCULK_RADIUS : SCULK_RADIUS;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {

                    pos.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    if (pos.distSqr(center) > radius * radius) continue;

                    BlockState state = level.getBlockState(pos);
                    if (state.is(Blocks.STONE)
                            || state.is(Blocks.DEEPSLATE)
                            || state.is(Blocks.DIRT)
                            || state.is(Blocks.GRASS_BLOCK)) {

                        if (level.random.nextFloat() < (charged ? 0.7f : 0.45f)) {
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
}