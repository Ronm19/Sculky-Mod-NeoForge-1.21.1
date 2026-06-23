package net.ronm19.sculky.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.ronm19.sculky.effect.ModEffects;
import org.jetbrains.annotations.NotNull;

public class SculkBlock extends Block {

    public SculkBlock(Properties properties) {
        super(properties);
    }

    /* ============================= */
    /*        CORE HELPERS           */
    /* ============================= */

    protected boolean isInfected(Entity entity) {
        return entity instanceof LivingEntity living &&
                living.hasEffect(ModEffects.SCULK_INFECTION_EFFECT.getDelegate());
    }

    protected boolean shouldReact(Level level) {
        return !(level.random.nextFloat() < 0.15F); // subtle & vanilla-like
    }

    protected void spawnPulse(Level level, BlockPos pos) {
        level.addParticle(
                ParticleTypes.SCULK_SOUL,
                pos.getX() + 0.5,
                pos.getY() + 0.8,
                pos.getZ() + 0.5,
                0.0D, 0.02D, 0.0D
        );
    }

    /* ============================= */
    /*      PASSIVE INTERACTION      */
    /* ============================= */

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!level.isClientSide) return;
        if (shouldReact(level)) return;

        if (isInfected(entity)) {
            spawnPulse(level, pos);
            entityInside(level, pos, state, entity);
        }

        super.stepOn(level, pos, state, entity);
    }

    public void entityInside(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!level.isClientSide) return;
        if (shouldReact(level)) return;

        if (isInfected(entity)) {
            spawnPulse(level, pos);
        }

        super.entityInside(state, level, pos, entity);
    }

    @Override
    public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, net.minecraft.util.RandomSource random) {
        if (random.nextFloat() < 0.03F) {
            spawnPulse(level, pos);
        }
    }
}
