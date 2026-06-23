package net.ronm19.sculky.item.custom;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import org.jetbrains.annotations.NotNull;

public class SculkShieldItem extends ShieldItem {
    public SculkShieldItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack stack, @NotNull ItemStack repairCandidate) {
        return repairCandidate.is(Items.ECHO_SHARD) || super.isValidRepairItem(stack, repairCandidate);
    }
}