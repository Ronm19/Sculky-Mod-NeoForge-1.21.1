package net.ronm19.sculky.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Set;

public class SculkBootsItem extends ArmorItem {

    private static final int EFFECT_REFRESH_TICKS = 10;

    // Safety platform check every tick
    private static final int SAFETY_INTERVAL_TICKS = 1;

    // Decorative side spread can stay slower
    private static final int SIDE_SPREAD_INTERVAL_TICKS = 4;
    private static final float SIDE_SPREAD_CHANCE = 0.35F;

    public SculkBootsItem(Holder<ArmorMaterial> material, ArmorItem.Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);

        if (level.isClientSide()) return;
        if (!(entity instanceof Player player)) return;
        if (player.isSpectator()) return;

        ItemStack equippedFeet = player.getItemBySlot(EquipmentSlot.FEET);
        if (equippedFeet != stack) return;

        // Speed I while worn
        player.addEffect(new MobEffectInstance(
                MobEffects.MOVEMENT_SPEED,
                EFFECT_REFRESH_TICKS,
                0,   // amplifier 0 = Speed I
                true,
                false,
                true
        ));

        if (player.isFallFlying()) return;

        boolean moving = player.getDeltaMovement().horizontalDistanceSqr() > 0.0009D;

        // 1) Main safety platform every tick
        if (player.tickCount % SAFETY_INTERVAL_TICKS == 0) {
            ensureSafePlatform(level, player);
        }

        // 2) Decorative/random side spread while moving
        if (moving && player.tickCount % SIDE_SPREAD_INTERVAL_TICKS == 0) {
            BlockPos center = BlockPos.containing(
                    player.getX(),
                    player.getBoundingBox().minY - 0.15D,
                    player.getZ()
            );

            for (Direction dir : Direction.Plane.HORIZONTAL) {
                if (level.random.nextFloat() <= SIDE_SPREAD_CHANCE) {
                    tryConvertAtOrBelow(level, center.relative(dir), false);
                }
            }
        }
    }

    private void ensureSafePlatform(Level level, Player player) {
        Set<BlockPos> positions = new HashSet<>();

        double minX = player.getBoundingBox().minX + 0.05D;
        double maxX = player.getBoundingBox().maxX - 0.05D;
        double minZ = player.getBoundingBox().minZ + 0.05D;
        double maxZ = player.getBoundingBox().maxZ - 0.05D;
        double y = player.getBoundingBox().minY - 0.15D;

        // Center + four corners = much safer footing
        positions.add(BlockPos.containing(player.getX(), y, player.getZ()));
        positions.add(BlockPos.containing(minX, y, minZ));
        positions.add(BlockPos.containing(minX, y, maxZ));
        positions.add(BlockPos.containing(maxX, y, minZ));
        positions.add(BlockPos.containing(maxX, y, maxZ));

        boolean rescuedFromFluid = false;

        for (BlockPos pos : positions) {
            if (tryConvertAtOrBelow(level, pos, true)) {
                rescuedFromFluid = true;
            }
        }

        // If we had to save the player on fluid/lava, cancel fall distance
        if (rescuedFromFluid) {
            player.fallDistance = 0.0F;
        }
    }

    private boolean tryConvertAtOrBelow(Level level, BlockPos pos, boolean safetyMode) {
        if (tryConvert(level, pos, safetyMode)) return true;
        return tryConvert(level, pos.below(), safetyMode);
    }

    private boolean tryConvert(Level level, BlockPos pos, boolean safetyMode) {
        if (!level.isInWorldBounds(pos)) return false;

        BlockState state = level.getBlockState(pos);

        if (state.is(Blocks.SCULK)) return false;
        if (state.hasBlockEntity()) return false;
        if (state.is(Blocks.BEDROCK)) return false;
        if (state.is(Blocks.OBSIDIAN)) return false;
        if (state.is(Blocks.CRYING_OBSIDIAN)) return false;
        if (state.is(Blocks.NETHER_PORTAL) || state.is(Blocks.END_PORTAL) || state.is(Blocks.END_PORTAL_FRAME)) return false;

        // Water / lava support
        if (isWaterOrLava(state)) {
            level.setBlock(pos, Blocks.SCULK.defaultBlockState(), 3);

            // Extra support in safety mode so player doesn't slip off edges
            if (safetyMode) {
                for (Direction dir : Direction.Plane.HORIZONTAL) {
                    BlockPos side = pos.relative(dir);
                    BlockState sideState = level.getBlockState(side);

                    if (isWaterOrLava(sideState)) {
                        level.setBlock(side, Blocks.SCULK.defaultBlockState(), 3);
                    }
                }
            }

            return true;
        }

        // Safe natural blocks to corrupt
        if (canCorruptIntoSculk(state)) {
            level.setBlock(pos, Blocks.SCULK.defaultBlockState(), 3);
            return false;
        }

        return false;
    }

    private boolean isWaterOrLava(BlockState state) {
        return state.getFluidState().is(FluidTags.WATER) || state.getFluidState().is(FluidTags.LAVA);
    }

    private boolean canCorruptIntoSculk(BlockState state) {
        if (state.isAir()) return false;
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
                || state.is(Blocks.ROOTED_DIRT)
                || state.is(Blocks.GRASS_BLOCK)
                || state.is(Blocks.MYCELIUM)
                || state.is(Blocks.PODZOL)
                || state.is(Blocks.SAND)
                || state.is(Blocks.RED_SAND)
                || state.is(Blocks.GRAVEL)
                || state.is(Blocks.MUD)
                || state.is(Blocks.CLAY);
    }
}