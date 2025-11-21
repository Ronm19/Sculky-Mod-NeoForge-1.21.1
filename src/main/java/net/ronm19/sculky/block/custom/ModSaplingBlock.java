package net.ronm19.sculky.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;

public class ModSaplingBlock extends SaplingBlock {

    public ModSaplingBlock( TreeGrower treeGrower, Properties properties) {
        super(treeGrower, properties);
    }

    @Override
    protected boolean mayPlaceOn( BlockState state, BlockGetter level, BlockPos pos) {
        // Allow planting on anything tagged as dirt
        return state.is(BlockTags.DIRT);
    }
}
