package net.ronm19.sculky.api.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.ronm19.sculky.block.ModBlocks;

public interface SculkPortal {
    private BlockState self() {
        return (BlockState)this;
    }

    default boolean isSculkPortalFrame( BlockGetter level, BlockPos pos ) {
        return this.self().getBlock().isPortalFrame(this.self(), level, pos);
    }

    static boolean isSculkPortalFrame( BlockState state, BlockGetter level, BlockPos pos ) {
        return state.is(ModBlocks.CHISELED_ANCIENT_SCULK_BRICKS);
    }
}
