package net.ronm19.sculky.portal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalShape;
import net.neoforged.neoforge.event.level.BlockEvent;

public class SculkPortalSpawnEvent extends BlockEvent.PortalSpawnEvent {
    private final SculkPortalShape size;

    public SculkPortalSpawnEvent( LevelAccessor level, BlockPos pos, BlockState state, SculkPortalShape size1, PortalShape size) {
        super(level, pos, state, size);
        this.size = size1;
    }

    public SculkPortalShape getSculkPortalSize() {
        return this.size;
    }
}

