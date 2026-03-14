package net.ronm19.sculky.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.ronm19.sculky.item.ModItems;
import org.jetbrains.annotations.NotNull;

public class SculkJarProjectileEntity extends ThrowableItemProjectile {

    public SculkJarProjectileEntity(EntityType<? extends SculkJarProjectileEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ModItems.SCULK_JAR.get();
    }

    @Override
    protected double getDefaultGravity() {
        return 0.05D;
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);

        if (this.level().isClientSide()) {
            return;
        }

        BlockPos center = BlockPos.containing(result.getLocation());

        spreadSculkPatch(center);

        this.level().levelEvent(2001, center, Block.getId(Blocks.SCULK.defaultBlockState()));
        this.discard();
    }

    private void spreadSculkPatch(BlockPos center) {
        Level level = this.level();
        int radius = 4 + level.random.nextInt(2); // 4-5 blocks, safer than full 10

        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt((x * x) + (z * z) + (y * y * 1.5D));

                    if (distance > radius + 0.35D) {
                        continue;
                    }

                    float chance = (float) Mth.clamp(1.0D - (distance / (radius + 0.5D)), 0.15D, 1.0D);
                    if (level.random.nextFloat() > chance) {
                        continue;
                    }

                    mutable.set(center.getX() + x, center.getY() + y, center.getZ() + z);
                    BlockState state = level.getBlockState(mutable);

                    // If it hits air, try to corrupt the block below it instead
                    if (state.isAir()) {
                        BlockPos below = mutable.below();
                        BlockState belowState = level.getBlockState(below);

                        if (canBecomeSculk(belowState)) {
                            level.setBlock(below, Blocks.SCULK.defaultBlockState(), 3);
                        }
                        continue;
                    }

                    if (canBecomeSculk(state)) {
                        level.setBlock(mutable, Blocks.SCULK.defaultBlockState(), 3);
                    }
                }
            }
        }
    }

    private boolean canBecomeSculk(BlockState state) {
        if (state.is(Blocks.SCULK)) return false;
        if (state.hasBlockEntity()) return false;
        if (!state.getFluidState().isEmpty()) return false;

        return state.is(Blocks.STONE)
                || state.is(Blocks.DEEPSLATE)
                || state.is(Blocks.COBBLED_DEEPSLATE)
                || state.is(Blocks.TUFF)
                || state.is(Blocks.ANDESITE)
                || state.is(Blocks.DIORITE)
                || state.is(Blocks.GRANITE)
                || state.is(Blocks.DIRT)
                || state.is(Blocks.COARSE_DIRT)
                || state.is(Blocks.GRASS_BLOCK)
                || state.is(Blocks.PODZOL)
                || state.is(Blocks.MYCELIUM);
    }
}