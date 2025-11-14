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
                .add(ModItems.INFESTED_SCULK_SWORD.asItem())
                .add(ModItems.SCULK_EDGE.asItem());

        this.tag(ItemTags.TRIMMABLE_ARMOR)
                .add(ModItems.INFESTED_SCULK_HELMET.get())
                .add(ModItems.INFESTED_SCULK_CHESTPLATE.get())
                .add(ModItems.INFESTED_SCULK_LEGGINGS.get())
                .add(ModItems.INFESTED_SCULK_BOOTS.get());

        tag(ItemTags.LOGS_THAT_BURN)
                .add(ModBlocks.INFESTED_SCULK_LOG.get().asItem())
                .add(ModBlocks.INFESTED_SCULK_WOOD.get().asItem())
                .add(ModBlocks.STRIPPED_INFESTED_SCULK_LOG.get().asItem())
                .add(ModBlocks.STRIPPED_INFESTED_SCULK_WOOD.get().asItem());

        tag(ItemTags.PLANKS)
                .add(ModBlocks.INFESTED_SCULK_PLANKS.get().asItem());
    }
}
