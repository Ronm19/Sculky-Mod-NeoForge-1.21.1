package net.ronm19.sculky.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.ronm19.sculky.entity.ModEntities;
import net.ronm19.sculky.entity.custom.SculkEvokerEntity;
import net.ronm19.sculky.item.ModItems;
import org.jetbrains.annotations.NotNull;

public class RoyalSculkTotemBlock extends Block {

    private static final double EVOKER_CHECK_RADIUS = 48.0D;

    public RoyalSculkTotemBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(
            @NotNull ItemStack stack,
            @NotNull BlockState state,
            @NotNull Level level,
            @NotNull BlockPos pos,
            @NotNull Player player,
            @NotNull InteractionHand hand,
            @NotNull BlockHitResult hitResult
    ) {
        if (!stack.is(ModItems.SCULK_CORE.get())) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            return ItemInteractionResult.SUCCESS;
        }

        if (level.getDifficulty() == Difficulty.PEACEFUL) {
            failRitual(
                    serverLevel,
                    pos,
                    player,
                    Component.literal("The Royal Sculk Totem refuses to wake in Peaceful mode.")
            );
            return ItemInteractionResult.SUCCESS;
        }

        boolean evokerNearby = !serverLevel.getEntitiesOfClass(
                SculkEvokerEntity.class,
                new AABB(pos).inflate(EVOKER_CHECK_RADIUS)
        ).isEmpty();

        if (evokerNearby) {
            failRitual(
                    serverLevel,
                    pos,
                    player,
                    Component.literal("The Totem is already bound to a nearby royal caster.")
            );
            return ItemInteractionResult.SUCCESS;
        }

        BlockPos spawnPos = pos.above();

        if (!serverLevel.getBlockState(spawnPos).isAir() || !serverLevel.getBlockState(spawnPos.above()).isAir()) {
            failRitual(
                    serverLevel,
                    pos,
                    player,
                    Component.literal("The Totem needs open space above the crown.")
            );
            return ItemInteractionResult.SUCCESS;
        }

        SculkEvokerEntity evoker = ModEntities.SCULK_EVOKER.get().create(serverLevel);

        if (evoker == null) {
            failRitual(
                    serverLevel,
                    pos,
                    player,
                    Component.literal("The Totem pulses, but nothing answers.")
            );
            return ItemInteractionResult.SUCCESS;
        }

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        beginRitual(serverLevel, pos, player);

        evoker.moveTo(
                spawnPos.getX() + 0.5D,
                spawnPos.getY(),
                spawnPos.getZ() + 0.5D,
                player.getYRot() + 180.0F,
                0.0F
        );

        evoker.finalizeSpawn(
                serverLevel,
                serverLevel.getCurrentDifficultyAt(spawnPos),
                MobSpawnType.MOB_SUMMONED,
                null
        );

        evoker.setPersistenceRequired();

        serverLevel.addFreshEntity(evoker);

        finishRitual(serverLevel, pos, player);

        return ItemInteractionResult.SUCCESS;
    }

    private void failRitual(ServerLevel serverLevel, BlockPos pos, Player player, Component message) {
        player.displayClientMessage(message, true);

        serverLevel.sendParticles(
                ParticleTypes.SCULK_SOUL,
                pos.getX() + 0.5D,
                pos.getY() + 1.05D,
                pos.getZ() + 0.5D,
                10,
                0.35D,
                0.25D,
                0.35D,
                0.01D
        );

        serverLevel.playSound(
                null,
                pos,
                SoundEvents.SCULK_SHRIEKER_SHRIEK,
                SoundSource.BLOCKS,
                0.35F,
                1.45F
        );
    }

    private void beginRitual(ServerLevel serverLevel, BlockPos pos, Player player) {
        player.displayClientMessage(Component.literal("The Royal Sculk Totem begins to pulse..."), true);

        serverLevel.playSound(null, pos, SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.BLOCKS, 1.35F, 0.7F);
        serverLevel.playSound(null, pos, SoundEvents.EVOKER_PREPARE_SUMMON, SoundSource.HOSTILE, 1.15F, 0.65F);

        serverLevel.sendParticles(
                ParticleTypes.SCULK_SOUL,
                pos.getX() + 0.5D,
                pos.getY() + 1.15D,
                pos.getZ() + 0.5D,
                70,
                0.75D,
                0.45D,
                0.75D,
                0.045D
        );

        serverLevel.sendParticles(
                ParticleTypes.SOUL_FIRE_FLAME,
                pos.getX() + 0.5D,
                pos.getY() + 1.05D,
                pos.getZ() + 0.5D,
                24,
                0.45D,
                0.25D,
                0.45D,
                0.025D
        );

        serverLevel.sendParticles(
                ParticleTypes.SONIC_BOOM,
                pos.getX() + 0.5D,
                pos.getY() + 1.05D,
                pos.getZ() + 0.5D,
                1,
                0.0D,
                0.0D,
                0.0D,
                0.0D
        );
    }

    private void finishRitual(ServerLevel serverLevel, BlockPos pos, Player player) {
        player.displayClientMessage(Component.literal("A servant of the buried throne emerges."), true);

        serverLevel.playSound(null, pos, SoundEvents.EVOKER_CAST_SPELL, SoundSource.HOSTILE, 1.15F, 0.75F);

        serverLevel.sendParticles(
                ParticleTypes.SCULK_SOUL,
                pos.getX() + 0.5D,
                pos.getY() + 1.45D,
                pos.getZ() + 0.5D,
                55,
                0.55D,
                0.35D,
                0.55D,
                0.06D
        );

        serverLevel.sendParticles(
                ParticleTypes.SOUL_FIRE_FLAME,
                pos.getX() + 0.5D,
                pos.getY() + 1.25D,
                pos.getZ() + 0.5D,
                18,
                0.35D,
                0.2D,
                0.35D,
                0.03D
        );
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);

        if (random.nextFloat() < 0.4F) {
            double x = pos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 0.5D;
            double y = pos.getY() + 0.75D + random.nextDouble() * 0.4D;
            double z = pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 0.5D;

            double xSpeed = (random.nextDouble() - 0.5D) * 0.012D;
            double ySpeed = 0.015D + random.nextDouble() * 0.018D;
            double zSpeed = (random.nextDouble() - 0.5D) * 0.012D;

            level.addParticle(
                    ParticleTypes.SCULK_SOUL,
                    x, y, z,
                    xSpeed, ySpeed, zSpeed
            );
        }

        if (random.nextFloat() < 0.16F) {
            double x = pos.getX() + 0.5D;
            double y = pos.getY() + 1.05D;
            double z = pos.getZ() + 0.5D;

            level.addParticle(
                    ParticleTypes.SOUL_FIRE_FLAME,
                    x, y, z,
                    0.0D, 0.015D, 0.0D
            );
        }

        if (random.nextFloat() < 0.08F) {
            double x = pos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 0.25D;
            double y = pos.getY() + 1.15D + random.nextDouble() * 0.2D;
            double z = pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 0.25D;

            level.addParticle(
                    ParticleTypes.SCULK_SOUL,
                    x, y, z,
                    0.0D, 0.025D, 0.0D
            );
        }
    }
}