package net.ronm19.sculky.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.block.ModBlocks;
import net.ronm19.sculky.item.custom.*;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SculkyMod.MOD_ID);

    public static final DeferredItem<Item> SCULK_SHARD = ITEMS.registerSimpleItem("sculk_shard");
    public static final DeferredItem<Item> RAW_SCULK_SHARD = ITEMS.registerSimpleItem("raw_sculk_shard");

    public static final DeferredItem<Item> SCULK_HEARTFRUIT =
            ITEMS.registerItem("sculk_heartfruit", Item::new, new Item.Properties().food(ModFoodProperties.SCULK_HEARTFRUIT));
    public static final DeferredItem<Item> TOMATO_SCULK =
            ITEMS.registerItem("tomato_sculk", Item::new, new Item.Properties().food(ModFoodProperties.TOMATO_SCULK));
    public static final DeferredItem<Item> ECHO_JELLY =
            ITEMS.registerItem("echo_jelly", Item::new, new Item.Properties().food(ModFoodProperties.ECHO_JELLY));
    public static final DeferredItem<Item> SCULK_PASTRY  =
            ITEMS.registerItem("sculk_pastry", Item::new, new Item.Properties().food(ModFoodProperties.SCULK_PASTRY));
    public static final DeferredItem<Item> SOULBITE_COOKIE =
            ITEMS.registerItem("soulbite_cookie", Item::new, new Item.Properties().food(ModFoodProperties.SOULBITE_COOKIE));

    // === Infested Sculk Tool Set ===
    public static final DeferredItem<Item> INFESTED_SCULK_SWORD = ITEMS.register("infested_sculk_sword",
            () -> new InfestedSculkSwordItem(ModToolTiers.INFESTED_SCULK,
                    new Item.Properties().attributes(SwordItem.createAttributes(ModToolTiers.INFESTED_SCULK, 5, -2.4f))));

    public static final DeferredItem<Item> INFESTED_SCULK_PICKAXE = ITEMS.register("infested_sculk_pickaxe",
            () -> new InfestedSculkPickaxeItem(ModToolTiers.INFESTED_SCULK,
                    new Item.Properties().attributes(PickaxeItem.createAttributes(ModToolTiers.INFESTED_SCULK, 1, -2.8f))));

    public static final DeferredItem<Item> INFESTED_SCULK_SHOVEL = ITEMS.register("infested_sculk_shovel",
            () -> new InfestedSculkShovelItem(ModToolTiers.INFESTED_SCULK,
                    new Item.Properties().attributes(ShovelItem.createAttributes(ModToolTiers.INFESTED_SCULK, 1.5f, -3.0f))));

    public static final DeferredItem<Item> INFESTED_SCULK_AXE = ITEMS.register("infested_sculk_axe",
            () -> new InfestedSculkAxeItem(ModToolTiers.INFESTED_SCULK,
                    new Item.Properties().attributes(AxeItem.createAttributes(ModToolTiers.INFESTED_SCULK, 7, -3.2f))));

    public static final DeferredItem<Item> INFESTED_SCULK_HOE = ITEMS.register("infested_sculk_hoe",
            () -> new InfestedSculkHoeItem(ModToolTiers.INFESTED_SCULK,
                    new Item.Properties().attributes(HoeItem.createAttributes(ModToolTiers.INFESTED_SCULK, 0, -3.0f))));



    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
