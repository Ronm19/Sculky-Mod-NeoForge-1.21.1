package net.ronm19.sculky.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SculkBowItem extends BowItem {
    public SculkBowItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getDefaultProjectileRange() {
        return 24;
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull Item.TooltipContext context,
            @NotNull List<Component> tooltip,
            @NotNull TooltipFlag flag
    ) {
        tooltip.add(Component.literal("Arrows fly faster and carry sculk energy.")
                .withStyle(ChatFormatting.DARK_AQUA));

        tooltip.add(Component.literal("Applies Darkness and Slowness on hit.")
                .withStyle(ChatFormatting.GRAY));

        super.appendHoverText(stack, context, tooltip, flag);
    }
}