package net.ronm19.sculky.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RoyalSculkFragmentItem extends Item {
    public RoyalSculkFragmentItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(Component.translatable("tooltip.sculky.royal_sculk_fragment")
                .withStyle(ChatFormatting.DARK_AQUA));

        super.appendHoverText(stack, context, tooltip, flag);
    }
}