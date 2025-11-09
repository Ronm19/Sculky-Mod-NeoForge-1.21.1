package net.ronm19.sculky.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.ronm19.sculky.api.interfaces.InfestedSculkTool;

public class InfestedSculkAxeItem extends AxeItem implements InfestedSculkTool {

    public InfestedSculkAxeItem(Tier tier, Properties properties) {
        super(tier, properties);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
        if (!level.isClientSide() && (
                state.is(Blocks.OAK_LOG) || state.is(Blocks.DARK_OAK_LOG) ||
                        state.is(Blocks.MANGROVE_LOG) || state.is(Blocks.SPRUCE_LOG))) {

            emitPulse(level, entity, 3); // weakness to nearby entities
            emitParticles(level, pos);
        }
        return super.mineBlock(stack, level, state, pos, entity);
    }

    @Override
    public boolean mineBlock( ItemStack stack, Level level, BlockState state, BlockPos pos, Player player ) {
        return true;
    }
}
