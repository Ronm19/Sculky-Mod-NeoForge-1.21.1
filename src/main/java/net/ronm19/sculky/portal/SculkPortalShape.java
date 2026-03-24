package net.ronm19.sculky.portal;

import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.ronm19.sculky.api.interfaces.SculkPortal;
import net.ronm19.sculky.block.ModBlocks;
import net.ronm19.sculky.block.custom.SculkPortalBlock;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Predicate;

public class SculkPortalShape {
    private static final int MIN_WIDTH = 2;
    public static final int MAX_WIDTH = 21;
    private static final int MIN_HEIGHT = 3;
    public static final int MAX_HEIGHT = 21;
    private static final BlockBehaviour.StatePredicate FRAME = SculkPortal::isSculkPortalFrame;
    private static final float SAFE_TRAVEL_MAX_ENTITY_XY = 4.0F;
    private static final double SAFE_TRAVEL_MAX_VERTICAL_DELTA = (double)1.0F;
    private final LevelAccessor level;
    private final Direction.Axis axis;
    private final Direction rightDir;
    private int numPortalBlocks;
    @Nullable
    private BlockPos bottomLeft;
    private int height;
    private final int width;

    public static Optional<SculkPortalShape> findEmptyPortalShape( LevelAccessor level, BlockPos bottomLeft, Direction.Axis axis) {
        return findSculkPortalShape(level, bottomLeft, (p_77727_) -> p_77727_.isValid() && p_77727_.numPortalBlocks == 0, axis);
    }

    public static Optional<SculkPortalShape> findSculkPortalShape( @NotNull LevelAccessor level, BlockPos bottomLeft, Predicate<SculkPortalShape> predicate, Direction.Axis axis) {
        Optional<SculkPortalShape> optional = Optional.of(new SculkPortalShape(level, bottomLeft, axis)).filter(predicate);
        if (optional.isPresent()) {
            return optional;
        } else {
            Direction.Axis direction$axis = axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
            return Optional.of(new SculkPortalShape(level, bottomLeft, direction$axis)).filter(predicate);
        }
    }

    public SculkPortalShape( LevelAccessor level, @org.jetbrains.annotations.Nullable BlockPos bottomLeft, Direction.Axis axis) {
        this.level = level;
        this.axis = axis;
        this.rightDir = axis == Direction.Axis.X ? Direction.WEST : Direction.SOUTH;
        this.bottomLeft = this.calculateBottomLeft(bottomLeft);
        if (this.bottomLeft == null) {
            this.bottomLeft = bottomLeft;
            this.width = 1;
            this.height = 1;
        } else {
            this.width = this.calculateWidth();
            if (this.width > 0) {
                this.height = this.calculateHeight();
            }
        }

    }

    @Nullable
    private BlockPos calculateBottomLeft(BlockPos pos) {
        for(int i = Math.max(this.level.getMinBuildHeight(), pos.getY() - 21); pos.getY() > i && isEmpty(this.level.getBlockState(pos.below())); pos = pos.below()) {
        }

        Direction direction = this.rightDir.getOpposite();
        int j = this.getDistanceUntilEdgeAboveFrame(pos, direction) - 1;
        return j < 0 ? null : pos.relative(direction, j);
    }

    private int calculateWidth() {
        int i = this.getDistanceUntilEdgeAboveFrame(this.bottomLeft, this.rightDir);
        return i >= 2 && i <= 21 ? i : 0;
    }

    private int getDistanceUntilEdgeAboveFrame(BlockPos pos, Direction direction) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for(int i = 0; i <= 21; ++i) {
            blockpos$mutableblockpos.set(pos).move(direction, i);
            BlockState blockstate = this.level.getBlockState(blockpos$mutableblockpos);
            if (!isEmpty(blockstate)) {
                if (FRAME.test(blockstate, this.level, blockpos$mutableblockpos)) {
                    return i;
                }
                break;
            }

            BlockState blockstate1 = this.level.getBlockState(blockpos$mutableblockpos.move(Direction.DOWN));
            if (!FRAME.test(blockstate1, this.level, blockpos$mutableblockpos)) {
                break;
            }
        }

        return 0;
    }

    private int calculateHeight() {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        int i = this.getDistanceUntilTop(blockpos$mutableblockpos);
        return i >= 3 && i <= 21 && this.hasTopFrame(blockpos$mutableblockpos, i) ? i : 0;
    }

    private boolean hasTopFrame(BlockPos.MutableBlockPos pos, int distanceToTop) {
        for(int i = 0; i < this.width; ++i) {
            BlockPos.MutableBlockPos blockpos$mutableblockpos = pos.set(this.bottomLeft).move(Direction.UP, distanceToTop).move(this.rightDir, i);
            if (!FRAME.test(this.level.getBlockState(blockpos$mutableblockpos), this.level, blockpos$mutableblockpos)) {
                return false;
            }
        }

        return true;
    }

    private int getDistanceUntilTop(BlockPos.MutableBlockPos pos) {
        for(int i = 0; i < 21; ++i) {
            pos.set(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, -1);
            if (!FRAME.test(this.level.getBlockState(pos), this.level, pos)) {
                return i;
            }

            pos.set(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, this.width);
            if (!FRAME.test(this.level.getBlockState(pos), this.level, pos)) {
                return i;
            }

            for(int j = 0; j < this.width; ++j) {
                pos.set(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, j);
                BlockState blockstate = this.level.getBlockState(pos);
                if (!isEmpty(blockstate)) {
                    return i;
                }

                if (blockstate.is(ModBlocks.SCULK_PORTAL)) {
                    ++this.numPortalBlocks;
                }
            }
        }

        return 21;
    }

    private static boolean isEmpty(BlockState state) {
        return state.isAir() || state.is(BlockTags.FIRE) || state.is(ModBlocks.SCULK_PORTAL);
    }

    public boolean isValid() {
        return this.bottomLeft != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
    }

    public void createPortalBlocks() {
        BlockState blockstate = (BlockState) ModBlocks.SCULK_PORTAL.get().defaultBlockState().setValue(SculkPortalBlock.AXIS, this.axis);
        BlockPos.betweenClosed(this.bottomLeft, this.bottomLeft.relative(Direction.UP, this.height - 1).relative(this.rightDir, this.width - 1)).forEach((p_77725_) -> this.level.setBlock(p_77725_, blockstate, 18));
    }

    public boolean isComplete() {
        return this.isValid() && this.numPortalBlocks == this.width * this.height;
    }

    public static Vec3 getRelativePosition(BlockUtil.FoundRectangle foundRectangle, Direction.Axis axis, Vec3 pos, EntityDimensions entityDimensions) {
        double d0 = (double)foundRectangle.axis1Size - (double)entityDimensions.width();
        double d1 = (double)foundRectangle.axis2Size - (double)entityDimensions.height();
        BlockPos blockpos = foundRectangle.minCorner;
        double d2;
        if (d0 > (double)0.0F) {
            double d3 = (double)blockpos.get(axis) + (double)entityDimensions.width() / (double)2.0F;
            d2 = Mth.clamp(Mth.inverseLerp(pos.get(axis) - d3, (double)0.0F, d0), (double)0.0F, (double)1.0F);
        } else {
            d2 = (double)0.5F;
        }

        double d5;
        if (d1 > (double)0.0F) {
            Direction.Axis direction$axis = Direction.Axis.Y;
            d5 = Mth.clamp(Mth.inverseLerp(pos.get(direction$axis) - (double)blockpos.get(direction$axis), (double)0.0F, d1), (double)0.0F, (double)1.0F);
        } else {
            d5 = (double)0.0F;
        }

        Direction.Axis direction$axis1 = axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
        double d4 = pos.get(direction$axis1) - ((double)blockpos.get(direction$axis1) + (double)0.5F);
        return new Vec3(d2, d5, d4);
    }

    public static Vec3 findCollisionFreePosition(Vec3 pos, ServerLevel level, Entity entity, EntityDimensions dimensions) {
        if (!(dimensions.width() > 4.0F) && !(dimensions.height() > 4.0F)) {
            double d0 = (double)dimensions.height() / (double)2.0F;
            Vec3 vec3 = pos.add((double)0.0F, d0, (double)0.0F);
            VoxelShape voxelshape = Shapes.create(AABB.ofSize(vec3, (double)dimensions.width(), (double)0.0F, (double)dimensions.width()).expandTowards((double)0.0F, (double)1.0F, (double)0.0F).inflate(1.0E-6));
            Optional<Vec3> optional = level.findFreePosition(entity, voxelshape, vec3, (double)dimensions.width(), (double)dimensions.height(), (double)dimensions.width());
            Optional<Vec3> optional1 = optional.map((p_259019_) -> p_259019_.subtract((double)0.0F, d0, (double)0.0F));
            return (Vec3)optional1.orElse(pos);
        } else {
            return pos;
        }
    }
}
