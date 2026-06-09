package net.ronm19.sculky.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class AncientSculkTabletItem extends Item {

    public AncientSculkTabletItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.sculky.ancient_sculk_tablet")
                .withStyle(ChatFormatting.DARK_AQUA, ChatFormatting.ITALIC));

        super.appendHoverText(stack, context, tooltip, flag);
    }
}