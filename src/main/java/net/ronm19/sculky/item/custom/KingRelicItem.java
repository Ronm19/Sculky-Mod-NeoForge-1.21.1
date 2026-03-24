package net.ronm19.sculky.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.ronm19.sculky.block.ModBlocks;
import net.ronm19.sculky.block.custom.SculkPortalBlock;
import net.ronm19.sculky.portal.SculkPortalShape;

import java.util.Optional;

public class KingRelicItem extends Item {

    public KingRelicItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        Player player = context.getPlayer();

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.PASS;
        }

        // 🔥 Scan nearby positions instead of only clicked block
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                for (int dz = -2; dz <= 2; dz++) {

                    BlockPos checkPos = clickedPos.offset(dx, dy, dz);

                    for (Direction.Axis axis : new Direction.Axis[]{Direction.Axis.X, Direction.Axis.Z}) {

                        Optional<SculkPortalShape> optionalShape =
                                SculkPortalShape.findEmptyPortalShape(serverLevel, checkPos, axis);

                        if (optionalShape.isEmpty()) continue;

                        SculkPortalShape shape = optionalShape.get();

                        if (!shape.isValid()) continue;

                        // 🔥 Create portal
                        shape.createPortalBlocks();

                        if (player != null) {
                            player.getCooldowns().addCooldown(this, 20);
                        }

                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }

        return InteractionResult.PASS;
    }

    // KEEPING YOUR OLD METHODS (as requested, not deleting)

    private BlockPos findPortalBottomLeft(ServerLevel level, BlockPos clickedPos, Direction.Axis axis) {
        Direction right = axis == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;

        for (int dx = -4; dx <= 4; dx++) {
            for (int dy = -4; dy <= 4; dy++) {
                for (int dz = -4; dz <= 4; dz++) {
                    BlockPos candidate = clickedPos.offset(dx, dy, dz);

                    if (isValidPortalFrame(level, candidate, right)) {
                        return candidate;
                    }
                }
            }
        }

        return null;
    }

    private boolean isValidPortalFrame(ServerLevel level, BlockPos candidate, Direction right) {
        var frameState = ModBlocks.CHISELED_ANCIENT_SCULK_BRICKS.get().defaultBlockState();

        for (int y = 0; y < 3; y++) {
            for (int w = 0; w < 2; w++) {
                BlockPos interiorPos = candidate.above(y).relative(right, w);
                var interiorState = level.getBlockState(interiorPos);

                if (!interiorState.canBeReplaced()) {
                    return false;
                }
            }
        }

        for (int w = -1; w <= 2; w++) {
            BlockPos pos = candidate.below().relative(right, w);
            if (!level.getBlockState(pos).is(frameState.getBlock())) {
                return false;
            }
        }

        for (int w = -1; w <= 2; w++) {
            BlockPos pos = candidate.above(3).relative(right, w);
            if (!level.getBlockState(pos).is(frameState.getBlock())) {
                return false;
            }
        }

        for (int y = 0; y < 3; y++) {
            BlockPos pos = candidate.above(y).relative(right, -1);
            if (!level.getBlockState(pos).is(frameState.getBlock())) {
                return false;
            }
        }

        for (int y = 0; y < 3; y++) {
            BlockPos pos = candidate.above(y).relative(right, 2);
            if (!level.getBlockState(pos).is(frameState.getBlock())) {
                return false;
            }
        }

        return true;
    }
}