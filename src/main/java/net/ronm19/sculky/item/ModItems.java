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

    public static final DeferredItem<Item> TOMATO_SCULK_SEEDS = ITEMS.register("tomato_sculk_seeds",
            () -> new ItemNameBlockItem(ModBlocks.TOMATO_SCULK_CROP.get(), new Item.Properties()));

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

    public static final DeferredItem<Item> INFESTED_SCULK_HAMMER = ITEMS.register("infested_sculk_hammer",
            () -> new InfestedSculkHammerItem(ModToolTiers.INFESTED_SCULK,
                    new Item.Properties().attributes(PickaxeItem.createAttributes(ModToolTiers.INFESTED_SCULK, 21, -3.3f))));

    public static final DeferredItem<Item> INFESTED_SCULK_HELMET = ITEMS.register("infested_sculk_helmet",
            () -> new ModArmorItem(ModArmorMaterials.INFESTED_SCULK, ArmorItem.Type.HELMET,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(20))));

    public static final DeferredItem<Item> INFESTED_SCULK_CHESTPLATE = ITEMS.register("infested_sculk_chestplate",
            () -> new ArmorItem(ModArmorMaterials.INFESTED_SCULK, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(20))));

    public static final DeferredItem<Item> INFESTED_SCULK_LEGGINGS = ITEMS.register("infested_sculk_leggings",
            () -> new ArmorItem(ModArmorMaterials.INFESTED_SCULK, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(20))));

    public static final DeferredItem<Item> INFESTED_SCULK_BOOTS = ITEMS.register("infested_sculk_boots",
            () -> new ArmorItem(ModArmorMaterials.INFESTED_SCULK, ArmorItem.Type.BOOTS,
                    new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(20))));

    public static final DeferredItem<Item> INFESTED_SCULK_HORSE_ARMOR = ITEMS.register("infested_sculk_horse_armor",
            () -> new AnimalArmorItem(ModArmorMaterials.INFESTED_SCULK, AnimalArmorItem.BodyType.EQUESTRIAN, false, new Item.Properties().stacksTo(1)));




    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
