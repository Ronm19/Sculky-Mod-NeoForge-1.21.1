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
                        pOutput.accept(ModItems.SCULK_HEARTFRUIT);
                        pOutput.accept(ModItems.TOMATO_SCULK);
                        pOutput.accept(ModItems.ECHO_JELLY);
                        pOutput.accept(ModItems.SCULK_PASTRY);
                        pOutput.accept(ModItems.SOULBITE_COOKIE);

                        pOutput.accept(ModItems.INFESTED_SCULK_SWORD);
                        pOutput.accept(ModItems.INFESTED_SCULK_PICKAXE);
                        pOutput.accept(ModItems.INFESTED_SCULK_AXE);
                        pOutput.accept(ModItems.INFESTED_SCULK_SHOVEL);
                        pOutput.accept(ModItems.INFESTED_SCULK_HOE);
                        pOutput.accept(ModItems.INFESTED_SCULK_HAMMER);


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
                        pOutput.accept(ModBlocks.SCULK_ORE);
                        pOutput.accept(ModBlocks.DEEPSLATE_SCULK_ORE);

                        pOutput.accept(ModBlocks.INFESTED_SCULK_STAIRS);
                        pOutput.accept(ModBlocks.INFESTED_SCULK_SLAB);

                        pOutput.accept(ModBlocks.INFESTED_SCULK_PRESSURE_PLATE);
                        pOutput.accept(ModBlocks.INFESTED_SCULK_BUTTON);

                        pOutput.accept(ModBlocks.INFESTED_SCULK_FENCE);
                        pOutput.accept(ModBlocks.INFESTED_SCULK_FENCE_GATE);
                        pOutput.accept(ModBlocks.INFESTED_SCULK_WALL);

                        pOutput.accept(ModBlocks.INFESTED_SCULK_DOOR);
                        pOutput.accept(ModBlocks.INFESTED_SCULK_TRAPDOOR);

                    }).build());



    public static void register( IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
