package net.ronm19.sculky.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.block.ModBlocks;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SculkyMod.MOD_ID);

    public static final Supplier<CreativeModeTab> SCULKY_ITEMS_TAB =
            CREATIVE_MODE_TABS.register("sculky_items_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.sculky.sculky_items_tab"))
                    .icon(() -> new ItemStack(ModItems.SCULK_SHARD.get()))
                    .displayItems((pParameters, pOutput) -> {

                        pOutput.accept(ModItems.SCULK_SHARD);
                        pOutput.accept(ModItems.RAW_SCULK_SHARD);
                        pOutput.accept(ModItems.SCULK_HEARTFRUIT);
                        pOutput.accept(ModItems.TOMATO_SCULK);
                        pOutput.accept(ModItems.ECHO_JELLY);
                        pOutput.accept(ModItems.SCULK_PASTRY);
                        pOutput.accept(ModItems.SOULBITE_COOKIE);

                        pOutput.accept(ModItems.TOMATO_SCULK_SEEDS);
                        pOutput.accept(ModItems.SCULK_BONE);
                        pOutput.accept(ModItems.SCULK_RESONANCE);
                        pOutput.accept(ModItems.ECHO_DUST);
                        pOutput.accept(ModItems.SCULK_FANG);
                        pOutput.accept(ModItems.SCULK_CHITIN);

                        pOutput.accept(ModItems.TOTEM_ECHO_RECALL);
                        pOutput.accept(ModItems.TOTEM_SWARM);
                        pOutput.accept(ModItems.TOTEM_SCULK_VEIL);

                        pOutput.accept(ModItems.INFESTED_SCULK_SWORD);
                        pOutput.accept(ModItems.INFESTED_SCULK_PICKAXE);
                        pOutput.accept(ModItems.INFESTED_SCULK_AXE);
                        pOutput.accept(ModItems.INFESTED_SCULK_SHOVEL);
                        pOutput.accept(ModItems.INFESTED_SCULK_HOE);
                        pOutput.accept(ModItems.INFESTED_SCULK_HAMMER);
                        pOutput.accept(ModItems.SCULK_EDGE);
                        pOutput.accept(ModItems.ECHO_CONDUIT);
                        pOutput.accept(ModItems.ECHO_DAGGER);
                        pOutput.accept(ModItems.SCULK_RAT_STAFF);

                        pOutput.accept(ModItems.INFESTED_SCULK_HELMET);
                        pOutput.accept(ModItems.INFESTED_SCULK_CHESTPLATE);
                        pOutput.accept(ModItems.INFESTED_SCULK_LEGGINGS);
                        pOutput.accept(ModItems.INFESTED_SCULK_BOOTS);
                        pOutput.accept(ModItems.INFESTED_SCULK_HORSE_ARMOR);


                    }).build());

    public static final Supplier<CreativeModeTab> SCULKY_BLOCKS_TAB =
            CREATIVE_MODE_TABS.register("sculky_blocks_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.sculky.sculky_blocks_tab"))
                    .icon(() -> new ItemStack(ModItems.RAW_SCULK_SHARD.get()))
                    .icon(() -> new ItemStack(ModBlocks.INFESTED_SCULK_BLOCK))
                    .withTabsBefore(ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "sculky_items_tab"))
                    .displayItems((pParameters, pOutput) -> {

                        pOutput.accept(ModBlocks.INFESTED_SCULK_BLOCK);
                        pOutput.accept(ModBlocks.RAW_INFESTED_SCULK_BLOCK);
                        pOutput.accept(ModBlocks.INFESTED_SCULK_BRICKS);
                        pOutput.accept(ModBlocks.SCULK_ORE);
                        pOutput.accept(ModBlocks.DEEPSLATE_SCULK_ORE);

                        pOutput.accept(ModBlocks.INFESTED_SCULK_STAIRS);
                        pOutput.accept(ModBlocks.INFESTED_SCULK_SLAB);

                        pOutput.accept(ModBlocks.INFESTED_SCULK_BRICK_STAIRS);
                        pOutput.accept(ModBlocks.INFESTED_SCULK_BRICK_SLAB);

                        pOutput.accept(ModBlocks.SCULKBLOOM);

                        pOutput.accept(ModBlocks.INFESTED_SCULK_PRESSURE_PLATE);
                        pOutput.accept(ModBlocks.INFESTED_SCULK_BUTTON);

                        pOutput.accept(ModBlocks.INFESTED_SCULK_BRICK_PRESSURE_PLATE);
                        pOutput.accept(ModBlocks.INFESTED_SCULK_BRICK_BUTTON);

                        pOutput.accept(ModBlocks.INFESTED_SCULK_FENCE);
                        pOutput.accept(ModBlocks.INFESTED_SCULK_FENCE_GATE);
                        pOutput.accept(ModBlocks.INFESTED_SCULK_WALL);

                        pOutput.accept(ModBlocks.INFESTED_SCULK_BRICK_FENCE);
                        pOutput.accept(ModBlocks.INFESTED_SCULK_BRICK_FENCE_GATE);
                        pOutput.accept(ModBlocks.INFESTED_SCULK_BRICK_WALL);

                        pOutput.accept(ModBlocks.INFESTED_SCULK_DOOR);
                        pOutput.accept(ModBlocks.INFESTED_SCULK_TRAPDOOR);

                        pOutput.accept(ModBlocks.INFESTED_SCULK_GRASS_BLOCK);
                        pOutput.accept(ModBlocks.INFESTED_SCULK_DIRT_BLOCK);
                        pOutput.accept(ModBlocks.INFESTED_SCULK_ROOTED_DIRT_BLOCK);
                        pOutput.accept(ModBlocks.INFESTED_SCULK_PODZOL_BLOCK);
                        pOutput.accept(ModBlocks.INFESTED_SCULK_SAND);

                        pOutput.accept(ModBlocks.INFESTED_SCULK_LOG);
                        pOutput.accept(ModBlocks.INFESTED_SCULK_WOOD);
                        pOutput.accept(ModBlocks.STRIPPED_INFESTED_SCULK_LOG);
                        pOutput.accept(ModBlocks.STRIPPED_INFESTED_SCULK_WOOD);
                        pOutput.accept(ModBlocks.INFESTED_SCULK_PLANKS);
                        pOutput.accept(ModBlocks.INFESTED_SCULK_SAPLING);

                    }).build());

    public static final Supplier<CreativeModeTab> SCULKY_ENTITIES_TAB =
            CREATIVE_MODE_TABS.register("sculky_entities_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.sculky.sculky_entities_tab"))
                    .icon(() -> new ItemStack(ModItems.SCULK_PARASITE_SPAWN_EGG.get()))
                    .withTabsBefore(ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "sculky_blocks_tab"))
                    .displayItems((pParameters, pOutput) -> {

                        pOutput.accept(ModItems.SCULK_PARASITE_SPAWN_EGG);
                        pOutput.accept(ModItems.SCULK_SENTINEL_SPAWN_EGG);
                        pOutput.accept(ModItems.SCULK_STALKER_SPAWN_EGG);
                        pOutput.accept(ModItems.SCULK_SHADE_SPAWN_EGG);
                        pOutput.accept(ModItems.SCULK_HORROR_SPAWN_EGG);
                        pOutput.accept(ModItems.SCULK_ZOMBIE_SPAWN_EGG);
                        pOutput.accept(ModItems.SCULK_SKELETON_SPAWN_EGG);
                        pOutput.accept(ModItems.SCULK_CREEPER_SPAWN_EGG);
                        pOutput.accept(ModItems.SCULK_SPIDER_SPAWN_EGG);
                        pOutput.accept(ModItems.SCULK_ENDERMAN_SPAWN_EGG);
                        pOutput.accept(ModItems.SCULKMITE_SPAWN_EGG);
                        pOutput.accept(ModItems.SCULK_SANDSNARE_SPAWN_EGG);

                        pOutput.accept(ModItems.SCULK_WOLF_SPAWN_EGG);
                        pOutput.accept(ModItems.SCULK_HORSE_SPAWN_EGG);
                        pOutput.accept(ModItems.SCULK_FOX_SPAWN_EGG);
                        pOutput.accept(ModItems.SCULK_RAT_SPAWN_EGG);
                        pOutput.accept(ModItems.SCULK_TAIL_SPAWN_EGG);
                        pOutput.accept(ModItems.SCULK_BEETLE_SPAWN_EGG);
                        pOutput.accept(ModItems.SCULK_BAT_SPAWN_EGG);
                        pOutput.accept(ModItems.SCULK_WOLF_ALPHA_SPAWN_EGG);

                    }).build());




    public static void register( IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
