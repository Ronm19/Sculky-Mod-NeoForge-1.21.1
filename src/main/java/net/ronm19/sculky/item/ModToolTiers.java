package net.ronm19.sculky.item;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;
import net.ronm19.sculky.util.ModTags;

public class ModToolTiers {
    public static final Tier INFESTED_SCULK = new SimpleTier(ModTags.Blocks.INCORRECT_FOR_INFESTED_SCULK_TOOL,
            2500, 2.0F, 4.0F, 25, () -> Ingredient.of(ModItems.SCULK_SHARD.get()));

}