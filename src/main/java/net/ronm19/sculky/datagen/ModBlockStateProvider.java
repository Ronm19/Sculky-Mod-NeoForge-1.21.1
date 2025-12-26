package net.ronm19.sculky.datagen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.block.ModBlocks;
import net.ronm19.sculky.block.custom.TomatoSculkCropBlock;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, SculkyMod.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(ModBlocks.INFESTED_SCULK_BLOCK);
        blockWithItem(ModBlocks.RAW_INFESTED_SCULK_BLOCK);
        blockWithItem(ModBlocks.INFESTED_SCULK_BRICKS);

        blockWithItem(ModBlocks.SCULK_ORE);
        blockWithItem(ModBlocks.DEEPSLATE_SCULK_ORE);

        stairsBlock(((StairBlock) ModBlocks.INFESTED_SCULK_STAIRS.get()), blockTexture(ModBlocks.INFESTED_SCULK_BLOCK.get()));
        slabBlock(((SlabBlock) ModBlocks.INFESTED_SCULK_SLAB.get()), blockTexture(ModBlocks.INFESTED_SCULK_BLOCK.get()), blockTexture(ModBlocks.INFESTED_SCULK_BLOCK.get()));

        pressurePlateBlock(((PressurePlateBlock) ModBlocks.INFESTED_SCULK_PRESSURE_PLATE.get()), blockTexture(ModBlocks.INFESTED_SCULK_BLOCK.get()));
        buttonBlock(((ButtonBlock) ModBlocks.INFESTED_SCULK_BUTTON.get()), blockTexture(ModBlocks.INFESTED_SCULK_BLOCK.get()));

        fenceBlock(((FenceBlock) ModBlocks.INFESTED_SCULK_FENCE.get()), blockTexture(ModBlocks.INFESTED_SCULK_BLOCK.get()));
        fenceGateBlock(((FenceGateBlock) ModBlocks.INFESTED_SCULK_FENCE_GATE.get()), blockTexture(ModBlocks.INFESTED_SCULK_BLOCK.get()));
        wallBlock(((WallBlock) ModBlocks.INFESTED_SCULK_WALL.get()), blockTexture(ModBlocks.INFESTED_SCULK_BLOCK.get()));


        stairsBlock(((StairBlock) ModBlocks.INFESTED_SCULK_BRICK_STAIRS.get()), blockTexture(ModBlocks.INFESTED_SCULK_BRICKS.get()));
        slabBlock(((SlabBlock) ModBlocks.INFESTED_SCULK_BRICK_SLAB.get()), blockTexture(ModBlocks.INFESTED_SCULK_BRICKS.get()), blockTexture(ModBlocks.INFESTED_SCULK_BRICKS.get()));

        pressurePlateBlock(((PressurePlateBlock) ModBlocks.INFESTED_SCULK_BRICK_PRESSURE_PLATE.get()), blockTexture(ModBlocks.INFESTED_SCULK_BRICKS.get()));
        buttonBlock(((ButtonBlock) ModBlocks.INFESTED_SCULK_BRICK_BUTTON.get()), blockTexture(ModBlocks.INFESTED_SCULK_BRICKS.get()));

        fenceBlock(((FenceBlock) ModBlocks.INFESTED_SCULK_BRICK_FENCE.get()), blockTexture(ModBlocks.INFESTED_SCULK_BRICKS.get()));
        fenceGateBlock(((FenceGateBlock) ModBlocks.INFESTED_SCULK_BRICK_FENCE_GATE.get()), blockTexture(ModBlocks.INFESTED_SCULK_BRICKS.get()));
        wallBlock(((WallBlock) ModBlocks.INFESTED_SCULK_BRICK_WALL.get()), blockTexture(ModBlocks.INFESTED_SCULK_BRICKS.get()));

        doorBlockWithRenderType(((DoorBlock) ModBlocks.INFESTED_SCULK_DOOR.get()), modLoc("block/infested_sculk_door_bottom"), modLoc("block/infested_sculk_door_top"), "cutout");
        trapdoorBlockWithRenderType(((TrapDoorBlock) ModBlocks.INFESTED_SCULK_TRAPDOOR.get()), modLoc("block/infested_sculk_trapdoor"), true, "cutout");

        blockItem(ModBlocks.INFESTED_SCULK_STAIRS);
        blockItem(ModBlocks.INFESTED_SCULK_SLAB);
        blockItem(ModBlocks.INFESTED_SCULK_PRESSURE_PLATE);
        blockItem(ModBlocks.INFESTED_SCULK_FENCE_GATE);

        blockItem(ModBlocks.INFESTED_SCULK_BRICK_STAIRS);
        blockItem(ModBlocks.INFESTED_SCULK_BRICK_SLAB);
        blockItem(ModBlocks.INFESTED_SCULK_BRICK_PRESSURE_PLATE);
        blockItem(ModBlocks.INFESTED_SCULK_BRICK_FENCE_GATE);

        blockItem(ModBlocks.INFESTED_SCULK_TRAPDOOR, "_bottom");

        makeCrop(((TomatoSculkCropBlock) ModBlocks.TOMATO_SCULK_CROP.get()), "tomato_sculk_crop_stage","tomato_sculk_crop_stage");

        simpleBlock(ModBlocks.SCULKBLOOM.get(),
                models().cross(blockTexture(ModBlocks.SCULKBLOOM.get()).getPath(), blockTexture(ModBlocks.SCULKBLOOM.get())).renderType("cutout"));

        simpleBlock(ModBlocks.POTTED_SCULKBLOOM.get(), models().singleTexture("potted_sculkbloom", ResourceLocation.parse("flower_pot_cross"), "plant",
                blockTexture(ModBlocks.SCULKBLOOM.get())).renderType("cutout"));

        logBlock(((RotatedPillarBlock) ModBlocks.INFESTED_SCULK_LOG.get()));
        axisBlock(((RotatedPillarBlock) ModBlocks.INFESTED_SCULK_WOOD.get()), blockTexture(ModBlocks.INFESTED_SCULK_LOG.get()), blockTexture(ModBlocks.INFESTED_SCULK_LOG.get()));
        logBlock(((RotatedPillarBlock) ModBlocks.STRIPPED_INFESTED_SCULK_LOG.get()));
        axisBlock(((RotatedPillarBlock) ModBlocks.STRIPPED_INFESTED_SCULK_WOOD.get()), blockTexture(ModBlocks.STRIPPED_INFESTED_SCULK_LOG.get()), blockTexture(ModBlocks.STRIPPED_INFESTED_SCULK_LOG.get()));

        blockItem(ModBlocks.INFESTED_SCULK_LOG);
        blockItem(ModBlocks.INFESTED_SCULK_WOOD);
        blockItem(ModBlocks.STRIPPED_INFESTED_SCULK_LOG);
        blockItem(ModBlocks.STRIPPED_INFESTED_SCULK_WOOD);

        blockWithItem(ModBlocks.INFESTED_SCULK_PLANKS);

        leavesBlock(ModBlocks.INFESTED_SCULK_LEAVES);
        saplingBlock(ModBlocks.INFESTED_SCULK_SAPLING);

        blockWithItem(ModBlocks.INFESTED_SCULK_DIRT_BLOCK);
        blockWithItem(ModBlocks.INFESTED_SCULK_ROOTED_DIRT_BLOCK);

        simpleGrassLikeBlock(ModBlocks.INFESTED_SCULK_GRASS_BLOCK.get(),
        modLoc("block/infested_sculk_grass_block_top"),
                modLoc("block/infested_sculk_grass_block_side"),
                modLoc("block/infested_sculk_grass_block_bottom"));

        simpleGrassLikeBlock(ModBlocks.INFESTED_SCULK_PODZOL_BLOCK.get(),
                modLoc("block/infested_sculk_podzol_block_top"),
                modLoc("block/infested_sculk_podzol_block_side"),
                modLoc("block/infested_sculk_podzol_block_bottom"));
    }


    private void leavesBlock(DeferredBlock<Block> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(),
                models().singleTexture(BuiltInRegistries.BLOCK.getKey(deferredBlock.get()).getPath(), ResourceLocation.parse("minecraft:block/leaves"),
                        "all", blockTexture(deferredBlock.get())).renderType("cutout"));
    }

    private void simpleGrassLikeBlock(Block block, ResourceLocation top, ResourceLocation side, ResourceLocation bottom) {
        // Generate the cube model using top, side, and bottom textures
        var model = models().cubeBottomTop(blockName(block), side, bottom, top);

        // Register the blockstate to use that model
        simpleBlock(block, model);

        // Also create an item model that points to the same block model
        simpleBlockItem(block, model);
    }



    private void saplingBlock(DeferredBlock<Block> deferredBlock) {
        simpleBlock(deferredBlock.get(), models().cross(BuiltInRegistries.BLOCK.getKey(deferredBlock.get()).getPath(), blockTexture(deferredBlock.get())).renderType("cutout"));
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

    public void makeCrop(CropBlock block, String modelName, String textureName) {
        Function<BlockState, ConfiguredModel[]> function = state -> states(state, block, modelName, textureName);

        getVariantBuilder(block).forAllStates(function);
    }

    private ConfiguredModel[] states(BlockState state, CropBlock block, String modelName, String textureName) {
        ConfiguredModel[] models = new ConfiguredModel[1];
        models[0] = new ConfiguredModel(models().crop(modelName + state.getValue(((TomatoSculkCropBlock) block).getAgeProperty()),
                ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "block/" + textureName +
                        state.getValue(((TomatoSculkCropBlock) block).getAgeProperty()))).renderType("cutout"));

        return models;
    }

    private String blockName(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).getPath();
    }

    public @NotNull ResourceLocation blockTexture( Block block ) {
        return modLoc("block/" + blockName(block));
    }
}