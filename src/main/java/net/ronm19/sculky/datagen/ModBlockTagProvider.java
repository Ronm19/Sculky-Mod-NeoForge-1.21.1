package net.ronm19.sculky.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.block.ModBlocks;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, SculkyMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags( HolderLookup.Provider provider ) {
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.INFESTED_SCULK_BLOCK.get())
                .add(ModBlocks.RAW_INFESTED_SCULK_BLOCK.get())
                .add(ModBlocks.SCULK_ORE.get())
                .add(ModBlocks.INFESTED_SCULK_WALL.get())
                .add(ModBlocks.DEEPSLATE_SCULK_ORE.get());

        this.tag(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.INFESTED_SCULK_BLOCK.get())
                .add(ModBlocks.RAW_INFESTED_SCULK_BLOCK.get())
                .add(ModBlocks.INFESTED_SCULK_WALL.get())
                .add(ModBlocks.SCULK_ORE.get())
                .add(ModBlocks.DEEPSLATE_SCULK_ORE.get());

        this.tag(BlockTags.SCULK_REPLACEABLE)
                .add(ModBlocks.SCULK_ORE.get())
                .add(ModBlocks.DEEPSLATE_SCULK_ORE.get());


        tag(BlockTags.FENCES).add(ModBlocks.INFESTED_SCULK_FENCE.get());
        tag(BlockTags.FENCE_GATES).add(ModBlocks.INFESTED_SCULK_FENCE_GATE.get());
        tag(BlockTags.WALLS).add(ModBlocks.INFESTED_SCULK_WALL.get());
    }


}
