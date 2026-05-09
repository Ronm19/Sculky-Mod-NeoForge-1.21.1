package net.ronm19.sculky.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.block.ModBlocks;
import net.ronm19.sculky.util.ModTags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, SculkyMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags( HolderLookup.@NotNull Provider provider ) {
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.INFESTED_SCULK_BLOCK.get())
                .add(ModBlocks.RAW_INFESTED_SCULK_BLOCK.get())
                .add(ModBlocks.SCULK_ORE.get())
                .add(ModBlocks.INFESTED_SCULK_BRICKS.get())
                .add(ModBlocks.INFESTED_SCULK_WALL.get())
                .add(ModBlocks.CHISELED_ANCIENT_SCULK_BRICKS.get())
                .add(ModBlocks.ANCIENT_SCULK_BRICKS.get())
                .add(ModBlocks.CRACKED_ANCIENT_SCULK_BRICKS.get())
                .add(ModBlocks.KINGS_PEDESTAL.get())
                .add(ModBlocks.THRONE_BLOCK.get())
                .add(ModBlocks.ROYAL_SCULK_BLOCK.get())
                .add(ModBlocks.ROYAL_SCULK_LANTERN.get())
                .add(ModBlocks.DEEPSLATE_SCULK_ORE.get());

        this.tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(ModBlocks.INFESTED_SCULK_BLOCK.get())
                .add(ModBlocks.RAW_INFESTED_SCULK_BLOCK.get())
                .add(ModBlocks.INFESTED_SCULK_WALL.get())
                .add(ModBlocks.INFESTED_SCULK_BRICKS.get())
                .add(ModBlocks.SCULK_ORE.get())
                .add(ModBlocks.CHISELED_ANCIENT_SCULK_BRICKS.get())
                .add(ModBlocks.ANCIENT_SCULK_BRICKS.get())
                .add(ModBlocks.ROYAL_SCULK_BLOCK.get())
                .add(ModBlocks.ROYAL_SCULK_LANTERN.get())
                .add(ModBlocks.CRACKED_ANCIENT_SCULK_BRICKS.get())
                .add(ModBlocks.KINGS_PEDESTAL.get())
                .add(ModBlocks.THRONE_BLOCK.get())
                .add(ModBlocks.DEEPSLATE_SCULK_ORE.get());

        this.tag(BlockTags.SCULK_REPLACEABLE)
                .add(ModBlocks.SCULK_ORE.get())
                .add(ModBlocks.DEEPSLATE_SCULK_ORE.get());

        this.tag(BlockTags.FLOWERS)
                        .add(ModBlocks.SCULKBLOOM.get())
                        .add(ModBlocks.POTTED_SCULKBLOOM.get())
                        .add(ModBlocks.ECHOBLOOM.get())
                        .add(ModBlocks.POTTED_ECHOBLOOM.get());

        this.tag(BlockTags.LOGS_THAT_BURN)
                .add(ModBlocks.INFESTED_SCULK_LOG.get())
                .add(ModBlocks.INFESTED_SCULK_WOOD.get())
                .add(ModBlocks.STRIPPED_INFESTED_SCULK_LOG.get())
                .add(ModBlocks.STRIPPED_INFESTED_SCULK_WOOD.get())
                .add(ModBlocks.SCULK_JUNGLE_LOG.get())
                .add(ModBlocks.SCULK_JUNGLE_WOOD.get())
                .add(ModBlocks.STRIPPED_SCULK_JUNGLE_LOG.get())
                .add(ModBlocks.STRIPPED_SCULK_JUNGLE_WOOD.get());

        this.tag(BlockTags.DIRT)
                        .add(ModBlocks.INFESTED_SCULK_GRASS_BLOCK.get())
                        .add(ModBlocks.INFESTED_SCULK_DIRT_BLOCK.get())
                        .add(ModBlocks.SCULK_SANCTUM_GRASS_BLOCK.get())
                        .add(ModBlocks.SCULK_SANCTUM_DIRT_BLOCK.get())
                        .add(ModBlocks.INFESTED_SCULK_ROOTED_DIRT_BLOCK.get())
                        .add(ModBlocks.INFESTED_SCULK_PODZOL_BLOCK.get());

        tag(BlockTags.FENCES)
                .add(ModBlocks.INFESTED_SCULK_BRICK_FENCE.get())
                .add(ModBlocks.SCULK_JUNGLE_FENCE.get())
                .add(ModBlocks.INFESTED_SCULK_FENCE.get());

        tag(BlockTags.FENCE_GATES)
                .add(ModBlocks.INFESTED_SCULK_BRICK_FENCE_GATE.get())
                .add(ModBlocks.SCULK_JUNGLE_FENCE_GATE.get())
                .add(ModBlocks.INFESTED_SCULK_FENCE_GATE.get());

        tag(BlockTags.WALLS)
                .add(ModBlocks.INFESTED_SCULK_BRICK_WALL.get())
                .add(ModBlocks.SCULK_JUNGLE_WALL.get())
                .add(ModBlocks.INFESTED_SCULK_WALL.get());

        tag(BlockTags.SAND)
                .add(ModBlocks.INFESTED_SCULK_SAND.get());

        this.tag(ModTags.Blocks.SCULK_SPREADABLE)
                // Dirt-like blocks
                .addTag(BlockTags.DIRT)
                .addTag(BlockTags.CONVERTABLE_TO_MUD)
                // Stone-like blocks
                .addTag(BlockTags.BASE_STONE_OVERWORLD)
                .addTag(BlockTags.BASE_STONE_NETHER)
                // Sand & terracotta
                .addTag(BlockTags.SAND)
                .addTag(BlockTags.TERRACOTTA)
                // Optional: moss-replaceables (nice synergy)
                .addTag(BlockTags.MOSS_REPLACEABLE);
    }



}
