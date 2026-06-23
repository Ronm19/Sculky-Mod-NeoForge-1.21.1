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
import net.ronm19.sculky.sounds.ModSounds;

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

    public static final DeferredItem<Item> CROWN_FRAGMENT = ITEMS.registerItem("crown_fragment", Item::new, new Item.Properties());
    public static final DeferredItem<Item> ROYAL_SCULK_FRAGMENT = ITEMS.registerItem("royal_sculk_fragment", RoyalSculkFragmentItem::new, new Item.Properties());
    public static final DeferredItem<Item> ANCIENT_RESONANCE_CORE = ITEMS.registerItem("ancient_resonance_core", Item::new, new Item.Properties());
    public static final DeferredItem<Item> THRONE_SHARD = ITEMS.registerItem("throne_shard", Item::new, new Item.Properties());
    public static final DeferredItem<Item> ANCIENT_SCULK_TABLET = ITEMS.registerItem("ancient_sculk_tablet", AncientSculkTabletItem::new);

    public static final DeferredItem<Item> SCULK_FIN = ITEMS.registerItem("sculk_fin", Item::new, new Item.Properties());


    public static final DeferredItem<Item> SCULK_LANTERN = ITEMS.registerItem("sculk_lantern",
            SculkLanternItem::new, new Item.Properties().stacksTo(1).rarity(Rarity.RARE));

    public static final DeferredItem<Item> KING_RELIC = ITEMS.registerItem("king_relic",
            KingRelicItem::new, new Item.Properties().stacksTo(1));

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

    public static final DeferredItem<Item> SHADOW_PANTHER_THEME_MUSIC_DISC = ITEMS.registerItem("shadow_panther_theme_music_disc",
            properties -> new Item(properties.jukeboxPlayable(ModSounds.SHADOW_PANTHER_THEME_KEY).stacksTo(1).rarity(Rarity.RARE)));

    public static final DeferredItem<Item> ECHOES_OF_THE_CROWN_MUSIC_DISC = ITEMS.registerItem("echo_of_the_crown_music_disc",
            properties -> new Item(properties.jukeboxPlayable(ModSounds.ECHOES_OF_THE_CROWN_KEY).stacksTo(1).rarity(Rarity.RARE)));

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

    public static final DeferredItem<Item> SCULK_FANG_SCEPTER = ITEMS.register("sculk_fang_scepter",
            () -> new SculkFangScepterItem(new Item.Properties().stacksTo(1).durability(192)));

    public static final DeferredItem<Item> SCULK_BOW = ITEMS.register("sculk_bow",
            () -> new SculkBowItem(new  Item.Properties().stacksTo(1).durability(456)));

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

    public static final DeferredItem<Item> SCULK_BOOTS = ITEMS.register("sculk_boots",
            () -> new SculkBootsItem(ModArmorMaterials.INFESTED_SCULK, ArmorItem.Type.BOOTS,
                    new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(30))));

    public static final DeferredItem<Item> INFESTED_SCULK_HORSE_ARMOR = ITEMS.register("infested_sculk_horse_armor",
            () -> new AnimalArmorItem(ModArmorMaterials.INFESTED_SCULK, AnimalArmorItem.BodyType.EQUESTRIAN, false, new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> ECHO_CONDUIT = ITEMS.register("echo_conduit",
            () -> new EchoConduitItem(new Item.Properties().stacksTo(1).durability(1300)));

    public static final DeferredItem<Item> ECHO_DAGGER = ITEMS.register("echo_dagger",
            () -> new EchoDaggerItem(ModToolTiers.INFESTED_SCULK, new Item.Properties().attributes
                    (SwordItem.createAttributes(ModToolTiers.INFESTED_SCULK, 8, -2.2f))));

    public static final DeferredItem<Item> SCULK_CLEAVER = ITEMS.register("sculk_cleaver",
            () -> new ExecutionerCleaverItem(ModToolTiers.INFESTED_SCULK, new Item.Properties().attributes(AxeItem.createAttributes(ModToolTiers.INFESTED_SCULK, 14.0F, -3.6F))));

    public static final DeferredItem<Item> KINGS_AXE = ITEMS.register("kings_axe",
            () -> new KingsAxeItem(ModToolTiers.INFESTED_SCULK, new Item.Properties().attributes(AxeItem.createAttributes(ModToolTiers.INFESTED_SCULK, 18.0F, -3.5F))));

    public static final DeferredItem<Item> SCULK_SHIELD = ITEMS.register("sculk_shield",
            () -> new SculkShieldItem(new Item.Properties().durability(3000)));

    public static final DeferredItem<Item> SCULK_JAR = ITEMS.register("sculk_jar",
            () -> new SculkJarItem(new Item.Properties().stacksTo(16)));

    public static final DeferredItem<Item> SCULK_BOMB = ITEMS.register("sculk_bomb",
            () -> new SculkBombItem(new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON)));

    // ------------------------------- Monsters Egg Spawns --------------------------------------------- //

    public static final DeferredItem<Item> SCULK_PARASITE_SPAWN_EGG = ITEMS.register("sculk_parasite_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_PARASITE, 0x0a1019, 0x3cf0d0, new Item.Properties()));

    public static final DeferredItem<Item> SCULK_SENTINEL_SPAWN_EGG = ITEMS.register("sculk_sentinel_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_SENTINEL, 0x0a0f14, 0x3df9ff, new Item.Properties()));

    public static final DeferredItem<Item> SCULK_STALKER_SPAWN_EGG = ITEMS.register("sculk_stalker_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_STALKER, 0x0A0A0A, 0x0094FF, new Item.Properties()));

    public static final DeferredItem<Item> SCULK_SHADE_SPAWN_EGG = ITEMS.register("sculk_shade_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_SHADE,  0x04070A, 0x2BF0FF, new Item.Properties()));

    public static final DeferredItem<Item> SCULK_HORROR_SPAWN_EGG = ITEMS.register("sculk_horror_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_HORROR,  0x081A1F, 0x49F7FF, new Item.Properties()));

    public static final DeferredItem<Item> SCULK_ZOMBIE_SPAWN_EGG = ITEMS.register("sculk_zombie_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_ZOMBIE,  0x08351E, 0x0CF1AE, new Item.Properties()));

    public static final DeferredItem<Item> SCULK_HUSK_SPAWN_EGG = ITEMS.register("sculk_husk_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_HUSK, 0x07131D, 0x16D6D1, new Item.Properties()));

    public static final DeferredItem<Item> SCULK_SKELETON_SPAWN_EGG = ITEMS.register("sculk_skeleton_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_SKELETON,  0x0B0F14, 0x2FE6D6, new Item.Properties()));

    public static final DeferredItem<Item> SCULK_CREEPER_SPAWN_EGG = ITEMS.register("sculk_creeper_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_CREEPER,  0x0B0F12, 0x2FA6A3, new Item.Properties()));

    public static final DeferredItem<Item> SCULK_SPIDER_SPAWN_EGG = ITEMS.register("sculk_spider_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_SPIDER,  0x0B1220, 0x19C7FF, new Item.Properties()));

    public static final DeferredItem<Item> SCULK_SANDSNARE_SPAWN_EGG = ITEMS.register("sculk_sandsnare_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_SANDSNARE, 0x0A0F1C, 0x2AA6A6, new Item.Properties()));

    public static final DeferredItem<Item> SCULK_ENDERMAN_SPAWN_EGG = ITEMS.register("sculk_enderman_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_ENDERMAN,  0x0B1416, 0x3FD6C6, new Item.Properties()));

    public static final DeferredItem<Item> SCULKMITE_SPAWN_EGG = ITEMS.register("sculkmite_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULKMITE, 0x05080A, 0x1FA7A1, new Item.Properties()));

    public static final DeferredItem<Item> CROWNED_SCULKMITE_SPAWN_EGG = ITEMS.register("crowned_sculkmite_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.CROWNED_SCULKMITE, 0x071012, 0x29D6D0, new Item.Properties()));

    public static final DeferredItem<Item> SALVATORE_SPAWN_EGG = ITEMS.register("salvatore_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SALVATORE, 0x0A0D10, 0x1EC6BE, new Item.Properties()));

    public static final DeferredItem<Item> SCULK_PHANTOM_SPAWN_EGG = ITEMS.register("sculk_phantom_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_PHANTOM, 0x05080C, 0x4AE7E0, new Item.Properties()));

    public static final DeferredItem<Item> SANCTUM_WATCHER_SPAWN_EGG = ITEMS.register("sanctum_watcher_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SANCTUM_WATCHER, 0x0B0E12, 0x2FA8A3, new Item.Properties()));

    public static final DeferredItem<Item> ROYAL_SCULK_KNIGHT_SPAWN_EGG = ITEMS.register("royal_sculk_knight_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.ROYAL_SCULK_KNIGHT, 0x080B12, 0x1EC6BE, new Item.Properties()));

    public static final DeferredItem<Item> SCULK_NECROMANCER_SPAWN_EGG = ITEMS.register("sculk_necromancer_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_NECROMANCER, 0x1A1E2B, 0x3FD6D1, new Item.Properties()));

    public static final DeferredItem<Item> SCULK_BRUTE_SPAWN_EGG = ITEMS.register("sculk_brute_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_BRUTE, 0x0F1620, 0x4FE6E1, new Item.Properties()));

    public static final DeferredItem<Item> SCULK_BURROWER_SPAWN_EGG = ITEMS.register("sculk_burrower_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_BURROWER, 0x0A1218, 0x35D9D1, new Item.Properties()));

    public static final DeferredItem<Item> SCULK_SLIME_SPAWN_EGG = ITEMS.register("sculk_slime_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_SLIME, 0x0A0F1C, 0x1EC6BE, new Item.Properties()));

    public static final DeferredItem<Item> SCULK_HERALD_SPAWN_EGG = ITEMS.register("sculk_herald_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_HERALD, 0x061214, 0x00D8FF, new Item.Properties()));

    public static final DeferredItem<Item> SCULK_SNAPPER_SPAWN_EGG = ITEMS.register("sculk_snapper_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_SNAPPER, 0x071014, 0x00D8E8, new Item.Properties()));

    public static final DeferredItem<Item> SCULK_VINDICATOR_SPAWN_EGG = ITEMS.register("sculk_vindicator_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_VINDICATOR, 0x1f252b, 0x25c8d8, new Item.Properties()));

    public static final DeferredItem<Item> SCULK_SENTRY_SPAWN_EGG = ITEMS.register("sculk_sentry_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_SENTRY, 0x20262E, 0x25C8D8, new Item.Properties()));

    public static final DeferredItem<Item> SCULK_SPIRIT_SPAWN_EGG = ITEMS.register("sculk_spirit_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_SPIRIT, 0x17232D, 0x2EDDEA, new Item.Properties()));

    public static final DeferredItem<Item> SCULK_EVOKER_SPAWN_EGG = ITEMS.register("sculk_evoker_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_EVOKER, 0x111821, 0x2EDDEA, new Item.Properties()));

    public static final DeferredItem<Item> SCULK_EXECUTIONER_SPAWN_EGG = ITEMS.register("sculk_executioner_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_EXECUTIONER, 0x11191A, 0x1AA7B5, new Item.Properties()));

    public static final DeferredItem<Item> SCULK_BULWARK_SPAWN_EGG = ITEMS.register("sculk_bulwark_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_BULWARK, 0x11191A, 0x5B4A35, new Item.Properties()));

    public static final DeferredItem<Item> SCULK_ORACLE_SPAWN_EGG = ITEMS.register("sculk_oracle_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_ORACLE, 0x0B1116, 0x18D7F2, new Item.Properties()));

    public static final DeferredItem<Item> THRONEBOUND_WRAITH_SPAWN_EGG = ITEMS.register("thronebound_wraith_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.THRONEBOUND_WRAITH, 0x0A0E12, 0x22D7F2, new Item.Properties()));

    public static final DeferredItem<Item> SCULK_KING_SPAWN_EGG = ITEMS.register("sculk_king_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_KING, 0x07191D, 0x18D8E8, new Item.Properties()));

    // ------------------------------- NEUTRAL Egg Spawns --------------------------------------------- //

    public static final DeferredItem<Item> SCULK_WOLF_SPAWN_EGG = ITEMS.register("sculk_wolf_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_WOLF, 0x0A0F12, 0x35D0E3, new Item.Properties()));

    public static final DeferredItem<Item> SCULK_WOLF_ALPHA_SPAWN_EGG = ITEMS.register("sculk_wolf_alpha_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_WOLF_ALPHA, 0xB1930, 0xCF1AE, new Item.Properties()));

    public static final DeferredItem<Item> SCULK_HORSE_SPAWN_EGG = ITEMS.register("sculk_horse_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_HORSE, 0x081418, 0x2AF2D2, new Item.Properties()));

    public static final DeferredItem<Item> SCULK_FOX_SPAWN_EGG = ITEMS.register("sculk_fox_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_FOX, 0x0A0F12, 0x34D5E7, new Item.Properties()));

    public static final DeferredItem<Item> SCULK_RAT_SPAWN_EGG = ITEMS.register("sculk_rat_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_RAT, 0x070B10, 0x2FE6E6, new Item.Properties()));

    public static final DeferredItem<Item> SCULK_TAIL_SPAWN_EGG = ITEMS.register("sculk_tail_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_TAIL, 0x0A0F1C, 0x0A0F1C, new Item.Properties()));

    public static final DeferredItem<Item> SCULK_BEETLE_SPAWN_EGG = ITEMS.register("sculk_beetle_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_BEETLE, 0x0A0F1C, 0x1FE4FF, new Item.Properties()));

    public static final DeferredItem<Item> SCULK_BAT_SPAWN_EGG = ITEMS.register("sculk_bat_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_BAT, 0x060A14, 0x0CF1AE, new Item.Properties()));

    public static final DeferredItem<Item> HOLLOW_HORN_SPAWN_EGG = ITEMS.register("hollow_horn_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.HOLLOW_HORN, 0x8A8468, 0x66D6D1, new Item.Properties()));

    public static final DeferredItem<Item> INFESTED_EYE_SPAWN_EGG = ITEMS.register("infested_eye_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.INFESTED_EYE, 0x08282B, 0x7CEAF3, new Item.Properties()));

    public static final DeferredItem<Item> SCULK_HUNTER_SPAWN_EGG = ITEMS.register("sculk_hunter_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_HUNTER, 0x0B1012, 0x7CEAF3, new Item.Properties()));

    public static final DeferredItem<Item> SHADOW_PANTHER_SPAWN_EGG = ITEMS.register("shadow_panther_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SHADOW_PANTHER, 0x050709, 0x2ED0DC, new Item.Properties()));

    public static final DeferredItem<Item> SCULK_GOLEM_SPAWN_EGG = ITEMS.register("sculk_golem_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_GOLEM, 0x5B5044, 0x42D0CB, new Item.Properties()));

    public static final DeferredItem<Item> SCULK_DOLPHIN_SPAWN_EGG = ITEMS.register("sculk_dolphin_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_DOLPHIN,  0x07131A, 0x00D7E8, new Item.Properties()));

    public static final DeferredItem<Item> SCULK_BEAR_SPAWN_EGG = ITEMS.register("sculk_bear_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SCULK_BEAR, 0x0B1218, 0x18D7F2, new Item.Properties()));




    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
