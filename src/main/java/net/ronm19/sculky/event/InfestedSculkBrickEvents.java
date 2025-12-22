package net.ronm19.sculky.event;


import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.block.ModBlocks;

@EventBusSubscriber(modid = SculkyMod.MOD_ID)
public class InfestedSculkBrickEvents {

    private static final int BRICK_RADIUS = 6;
    private static final int SCULK_MOB_RADIUS = 8;
    private static final int CATALYST_RADIUS = 4;

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity().level() instanceof ServerLevel level)) return;

        BlockPos deathPos = event.getEntity().blockPosition();

        // Scan for Infested Sculk Bricks nearby
        BlockPos.MutableBlockPos scanPos = new BlockPos.MutableBlockPos();
        boolean foundBrick = false;
        boolean foundCatalyst = false;

        for (int dx = -BRICK_RADIUS; dx <= BRICK_RADIUS; dx++) {
            for (int dy = -BRICK_RADIUS; dy <= BRICK_RADIUS; dy++) {
                for (int dz = -BRICK_RADIUS; dz <= BRICK_RADIUS; dz++) {
                    scanPos.set(deathPos.getX() + dx, deathPos.getY() + dy, deathPos.getZ() + dz);
                    BlockState state = level.getBlockState(scanPos);

                    if (state.is(ModBlocks.INFESTED_SCULK_BRICKS.get())) {
                        foundBrick = true;

                        // Visual feedback from the brick
                        level.playSound(
                                null,
                                scanPos,
                                SoundEvents.SCULK_BLOCK_CHARGE,
                                SoundSource.BLOCKS,
                                0.8f,
                                0.9f + level.random.nextFloat() * 0.2f
                        );

                        level.sendParticles(
                                net.minecraft.core.particles.ParticleTypes.SCULK_SOUL,
                                scanPos.getX() + 0.5,
                                scanPos.getY() + 0.8,
                                scanPos.getZ() + 0.5,
                                6,
                                0.2,
                                0.2,
                                0.2,
                                0.01
                        );

                        // Check for nearby catalyst
                        if (!foundCatalyst) {
                            foundCatalyst = isCatalystNearby(level, scanPos, CATALYST_RADIUS);
                        }
                    }
                }
            }
        }

        if (!foundBrick) return;

        // Buff nearby sculk mobs
        AABB mobBox = new AABB(deathPos).inflate(SCULK_MOB_RADIUS);
        for (Mob mob : level.getEntitiesOfClass(Mob.class, mobBox,
                m -> m.getType().is(net.minecraft.tags.TagKey.create(
                        net.minecraft.core.registries.Registries.ENTITY_TYPE,
                        net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                                SculkyMod.MOD_ID, "sculk_mobs"))))) {

            mob.addEffect(new MobEffectInstance(
                    MobEffects.MOVEMENT_SPEED,
                    60, // 3 seconds
                    0
            ));
        }

        // Extra reaction if catalyst nearby (hook for later expansion)
        if (foundCatalyst) {
            level.playSound(
                    null,
                    deathPos,
                    SoundEvents.SCULK_CATALYST_BLOOM,
                    SoundSource.BLOCKS,
                    0.6f,
                    1.0f
            );
        }
    }

    private static boolean isCatalystNearby(ServerLevel level, BlockPos center, int radius) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    pos.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    if (level.getBlockState(pos).is(Blocks.SCULK_CATALYST)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
