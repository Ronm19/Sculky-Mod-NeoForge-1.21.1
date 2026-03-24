package net.ronm19.sculky.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.ronm19.sculky.block.ModBlocks;
import net.ronm19.sculky.portal.SculkPortalShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Optional;

public class SculkPortalBlock extends Block implements Portal, SimpleWaterloggedBlock {
    public static final MapCodec<SculkPortalBlock> CODEC = simpleCodec(SculkPortalBlock::new);
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;

    private static final VoxelShape X_AXIS_AABB = Block.box(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
    private static final VoxelShape Z_AXIS_AABB = Block.box(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);

    public SculkPortalBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.X));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(AXIS) == Direction.Axis.Z ? Z_AXIS_AABB : X_AXIS_AABB;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.dimensionType().natural()
                && level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)
                && random.nextInt(2000) < level.getDifficulty().getId()) {
            while (level.getBlockState(pos).is(this)) {
                pos = pos.below();
            }
        }
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        Direction.Axis facingAxis = facing.getAxis();
        Direction.Axis portalAxis = state.getValue(AXIS);
        boolean wrongHorizontalBreak = portalAxis != facingAxis && facingAxis.isHorizontal();

        return !wrongHorizontalBreak
                && !facingState.is(this)
                && !(new SculkPortalShape(level, currentPos, portalAxis)).isComplete()
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity.canUsePortal(false)) {
            entity.setAsInsidePortal(this, pos);
        }
    }

    @Override
    public int getPortalTransitionTime(ServerLevel level, Entity entity) {
        if (entity instanceof Player player) {
            return Math.max(
                    1,
                    level.getGameRules().getInt(
                            player.getAbilities().invulnerable
                                    ? GameRules.RULE_PLAYERS_NETHER_PORTAL_CREATIVE_DELAY
                                    : GameRules.RULE_PLAYERS_NETHER_PORTAL_DEFAULT_DELAY
                    )
            );
        }
        return 0;
    }

    @Nullable
    @Override
    public DimensionTransition getPortalDestination(ServerLevel level, Entity entity, BlockPos pos) {
        ResourceKey<Level> SCULK_SANCTUM = ResourceKey.create(
                Registries.DIMENSION,
                ResourceLocation.fromNamespaceAndPath("sculky", "sculk_sanctum")
        );

        ServerLevel targetLevel = level.dimension() == SCULK_SANCTUM
                ? level.getServer().getLevel(Level.OVERWORLD)
                : level.getServer().getLevel(SCULK_SANCTUM);

        if (targetLevel == null) {
            return null;
        }

        BlockPos safeSpot = findEquivalentArrivalPos(level, targetLevel, entity.position(), entity);

        Direction.Axis axis = level.getBlockState(pos)
                .getOptionalValue(AXIS)
                .orElse(Direction.Axis.X);

        // CHANGED: spawn player in front of the created portal if possible
        BlockPos finalSpawn = createReturnPortalNear(targetLevel, safeSpot, axis, entity);
        if (finalSpawn == null) {
            finalSpawn = safeSpot;
        }

        return new DimensionTransition(
                targetLevel,
                new Vec3(finalSpawn.getX() + 0.5D, finalSpawn.getY() + 0.1D, finalSpawn.getZ() + 0.5D),
                Vec3.ZERO,
                entity.getYRot(),
                entity.getXRot(),
                DimensionTransition.PLAY_PORTAL_SOUND.then(DimensionTransition.PLACE_PORTAL_TICKET)
        );
    }

    private BlockPos findEquivalentArrivalPos(ServerLevel sourceLevel, ServerLevel targetLevel, Vec3 sourcePos, Entity entity) {
        BlockPos baseXZ = BlockPos.containing(sourcePos.x, 0, sourcePos.z);

        BlockPos surface = targetLevel.getHeightmapPos(
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                baseXZ
        );

        BlockPos direct = findSafeStandPos(targetLevel, surface, entity);
        if (direct != null) {
            return direct;
        }

        BlockPos nearby = findSafePosNear(targetLevel, surface, 16);
        if (nearby != null) {
            return nearby;
        }

        return surface.above();
    }

    @Nullable
    private BlockPos findSafeStandPos(ServerLevel level, BlockPos pos, Entity entity) {
        if (!isSafeStandPosition(level, pos)) {
            return null;
        }

        if (canEntityFitAt(level, pos, entity)) {
            return pos;
        }

        return null;
    }

    private boolean canEntityFitAt(ServerLevel level, BlockPos pos, Entity entity) {
        EntityDimensions dims = entity.getDimensions(entity.getPose());

        double x = pos.getX() + 0.5D;
        double y = pos.getY();
        double z = pos.getZ() + 0.5D;

        var testBox = dims.makeBoundingBox(x, y, z);
        return level.noCollision(entity, testBox);
    }

    @Nullable
    private static BlockPos findSafePosNear(ServerLevel level, BlockPos center, int radius) {
        for (int r = 0; r <= radius; r++) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {
                    if (r != 0 && Math.abs(dx) != r && Math.abs(dz) != r) continue;

                    BlockPos columnPos = new BlockPos(center.getX() + dx, 0, center.getZ() + dz);
                    BlockPos heightPos = level.getHeightmapPos(
                            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                            columnPos
                    );

                    BlockPos feetPos = heightPos;

                    if (!level.getBlockState(feetPos).isAir()) {
                        feetPos = feetPos.above();
                    }

                    if (isSafeStandPosition(level, feetPos)) {
                        return feetPos;
                    }
                }
            }
        }

        return null;
    }

    private static boolean isSafeStandPosition(ServerLevel level, BlockPos feetPos) {
        BlockPos belowPos = feetPos.below();
        BlockPos headPos = feetPos.above();

        BlockState belowState = level.getBlockState(belowPos);
        BlockState feetState = level.getBlockState(feetPos);
        BlockState headState = level.getBlockState(headPos);

        return belowState.isFaceSturdy(level, belowPos, Direction.UP)
                && isOpenForPlayer(level, feetPos, feetState)
                && isOpenForPlayer(level, headPos, headState);
    }

    private static boolean isOpenForPlayer(ServerLevel level, BlockPos pos, BlockState state) {
        return state.getCollisionShape(level, pos).isEmpty()
                && state.getFluidState().isEmpty();
    }

    @Nullable
    private BlockPos createReturnPortalNear(ServerLevel level, BlockPos center, Direction.Axis axis, Entity entity) {
        for (int r = 0; r <= 6; r++) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {
                    if (r != 0 && Math.abs(dx) != r && Math.abs(dz) != r) continue;

                    BlockPos column = new BlockPos(center.getX() + dx, 0, center.getZ() + dz);
                    BlockPos surface = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, column);

                    // bottom-left INTERIOR of the 2x3 portal
                    BlockPos bottomLeft = surface.above();

                    if (!canBuildPortalFrameAt(level, bottomLeft, axis)) {
                        continue;
                    }

                    buildPortalFrame(level, bottomLeft, axis);

                    Optional<SculkPortalShape> shape = SculkPortalShape.findEmptyPortalShape(level, bottomLeft, axis);
                    if (shape.isEmpty()) {
                        continue;
                    }

                    shape.get().createPortalBlocks();

                    // CHANGED: return a safe spawn in front of portal
                    BlockPos spawnPos = findSpawnInFrontOfPortal(level, bottomLeft, axis, entity);
                    if (spawnPos != null) {
                        return spawnPos;
                    }

                    // fallback if front-of-portal failed for some reason
                    BlockPos fallback = findSafePosNear(level, bottomLeft, 3);
                    if (fallback != null) {
                        return fallback;
                    }

                    return bottomLeft;
                }
            }
        }

        return null;
    }

    @Nullable
    private BlockPos findSpawnInFrontOfPortal(ServerLevel level, BlockPos bottomLeft, Direction.Axis axis, Entity entity) {
        Direction right = axis == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;

        Direction frontA = axis == Direction.Axis.X ? Direction.SOUTH : Direction.EAST;
        Direction frontB = frontA.getOpposite();

        BlockPos candidate1 = bottomLeft.relative(right).relative(frontA);
        BlockPos candidate2 = bottomLeft.relative(right).relative(frontB);
        BlockPos candidate3 = bottomLeft.relative(frontA);
        BlockPos candidate4 = bottomLeft.relative(frontB);

        BlockPos safe1 = findSafeStandPos(level, candidate1, entity);
        if (safe1 != null) return safe1;

        BlockPos safe2 = findSafeStandPos(level, candidate2, entity);
        if (safe2 != null) return safe2;

        BlockPos safe3 = findSafeStandPos(level, candidate3, entity);
        if (safe3 != null) return safe3;

        BlockPos safe4 = findSafeStandPos(level, candidate4, entity);
        if (safe4 != null) return safe4;

        return findSafePosNear(level, bottomLeft.relative(right), 3);
    }

    private boolean canBuildPortalFrameAt(ServerLevel level, BlockPos bottomLeft, Direction.Axis axis) {
        Direction right = axis == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;

        for (int y = -1; y <= 3; y++) {
            for (int w = -1; w <= 2; w++) {
                BlockPos checkPos = bottomLeft.above(y).relative(right, w);
                BlockState state = level.getBlockState(checkPos);

                boolean isFrame = y == -1 || y == 3 || w == -1 || w == 2;

                if (isFrame) {
                    if (!state.canBeReplaced() && !state.is(ModBlocks.CHISELED_ANCIENT_SCULK_BRICKS.get())) {
                        return false;
                    }
                } else {
                    if (!state.canBeReplaced() && !state.is(ModBlocks.SCULK_PORTAL.get())) {
                        return false;
                    }
                }
            }
        }

        BlockPos belowLeft = bottomLeft.below();
        BlockPos belowRight = bottomLeft.relative(right).below();

        return level.getBlockState(belowLeft).blocksMotion()
                && level.getBlockState(belowRight).blocksMotion();
    }

    private void buildPortalFrame(ServerLevel level, BlockPos bottomLeft, Direction.Axis axis) {
        Direction right = axis == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;
        BlockState frame = ModBlocks.CHISELED_ANCIENT_SCULK_BRICKS.get().defaultBlockState();

        for (int y = -1; y <= 3; y++) {
            for (int w = -1; w <= 2; w++) {
                boolean isFrame = y == -1 || y == 3 || w == -1 || w == 2;
                if (!isFrame) continue;

                BlockPos framePos = bottomLeft.above(y).relative(right, w);
                level.setBlock(framePos, frame, 3);
            }
        }
    }

    @Override
    public Portal.@NotNull Transition getLocalTransition() {
        return Transition.CONFUSION;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(100) == 0) {
            level.playLocalSound(
                    pos.getX() + 0.5D,
                    pos.getY() + 0.5D,
                    pos.getZ() + 0.5D,
                    SoundEvents.PORTAL_AMBIENT,
                    SoundSource.BLOCKS,
                    0.5F,
                    random.nextFloat() * 0.4F + 0.8F,
                    false
            );
        }

        for (int i = 0; i < 4; ++i) {
            double d0 = pos.getX() + random.nextDouble();
            double d1 = pos.getY() + random.nextDouble();
            double d2 = pos.getZ() + random.nextDouble();
            double d3 = (random.nextFloat() - 0.5F) * 0.5D;
            double d4 = (random.nextFloat() - 0.5F) * 0.5D;
            double d5 = (random.nextFloat() - 0.5F) * 0.5D;
            int j = random.nextInt(2) * 2 - 1;

            if (!level.getBlockState(pos.west()).is(this) && !level.getBlockState(pos.east()).is(this)) {
                d0 = pos.getX() + 0.5D + 0.25D * j;
                d3 = random.nextFloat() * 2.0F * j;
            } else {
                d2 = pos.getZ() + 0.5D + 0.25D * j;
                d5 = random.nextFloat() * 2.0F * j;
            }

            level.addParticle(ParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5);
        }
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return ItemStack.EMPTY;
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rot) {
        return switch (rot) {
            case COUNTERCLOCKWISE_90, CLOCKWISE_90 -> switch (state.getValue(AXIS)) {
                case Z -> state.setValue(AXIS, Direction.Axis.X);
                case X -> state.setValue(AXIS, Direction.Axis.Z);
                default -> state;
            };
            default -> state;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }
}