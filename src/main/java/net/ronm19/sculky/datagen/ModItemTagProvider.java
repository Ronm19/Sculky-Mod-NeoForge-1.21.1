package net.ronm19.sculky.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.block.ModBlocks;
import net.ronm19.sculky.item.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider( PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider,
                               CompletableFuture<TagLookup<Block>> pBlockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pLookupProvider, pBlockTags, SculkyMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags( HolderLookup.Provider provider ) {

        this.tag(ItemTags.SWORDS)
                .add(ModItems.INFESTED_SCULK_SWORD.get())
                .add(ModItems.ECHO_DAGGER.get())
                .add(ModItems.INFESTED_SCULK_HAMMER.get())
                .add(ModItems.KINGS_AXE.get())
                .add(ModItems.SCULK_CLEAVER.get())
                .add(ModItems.SCULK_EDGE.get());

        this.tag(ItemTags.AXES)
                .add(ModItems.KINGS_AXE.get())
                .add(ModItems.SCULK_CLEAVER.get())
                .add(ModItems.INFESTED_SCULK_AXE.get());

        this.tag(ItemTags.PICKAXES)
                .add(ModItems.INFESTED_SCULK_HAMMER.get())
                .add(ModItems.INFESTED_SCULK_PICKAXE.get());

        this.tag(ItemTags.SHOVELS)
                .add(ModItems.INFESTED_SCULK_SHOVEL.get());

        this.tag(ItemTags.HOES)
                .add(ModItems.INFESTED_SCULK_HOE.get());

        this.tag(ItemTags.TRIMMABLE_ARMOR)
                .add(ModItems.INFESTED_SCULK_HELMET.get())
                .add(ModItems.INFESTED_SCULK_CHESTPLATE.get())
                .add(ModItems.INFESTED_SCULK_LEGGINGS.get())
                .add(ModItems.INFESTED_SCULK_BOOTS.get());

        tag(ItemTags.LOGS_THAT_BURN)
                .add(ModBlocks.INFESTED_SCULK_LOG.get().asItem())
                .add(ModBlocks.INFESTED_SCULK_WOOD.get().asItem())
                .add(ModBlocks.STRIPPED_INFESTED_SCULK_LOG.get().asItem())
                .add(ModBlocks.STRIPPED_INFESTED_SCULK_WOOD.get().asItem())
                .add(ModBlocks.SCULK_JUNGLE_LOG.get().asItem())
                .add(ModBlocks.SCULK_JUNGLE_WOOD.get().asItem())
                .add(ModBlocks.STRIPPED_SCULK_JUNGLE_LOG.get().asItem())
                .add(ModBlocks.STRIPPED_SCULK_JUNGLE_WOOD.get().asItem());

        tag(ItemTags.PLANKS)
                .add(ModBlocks.INFESTED_SCULK_PLANKS.get().asItem())
                .add(ModBlocks.SCULK_JUNGLE_PLANKS.get().asItem());

        tag(ItemTags.DIRT)
                .add(ModBlocks.INFESTED_SCULK_GRASS_BLOCK.asItem())
                .add(ModBlocks.INFESTED_SCULK_DIRT_BLOCK.asItem())
                .add(ModBlocks.SCULK_SANCTUM_GRASS_BLOCK.asItem())
                .add(ModBlocks.SCULK_SANCTUM_DIRT_BLOCK.asItem())
                .add(ModBlocks.INFESTED_SCULK_ROOTED_DIRT_BLOCK.asItem())
                .add(ModBlocks.INFESTED_SCULK_PODZOL_BLOCK.asItem());

        tag(ItemTags.SAND)
        .add(ModBlocks.INFESTED_SCULK_SAND.get().asItem());

        tag(ItemTags.FLOWERS)
                .add(ModBlocks.ECHOBLOOM.get().asItem())
                .add(ModBlocks.SCULKBLOOM.get().asItem());

        tag(ItemTags.FOX_FOOD)
                .add(ModItems.ECHO_JELLY.asItem());
    }
}
