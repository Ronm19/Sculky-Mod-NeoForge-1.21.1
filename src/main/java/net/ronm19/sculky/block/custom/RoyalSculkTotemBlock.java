package net.ronm19.sculky.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class RoyalSculkTotemBlock extends Block {

    public RoyalSculkTotemBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);

        if (random.nextFloat() < 0.35F) {
            double x = pos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 0.45D;
            double y = pos.getY() + 0.75D + random.nextDouble() * 0.35D;
            double z = pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 0.45D;

            double xSpeed = (random.nextDouble() - 0.5D) * 0.01D;
            double ySpeed = 0.015D + random.nextDouble() * 0.015D;
            double zSpeed = (random.nextDouble() - 0.5D) * 0.01D;

            level.addParticle(
                    ParticleTypes.SCULK_SOUL,
                    x, y, z,
                    xSpeed, ySpeed, zSpeed
            );
        }

        if (random.nextFloat() < 0.12F) {
            double x = pos.getX() + 0.5D;
            double y = pos.getY() + 1.05D;
            double z = pos.getZ() + 0.5D;

            level.addParticle(
                    ParticleTypes.SOUL_FIRE_FLAME,
                    x, y, z,
                    0.0D, 0.015D, 0.0D
            );
        }
    }
}