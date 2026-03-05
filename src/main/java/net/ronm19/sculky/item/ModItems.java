package net.ronm19.sculky.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.block.ModBlocks;
import net.ronm19.sculky.entity.ModEntities;
import net.ronm19.sculky.item.custom.*;

import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SculkyMod.MOD_ID);

    public static final DeferredItem<Item> SCULK_SHARD = ITEMS.registerSimpleItem("sculk_shard");
    public static final DeferredItem<Item> RAW_SCULK_SHARD = ITEMS.registerSimpleItem("raw_sculk_shard");

    public static final DeferredItem<Item> SCULK_BONE = ITEMS.registerSimpleItem("sculk_bone");

    public static final DeferredItem<Item> SCULK_RESONANCE =
            ITEMS.registerItem("sculk_resonance", Item::new, new Item.Properties().stacksTo(16).rarity(Rarity.RARE));
    public static final DeferredItem<Item> ECHO_DUST =
            ITEMS.registerItem("echo_dust", Item::new, new Item.Properties());
    public static final DeferredItem<Item> SCULK_FANG =
            ITEMS.registerItem("sculk_fang", Item::new, new Item.Properties());
    public static final DeferredItem<Item> SCULK_CHITIN =
            ITEMS.registerItem("sculk_chitin", Item::new, new Item.Properties());
    public static final DeferredItem<Item> SCULK_CORE =
            ITEMS.registerItem("sculk_core", Item::new, new Item.Properties());

    public static final DeferredItem<Item> SCULK_LANTERN = ITEMS.registerItem("sculk_lantern",
                    SculkLanternItem::new, new Item.Properties().stacksTo(1).rarity(Rarity.RARE));


    public static final DeferredItem<Item> SCULK_RAT_STAFF = ITEMS.register("sculk_rat_staff",
            () -> new SculkRatStaffItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> TOTEM_ECHO_RECALL = ITEMS.register("totem_echo_recall",
            () -> new EchoRecallTotemItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> TOTEM_SWARM = ITEMS.register("totem_swarm",
            () -> new SwarmTotemItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> TOTEM_SCULK_VEIL = ITEMS.register("totem_sculk_veil",
            () -> new SculkVeilTotemItem(new Item.Properties().stacksTo(1)));


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
    public static final DeferredItem<Item> SCULK_APPLE =
            ITEMS.registerItem("sculk_apple", Item::new, new Item.Properties().food(ModFoodProperties.SCULK_APPLE));


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
                    new Item.Properties().attributes(PickaxeItem.createAttributes(ModToolTiers.INFESTED_SCULK, 22, -3.3f))));

    public static final DeferredItem<Item> SCULK_EDGE = ITEMS.register("sculk_edge",
            () -> new SculkEdgeItem(ModToolTiers.INFESTED_SCULK,
                    new Item.Properties().attributes(SwordItem.createAttributes(ModToolTiers.INFESTED_SCULK, 20, -2.2f))));

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

    public static final DeferredItem<Item> ECHO_CONDUIT = ITEMS.register("echo_conduit",
            () -> new EchoConduitItem(new Item.Properties().stacksTo(1).durability(1300)));

    public static final DeferredItem<Item> ECHO_DAGGER = ITEMS.register("echo_dagger",
            () -> new EchoDaggerItem(ModToolTiers.INFESTED_SCULK, new Item.Properties().attributes
                    (SwordItem.createAttributes(ModToolTiers.INFESTED_SCULK, 8, -2.2f))));



    // ------------------------------- MONSTERs Egg Spawns --------------------------------------------- //

    public static final DeferredItem<Item> SCULK_PARASITE_SPAWN_EGG = ITEMS.register("sculk_parasite_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_PARASITE, 0x0a1019, 0x3cf0d0,
                    new Item.Properties()));

    public static final DeferredItem<Item> SCULK_SENTINEL_SPAWN_EGG = ITEMS.register("sculk_sentinel_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_SENTINEL, 0x0a0f14, 0x3df9ff,
                    new Item.Properties()));

    public static final DeferredItem<Item> SCULK_STALKER_SPAWN_EGG = ITEMS.register("sculk_stalker_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_STALKER, 0x0A0A0A, 0x0094FF,
                    new Item.Properties()));

    public static final DeferredItem<Item> SCULK_SHADE_SPAWN_EGG = ITEMS.register("sculk_shade_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_SHADE,  0x04070A, 0x2BF0FF,
                    new Item.Properties()));

    public static final DeferredItem<Item> SCULK_HORROR_SPAWN_EGG = ITEMS.register("sculk_horror_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_HORROR,  0x081A1F, 0x49F7FF,
                    new Item.Properties()));

    public static final DeferredItem<Item> SCULK_ZOMBIE_SPAWN_EGG = ITEMS.register("sculk_zombie_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_ZOMBIE,  0x08351E, 0x0CF1AE,
                    new Item.Properties()));

    public static final DeferredItem<Item> SCULK_HUSK_SPAWN_EGG = ITEMS.register("sculk_husk_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_HUSK, 0x07131D, 0x16D6D1,
                    new Item.Properties()));

    public static final DeferredItem<Item> SCULK_SKELETON_SPAWN_EGG = ITEMS.register("sculk_skeleton_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_SKELETON,  0x0B0F14, 0x2FE6D6,
                    new Item.Properties()));

    public static final DeferredItem<Item> SCULK_CREEPER_SPAWN_EGG = ITEMS.register("sculk_creeper_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_CREEPER,  0x0B0F12, 0x2FA6A3,
                    new Item.Properties()));

    public static final DeferredItem<Item> SCULK_SPIDER_SPAWN_EGG = ITEMS.register("sculk_spider_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_SPIDER,  0x0B1220, 0x19C7FF,
                    new Item.Properties()));

    public static final DeferredItem<Item> SCULK_SANDSNARE_SPAWN_EGG = ITEMS.register("sculk_sandsnare_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_SANDSNARE, 0x0A0F1C, 0x2AA6A6,
                    new Item.Properties()));



    public static final DeferredItem<Item> SCULK_ENDERMAN_SPAWN_EGG = ITEMS.register("sculk_enderman_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_ENDERMAN,  0x0B1416, 0x3FD6C6,
                    new Item.Properties()));

    public static final DeferredItem<Item> SCULKMITE_SPAWN_EGG = ITEMS.register("sculkmite_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULKMITE, 0x05080A, 0x1FA7A1,
                    new Item.Properties()));




    // ------------------------------- NEUTRAL Egg Spawns --------------------------------------------- //

    public static final DeferredItem<Item> SCULK_WOLF_SPAWN_EGG = ITEMS.register("sculk_wolf_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_WOLF, 0x0A0F12, 0x35D0E3,
                    new Item.Properties()));

    public static final DeferredItem<Item> SCULK_WOLF_ALPHA_SPAWN_EGG = ITEMS.register("sculk_wolf_alpha_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_WOLF_ALPHA, 0xB1930, 0xCF1AE,
                    new Item.Properties()));

    public static final DeferredItem<Item> SCULK_HORSE_SPAWN_EGG = ITEMS.register("sculk_horse_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_HORSE, 0x081418, 0x2AF2D2,
                    new Item.Properties()));

    public static final DeferredItem<Item> SCULK_FOX_SPAWN_EGG = ITEMS.register("sculk_fox_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_FOX, 0x0A0F12, 0x34D5E7,
                    new Item.Properties()));

    public static final DeferredItem<Item> SCULK_RAT_SPAWN_EGG = ITEMS.register(
            "sculk_rat_spawn_egg", () -> new DeferredSpawnEggItem(ModEntities.SCULK_RAT, 0x070B10, 0x2FE6E6,
                    new Item.Properties()));



    public static final DeferredItem<Item> SCULK_TAIL_SPAWN_EGG = ITEMS.register("sculk_tail_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_TAIL, 0x0A0F1C, 0x0A0F1C,
                    new Item.Properties()));

    public static final DeferredItem<Item> SCULK_BEETLE_SPAWN_EGG = ITEMS.register("sculk_beetle_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_BEETLE, 0x0A0F1C, 0x1FE4FF, new Item.Properties()));

    public static final DeferredItem<Item> SCULK_BAT_SPAWN_EGG = ITEMS.register("sculk_bat_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_BAT, 0x060A14, 0x0CF1AE,
                    new Item.Properties()));






    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
