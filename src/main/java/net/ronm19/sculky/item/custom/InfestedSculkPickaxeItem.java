package net.ronm19.sculky.item.custom;


import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.ronm19.sculky.api.interfaces.InfestedSculkTool;

public class InfestedSculkPickaxeItem extends PickaxeItem implements InfestedSculkTool {

    public InfestedSculkPickaxeItem( Tier tier, Properties properties ) {
        super(tier, properties);
    }

    @Override
    public boolean mineBlock( ItemStack stack, Level level, BlockState state, net.minecraft.core.BlockPos pos, Player player ) {
        onMineBlock(level, state, pos, player); // calls your interface behavior
        return super.mineBlock(stack, level, state, pos, player);
    }
}
