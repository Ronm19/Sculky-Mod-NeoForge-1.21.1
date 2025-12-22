package net.ronm19.sculky.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.lighting.LightEngine;
import net.ronm19.sculky.block.ModBlocks;
import org.checkerframework.checker.units.qual.C;

public class InfestedSculkGrassBlock extends SpreadingSnowyDirtBlock implements BonemealableBlock {

    /* ============================= */
    /*        CODEC (REQUIRED)       */
    /* ============================= */

    public static final MapCodec<InfestedSculkGrassBlock> CODEC =
            simpleCodec(InfestedSculkGrassBlock::new);

    public InfestedSculkGrassBlock(Properties properties) {
        super(properties);
        // DO NOT register SNOWY here – SpreadingSnowyDirtBlock already does it
    }

    @Override
    protected MapCodec<? extends SpreadingSnowyDirtBlock> codec() {
        return CODEC;
    }

    /* ============================= */
    /*        BONEMEAL LOGIC         */
    /* ============================= */

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return level.getBlockState(pos.above()).isAir();
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        if (level.isEmptyBlock(pos.above())) {
            level.setBlockAndUpdate(
                    pos.above(),
                    ModBlocks.SCULKBLOOM.get().defaultBlockState()
            );
        }
    }

    /* ============================= */
    /*     SCULK GRASS BEHAVIOR      */
    /* ============================= */

    private static boolean canBeGrass(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos above = pos.above();
        BlockState aboveState = level.getBlockState(above);

        if (aboveState.is(Blocks.SNOW) && aboveState.getValue(SnowLayerBlock.LAYERS) == 1) {
            return true;
        } else if (aboveState.getFluidState().is(FluidTags.WATER)) {
            return false;
        } else {
            int lightBlock = LightEngine.getLightBlockInto(
                    level,
                    state,
                    pos,
                    aboveState,
                    above,
                    Direction.UP,
                    aboveState.getLightBlock(level, above)
            );
            return lightBlock < level.getMaxLightLevel();
        }
    }

    private static boolean canPropagate(BlockState state, LevelReader level, BlockPos pos) {
        return canBeGrass(state, level, pos)
                && !level.getFluidState(pos.above()).is(FluidTags.WATER);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 3)) return;

        if (!canBeGrass(state, level, pos)) {
            level.setBlockAndUpdate(
                    pos,
                    ModBlocks.INFESTED_SCULK_DIRT_BLOCK.get().defaultBlockState()
            );
            return;
        }

        if (level.getMaxLocalRawBrightness(pos.above()) >= 9) {
            BlockState grassState = this.defaultBlockState();

            for (int i = 0; i < 4; ++i) {
                BlockPos target = pos.offset(
                        random.nextInt(3) - 1,
                        random.nextInt(5) - 3,
                        random.nextInt(3) - 1
                );

                if (level.getBlockState(target).is(ModBlocks.INFESTED_SCULK_DIRT_BLOCK.get())
                        && canPropagate(grassState, level, target)) {

                    boolean snowy = level.getBlockState(target.above()).is(Blocks.SNOW);
                    level.setBlockAndUpdate(
                            target,
                            grassState.setValue(BlockStateProperties.SNOWY, snowy)
                    );
                }
            }
        }
    }
}

