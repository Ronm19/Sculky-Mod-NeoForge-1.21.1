package net.ronm19.sculky.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.phys.AABB;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.ModEntities;
import net.ronm19.sculky.entity.custom.SculkKingEntity;

public class SculkKingRitualHelper {
    public static final ResourceKey<Structure> BURIED_THRONE = ResourceKey.create(
            Registries.STRUCTURE,
            ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "buried_throne")
    );

    private static final double KING_CHECK_RADIUS = 96.0D;

    public static boolean isInsideBuriedThrone(ServerLevel level, BlockPos pos) {
        return level.registryAccess()
                .registryOrThrow(Registries.STRUCTURE)
                .getHolder(BURIED_THRONE)
                .map(holder -> level.structureManager()
                        .getStructureWithPieceAt(pos, holder.value())
                        .isValid())
                .orElse(false);
    }

    public static boolean hasNearbySculkKing(ServerLevel level, BlockPos pos) {
        return !level.getEntitiesOfClass(
                SculkKingEntity.class,
                new AABB(pos).inflate(KING_CHECK_RADIUS),
                king -> king.isAlive()
        ).isEmpty();
    }

    public static boolean summonSculkKing(ServerLevel level, BlockPos pedestalPos) {
        if (hasNearbySculkKing(level, pedestalPos)) {
            return false;
        }

        SculkKingEntity king = ModEntities.SCULK_KING.get().create(level);

        if (king == null) {
            return false;
        }

        BlockPos spawnPos = findKingSpawnPos(level, pedestalPos);

        king.moveTo(
                spawnPos.getX() + 0.5D,
                spawnPos.getY(),
                spawnPos.getZ() + 0.5D,
                level.random.nextFloat() * 360.0F,
                0.0F
        );

        king.setPersistenceRequired();

        king.finalizeSpawn(
                level,
                level.getCurrentDifficultyAt(spawnPos),
                MobSpawnType.MOB_SUMMONED,
                null
        );

        boolean added = level.addFreshEntity(king);

        if (!added) {
            return false;
        }

        king.triggerRoarAnimation();

        playKingSummonEffects(level, pedestalPos, spawnPos);

        return true;
    }

    private static BlockPos findKingSpawnPos(ServerLevel level, BlockPos pedestalPos) {
        BlockPos[] candidates = new BlockPos[] {
                pedestalPos.offset(0, 1, 3),
                pedestalPos.offset(0, 1, -3),
                pedestalPos.offset(3, 1, 0),
                pedestalPos.offset(-3, 1, 0),
                pedestalPos.offset(0, 1, 5),
                pedestalPos.offset(0, 1, -5),
                pedestalPos.above()
        };

        for (BlockPos candidate : candidates) {
            BlockPos ground = candidate.below();

            boolean hasGround = level.getBlockState(ground)
                    .isFaceSturdy(level, ground, net.minecraft.core.Direction.UP);

            boolean hasRoom =
                    level.getBlockState(candidate).isAir()
                            && level.getBlockState(candidate.above()).isAir()
                            && level.getBlockState(candidate.above(2)).isAir()
                            && level.getBlockState(candidate.above(3)).isAir()
                            && level.getBlockState(candidate.above(4)).isAir();

            if (hasGround && hasRoom) {
                return candidate;
            }
        }

        // Emergency fallback: spawn above the pedestal no matter what.
        return pedestalPos.above();
    }

    public static void playThroneAnswerEffects(ServerLevel level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.BLOCKS, 1.4F, 0.55F);
        level.playSound(null, pos, SoundEvents.WARDEN_HEARTBEAT, SoundSource.HOSTILE, 1.2F, 0.7F);
        level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 0.8F, 0.45F);

        level.sendParticles(
                net.minecraft.core.particles.ParticleTypes.SCULK_SOUL,
                pos.getX() + 0.5D,
                pos.getY() + 1.1D,
                pos.getZ() + 0.5D,
                60,
                0.75D,
                0.45D,
                0.75D,
                0.045D
        );

        level.sendParticles(
                net.minecraft.core.particles.ParticleTypes.SONIC_BOOM,
                pos.getX() + 0.5D,
                pos.getY() + 1.0D,
                pos.getZ() + 0.5D,
                1,
                0.0D,
                0.0D,
                0.0D,
                0.0D
        );
    }

    private static void playKingSummonEffects(ServerLevel level, BlockPos pedestalPos, BlockPos spawnPos) {
        level.playSound(null, pedestalPos, SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.HOSTILE, 1.8F, 0.35F);
        level.playSound(null, pedestalPos, SoundEvents.WARDEN_EMERGE, SoundSource.HOSTILE, 1.4F, 0.55F);
        level.playSound(null, pedestalPos, SoundEvents.WARDEN_ROAR, SoundSource.HOSTILE, 1.2F, 0.7F);

        level.sendParticles(
                net.minecraft.core.particles.ParticleTypes.SCULK_SOUL,
                pedestalPos.getX() + 0.5D,
                pedestalPos.getY() + 1.2D,
                pedestalPos.getZ() + 0.5D,
                140,
                1.1D,
                0.8D,
                1.1D,
                0.08D
        );

        level.sendParticles(
                net.minecraft.core.particles.ParticleTypes.SCULK_SOUL,
                spawnPos.getX() + 0.5D,
                spawnPos.getY() + 1.6D,
                spawnPos.getZ() + 0.5D,
                180,
                1.0D,
                1.2D,
                1.0D,
                0.09D
        );

        level.sendParticles(
                net.minecraft.core.particles.ParticleTypes.SONIC_BOOM,
                spawnPos.getX() + 0.5D,
                spawnPos.getY() + 1.4D,
                spawnPos.getZ() + 0.5D,
                1,
                0.0D,
                0.0D,
                0.0D,
                0.0D
        );
    }
}