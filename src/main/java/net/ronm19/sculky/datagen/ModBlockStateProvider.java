package net.ronm19.sculky.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.*;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.block.ModBlocks;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, SculkyMod.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(ModBlocks.INFESTED_SCULK_BLOCK);
        blockWithItem(ModBlocks.RAW_INFESTED_SCULK_BLOCK);

        blockWithItem(ModBlocks.SCULK_ORE);
        blockWithItem(ModBlocks.DEEPSLATE_SCULK_ORE);

        stairsBlock(((StairBlock) ModBlocks.INFESTED_SCULK_STAIRS.get()), blockTexture(ModBlocks.INFESTED_SCULK_BLOCK.get()));
        slabBlock(((SlabBlock) ModBlocks.INFESTED_SCULK_SLAB.get()), blockTexture(ModBlocks.INFESTED_SCULK_BLOCK.get()), blockTexture(ModBlocks.INFESTED_SCULK_BLOCK.get()));

        pressurePlateBlock(((PressurePlateBlock) ModBlocks.INFESTED_SCULK_PRESSURE_PLATE.get()), blockTexture(ModBlocks.INFESTED_SCULK_BLOCK.get()));
        buttonBlock(((ButtonBlock) ModBlocks.INFESTED_SCULK_BUTTON.get()), blockTexture(ModBlocks.INFESTED_SCULK_BLOCK.get()));

        fenceBlock(((FenceBlock) ModBlocks.INFESTED_SCULK_FENCE.get()), blockTexture(ModBlocks.INFESTED_SCULK_BLOCK.get()));
        fenceGateBlock(((FenceGateBlock) ModBlocks.INFESTED_SCULK_FENCE_GATE.get()), blockTexture(ModBlocks.INFESTED_SCULK_BLOCK.get()));
        wallBlock(((WallBlock) ModBlocks.INFESTED_SCULK_WALL.get()), blockTexture(ModBlocks.INFESTED_SCULK_BLOCK.get()));

        doorBlockWithRenderType(((DoorBlock) ModBlocks.INFESTED_SCULK_DOOR.get()), modLoc("block/infested_sculk_door_bottom"), modLoc("block/infested_sculk_door_top"), "cutout");
        trapdoorBlockWithRenderType(((TrapDoorBlock) ModBlocks.INFESTED_SCULK_TRAPDOOR.get()), modLoc("block/infested_sculk_trapdoor"), true, "cutout");

        blockItem(ModBlocks.INFESTED_SCULK_STAIRS);
        blockItem(ModBlocks.INFESTED_SCULK_SLAB);
        blockItem(ModBlocks.INFESTED_SCULK_PRESSURE_PLATE);
        blockItem(ModBlocks.INFESTED_SCULK_FENCE_GATE);

        blockItem(ModBlocks.INFESTED_SCULK_TRAPDOOR, "_bottom");
    }

    private void blockWithItem(DeferredBlock<Block> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }

    private void blockItem(DeferredBlock<Block> deferredBlock) {
        simpleBlockItem(deferredBlock.get(), new ModelFile.UncheckedModelFile("sculky:block/" + deferredBlock.getId().getPath()));
    }

    private void blockItem(DeferredBlock<Block> deferredBlock, String appendix) {
        simpleBlockItem(deferredBlock.get(), new ModelFile.UncheckedModelFile("sculky:block/" + deferredBlock.getId().getPath() + appendix));
    }
}