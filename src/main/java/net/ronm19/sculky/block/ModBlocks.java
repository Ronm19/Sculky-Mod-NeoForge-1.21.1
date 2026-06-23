package net.ronm19.sculky.block;


import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.ColorRGBA;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.block.custom.*;
import net.ronm19.sculky.block.custom.SculkBlock;
import net.ronm19.sculky.item.ModItems;
import net.ronm19.sculky.worldgen.tree.ModTreeGrowers;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(SculkyMod.MOD_ID);

    public static final DeferredBlock<Block> INFESTED_SCULK_BLOCK = registerBlock("infested_sculk_block",
            () -> new SculkBlock(BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops().sound(SoundType.SCULK)));
    public static final DeferredBlock<Block> RAW_INFESTED_SCULK_BLOCK = registerBlock("raw_infested_sculk_block",
            () -> new SculkBlock(BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops().sound(SoundType.SCULK)));

    public static final DeferredBlock<Block> INFESTED_SCULK_BRICKS = registerBlock("infested_sculk_bricks",
            () -> new InfestedSculkBricksBlock(BlockBehaviour.Properties.of().strength(5.5f, 8.0f).requiresCorrectToolForDrops().sound(SoundType.SCULK)));


    public static final DeferredBlock<Block> INFESTED_SCULK_SAND = registerBlock("infested_sculk_sand",
            () -> new Block(BlockBehaviour.Properties.of().strength(0.5F).sound(SoundType.SCULK)));
    public static final DeferredBlock<Block> SCULK_ORE = registerBlock("sculk_ore",
            () -> new DropExperienceBlock(UniformInt.of(1, 5), BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops().sound(SoundType.SCULK)));
    public static final DeferredBlock<Block> DEEPSLATE_SCULK_ORE = registerBlock("deepslate_sculk_ore",
            () -> new DropExperienceBlock(UniformInt.of(2, 7),BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops().sound(SoundType.SCULK)));

    public static final DeferredBlock<Block> THRONE_BLOCK = registerBlock("throne_block",
            () -> new ThroneBlock(BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops().sound(SoundType.SCULK_SHRIEKER)));

    public static final DeferredBlock<Block> KINGS_PEDESTAL = registerBlock("kings_pedestal",
            () -> new KingsPedestalBlock(BlockBehaviour.Properties.of().strength(3.5f, 6.0F).requiresCorrectToolForDrops()
                    .sound(SoundType.SCULK_SHRIEKER).lightLevel(state -> state.getValue(KingsPedestalBlock.ACTIVATED) ? 10 : 0)));

    public static final DeferredBlock<Block> ANCIENT_SCULK_BRICKS = registerBlock("ancient_sculk_bricks",
            () -> new InfestedSculkBricksBlock(BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops().sound(SoundType.SCULK)));

    public static final DeferredBlock<Block> CRACKED_ANCIENT_SCULK_BRICKS = registerBlock("cracked_ancient_sculk_bricks",
            () -> new InfestedSculkBricksBlock(BlockBehaviour.Properties.of().strength(2f).requiresCorrectToolForDrops().sound(SoundType.SCULK)));

    public static final DeferredBlock<Block> CHISELED_ANCIENT_SCULK_BRICKS = registerBlock("chiseled_ancient_sculk_bricks",
            () -> new InfestedSculkBricksBlock(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops().sound(SoundType.SCULK)));

    public static final DeferredBlock<Block> CHISELED_ROYAL_SCULK = registerBlock("chiseled_royal_sculk",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SCULK).strength(3.0F, 6.0F)
                    .requiresCorrectToolForDrops().sound(SoundType.SCULK).lightLevel(state -> 4)));

    public static final DeferredBlock<Block> ROYAL_SCULK_BLOCK = registerBlock("royal_sculk_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.SCULK)));

    public static final DeferredBlock<Block> ROYAL_SCULK_TOTEM = registerBlock("royal_sculk_totem",
            () -> new RoyalSculkTotemBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SCULK)
                    .strength(4.0F, 8.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.SCULK)
                    .lightLevel(state -> 7)
            ));

    public static final DeferredBlock<Block> ROYAL_SCULK_LANTERN = registerBlock("royal_sculk_lantern",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(2.5F, 5.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.SCULK)
                    .lightLevel(state -> 12)));


    // ------------------------------------- INFESTED SCULK NON-BLOCKS ------------------------------------ //


    public static final DeferredBlock<Block> INFESTED_SCULK_STAIRS = registerBlock("infested_sculk_stairs",
            () -> new StairBlock(ModBlocks.INFESTED_SCULK_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.of().strength(4f).sound(SoundType.SCULK)));

    public static final DeferredBlock<Block> INFESTED_SCULK_SLAB = registerBlock("infested_sculk_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.of().strength(4f).sound(SoundType.SCULK)));

    public static final DeferredBlock<Block> INFESTED_SCULK_PRESSURE_PLATE = registerBlock("infested_sculk_pressure_plate",
            () -> new PressurePlateBlock(BlockSetType.CRIMSON, BlockBehaviour.Properties.of().strength(4f).sound(SoundType.SCULK)));

    public static final DeferredBlock<Block> INFESTED_SCULK_BUTTON = registerBlock("infested_sculk_button",
            () -> new ButtonBlock(BlockSetType.CRIMSON, 10, BlockBehaviour.Properties.of().strength(4f).sound(SoundType.SCULK).noCollission()));

    public static final DeferredBlock<Block> INFESTED_SCULK_FENCE = registerBlock("infested_sculk_fence",
            () -> new FenceBlock(BlockBehaviour.Properties.of().strength(4f).sound(SoundType.SCULK)));

    public static final DeferredBlock<Block> INFESTED_SCULK_FENCE_GATE = registerBlock("infested_sculk_fence_gate",
            () -> new FenceGateBlock(WoodType.CRIMSON, BlockBehaviour.Properties.of().strength(4f).sound(SoundType.SCULK)));

    public static final DeferredBlock<Block> INFESTED_SCULK_WALL = registerBlock("infested_sculk_wall",
            () -> new WallBlock(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops().sound(SoundType.SCULK)));

    public static final DeferredBlock<Block> INFESTED_SCULK_DOOR = registerBlock("infested_sculk_door",
            () -> new DoorBlock(BlockSetType.CRIMSON, BlockBehaviour.Properties.of().strength(4f).sound(SoundType.SCULK).noOcclusion()));

    public static final DeferredBlock<Block> INFESTED_SCULK_TRAPDOOR = registerBlock("infested_sculk_trapdoor",
            () -> new TrapDoorBlock(BlockSetType.CRIMSON, BlockBehaviour.Properties.of().strength(4f).sound(SoundType.SCULK).noOcclusion()));

    // ------------------------------------- INFESTED SCULK BRICK NON-BLOCKS ------------------------------------ //


    public static final DeferredBlock<Block> INFESTED_SCULK_BRICK_STAIRS = registerBlock("infested_sculk_brick_stairs",
            () -> new StairBlock(ModBlocks.INFESTED_SCULK_BLOCK.get().defaultBlockState(),
                    BlockBehaviour.Properties.of().strength(4f).sound(SoundType.SCULK)));

    public static final DeferredBlock<Block> INFESTED_SCULK_BRICK_SLAB = registerBlock("infested_sculk_brick_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.of().strength(4f).sound(SoundType.SCULK)));

    public static final DeferredBlock<Block> INFESTED_SCULK_BRICK_PRESSURE_PLATE = registerBlock("infested_sculk_brick_pressure_plate",
            () -> new PressurePlateBlock(BlockSetType.IRON, BlockBehaviour.Properties.of().strength(4f).sound(SoundType.SCULK)));

    public static final DeferredBlock<Block> INFESTED_SCULK_BRICK_BUTTON = registerBlock("infested_sculk_brick_button",
            () -> new ButtonBlock(BlockSetType.IRON, 10, BlockBehaviour.Properties.of().strength(4f).sound(SoundType.SCULK).noCollission()));

    public static final DeferredBlock<Block> INFESTED_SCULK_BRICK_FENCE = registerBlock("infested_sculk_brick_fence",
            () -> new FenceBlock(BlockBehaviour.Properties.of().strength(4f).sound(SoundType.SCULK)));

    public static final DeferredBlock<Block> INFESTED_SCULK_BRICK_FENCE_GATE = registerBlock("infested_sculk_brick_fence_gate",
            () -> new FenceGateBlock(WoodType.CRIMSON, BlockBehaviour.Properties.of().strength(4f).sound(SoundType.SCULK)));

    public static final DeferredBlock<Block> INFESTED_SCULK_BRICK_WALL = registerBlock("infested_sculk_brick_wall",
            () -> new WallBlock(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops().sound(SoundType.SCULK)));


    // ------------------------------------- JUNGLE NON-BLOCKS ------------------------------------ //


    public static final DeferredBlock<Block> SCULK_JUNGLE_STAIRS = registerBlock("sculk_jungle_stairs",
            () -> new StairBlock(ModBlocks.INFESTED_SCULK_BRICKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.of().strength(4f).sound(SoundType.SCULK)));

    public static final DeferredBlock<Block> SCULK_JUNGLE_SLAB = registerBlock("sculk_jungle_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.of().strength(4f).sound(SoundType.SCULK)));

    public static final DeferredBlock<Block> SCULK_JUNGLE_PRESSURE_PLATE = registerBlock("sculk_jungle_pressure_plate",
            () -> new PressurePlateBlock(BlockSetType.JUNGLE, BlockBehaviour.Properties.of().strength(4f).sound(SoundType.SCULK)));

    public static final DeferredBlock<Block> SCULK_JUNGLE_BUTTON = registerBlock("sculk_jungle_button",
            () -> new ButtonBlock(BlockSetType.JUNGLE, 10, BlockBehaviour.Properties.of().strength(4f).sound(SoundType.SCULK).noCollission()));

    public static final DeferredBlock<Block> SCULK_JUNGLE_FENCE = registerBlock("sculk_jungle_fence",
            () -> new FenceBlock(BlockBehaviour.Properties.of().strength(4f).sound(SoundType.SCULK)));

    public static final DeferredBlock<Block> SCULK_JUNGLE_FENCE_GATE = registerBlock("sculk_jungle_fence_gate",
            () -> new FenceGateBlock(WoodType.JUNGLE, BlockBehaviour.Properties.of().strength(4f).sound(SoundType.SCULK)));

    public static final DeferredBlock<Block> SCULK_JUNGLE_WALL = registerBlock("sculk_jungle_wall",
            () -> new WallBlock(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops().sound(SoundType.SCULK)));

    public static final DeferredBlock<Block> SCULK_JUNGLE_DOOR = registerBlock("sculk_jungle_door",
            () -> new DoorBlock(BlockSetType.JUNGLE, BlockBehaviour.Properties.of().strength(4f).sound(SoundType.SCULK).noOcclusion()));

    public static final DeferredBlock<Block> SCULK_JUNGLE_TRAPDOOR = registerBlock("sculk_jungle_trapdoor",
            () -> new TrapDoorBlock(BlockSetType.JUNGLE, BlockBehaviour.Properties.of().strength(4f).sound(SoundType.SCULK).noOcclusion()));


    // ------------------------------------- ETC ------------------------------------ //

    public static final DeferredBlock<Block> SCULK_PORTAL = registerBlock("sculk_portal",
            () -> new SculkPortalBlock(BlockBehaviour.Properties.of().strength(0.1F, 3600000.0F).noCollission()
                    .noLootTable().lightLevel(state -> 11).noOcclusion().noCollission().instabreak().sound(SoundType.SCULK_SHRIEKER)));

    public static final DeferredBlock<Block> TOMATO_SCULK_CROP = BLOCKS.register("tomato_sculk_crop",
            () -> new TomatoSculkCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT)));

    public static final DeferredBlock<Block> INFESTED_SCULK_GRASS_BLOCK = registerBlock("infested_sculk_grass_block",
            () -> new InfestedSculkGrassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK).sound(SoundType.SCULK)));

    public static final DeferredBlock<Block> INFESTED_SCULK_DIRT_BLOCK = registerBlock("infested_sculk_dirt_block",
            () -> new SculkBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DIRT).sound(SoundType.SCULK)));

    public static final DeferredBlock<Block> INFESTED_SCULK_ROOTED_DIRT_BLOCK = registerBlock("infested_sculk_rooted_dirt_block",
            () -> new SculkBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ROOTED_DIRT).sound(SoundType.SCULK)));

    public static final DeferredBlock<Block> INFESTED_SCULK_PODZOL_BLOCK = registerBlock("infested_sculk_podzol_block",
            () -> new SculkBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PODZOL).sound(SoundType.SCULK)));


    public static final DeferredBlock<Block> SCULK_SANCTUM_GRASS_BLOCK = registerBlock("sculk_sanctum_grass_block",
            () -> new SculkSanctumGrassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK).sound(SoundType.SCULK)));

    public static final DeferredBlock<Block> SCULK_SANCTUM_DIRT_BLOCK = registerBlock("sculk_sanctum_dirt_block",
            () -> new SculkBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DIRT).sound(SoundType.SCULK)));


    public static final DeferredBlock<Block> SCULKBLOOM = registerBlock("sculkbloom",
            () -> new FlowerBlock(MobEffects.DARKNESS, 12, BlockBehaviour.Properties.ofFullCopy(Blocks.WITHER_ROSE)));

    public static final DeferredBlock<Block> POTTED_SCULKBLOOM = BLOCKS.register("potted_sculkbloom",
            () -> new FlowerPotBlock(() -> ((FlowerPotBlock) Blocks.FLOWER_POT), SCULKBLOOM, BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_WITHER_ROSE)));


    public static final DeferredBlock<Block> ECHOBLOOM = registerBlock("echobloom",
            () -> new FlowerBlock(MobEffects.DAMAGE_RESISTANCE, 15, BlockBehaviour.Properties.ofFullCopy(Blocks.WITHER_ROSE)));

    public static final DeferredBlock<Block> POTTED_ECHOBLOOM = BLOCKS.register("potted_echobloom",
            () -> new FlowerPotBlock(() -> ((FlowerPotBlock) Blocks.FLOWER_POT), ECHOBLOOM, BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_WITHER_ROSE)));

    // ---------------------------------------- SCULK WOOD --------------------------------------------- //

    public static final DeferredBlock<Block> INFESTED_SCULK_LOG = registerBlock("infested_sculk_log",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG).sound(SoundType.SCULK)));
    public static final DeferredBlock<Block> INFESTED_SCULK_WOOD = registerBlock("infested_sculk_wood",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WOOD).sound(SoundType.SCULK)));
    public static final DeferredBlock<Block> STRIPPED_INFESTED_SCULK_LOG = registerBlock("stripped_infested_sculk_log",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_LOG).sound(SoundType.SCULK)));
    public static final DeferredBlock<Block> STRIPPED_INFESTED_SCULK_WOOD = registerBlock("stripped_infested_sculk_wood",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_WOOD).sound(SoundType.SCULK)));

    public static final DeferredBlock<Block> INFESTED_SCULK_PLANKS = registerBlock("infested_sculk_planks",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).sound(SoundType.SCULK)) {
                @Override
                public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return true;
                }

                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 20;
                }

                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 5;
                }
            });

    public static final DeferredBlock<Block> INFESTED_SCULK_LEAVES = registerBlock("infested_sculk_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES).sound(SoundType.SCULK)) {
                @Override
                public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return true;
                }

                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 60;
                }

                @Override
                public int getFireSpreadSpeed( BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 30;
                }
            });

    public static final DeferredBlock<Block> INFESTED_SCULK_SAPLING = registerBlock("infested_sculk_sapling",
            () -> new ModSaplingBlock(ModTreeGrowers.INFESTED_SCULK, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING).sound(SoundType.SCULK)));

    // ------------------------------------------ SCULK JUNGLE SET ------------------------------------------------------- //

    public static final DeferredBlock<Block> SCULK_JUNGLE_LOG = registerBlock("sculk_jungle_log",
            () -> new ModJungleFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.JUNGLE_LOG).sound(SoundType.SCULK)));
    public static final DeferredBlock<Block> SCULK_JUNGLE_WOOD = registerBlock("sculk_jungle_wood",
            () -> new ModJungleFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.JUNGLE_WOOD).sound(SoundType.SCULK)));
    public static final DeferredBlock<Block> STRIPPED_SCULK_JUNGLE_LOG = registerBlock("stripped_sculk_jungle_log",
            () -> new ModJungleFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_JUNGLE_LOG).sound(SoundType.SCULK)));
    public static final DeferredBlock<Block> STRIPPED_SCULK_JUNGLE_WOOD = registerBlock("stripped_sculk_jungle_wood",
            () -> new ModJungleFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_JUNGLE_WOOD).sound(SoundType.SCULK)));

    public static final DeferredBlock<Block> SCULK_JUNGLE_PLANKS = registerBlock("sculk_jungle_planks",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.JUNGLE_PLANKS).sound(SoundType.SCULK)) {
                @Override
                public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return true;
                }

                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 20;
                }

                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 5;
                }
            });

    public static final DeferredBlock<Block> SCULK_JUNGLE_LEAVES = registerBlock("sculk_jungle_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.JUNGLE_LEAVES).sound(SoundType.SCULK)) {
                @Override
                public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return true;
                }

                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 60;
                }

                @Override
                public int getFireSpreadSpeed( BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 30;
                }
            });

    public static final DeferredBlock<Block> SCULK_JUNGLE_SAPLING = registerBlock("sculk_jungle_sapling",
            () -> new ModSaplingBlock(ModTreeGrowers.SCULK_JUNGLE, BlockBehaviour.Properties.ofFullCopy(Blocks.JUNGLE_SAPLING).sound(SoundType.SCULK)));


    // ------------------------------------- SCULK KING / CROWNLANDS BLOCKS ------------------------------------ //

    public static final DeferredBlock<Block> ROYAL_SCULK_GRASS_BLOCK = registerBlock("royal_sculk_grass_block",
            () -> new SculkSanctumGrassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK)
                    .sound(SoundType.SCULK)));

    public static final DeferredBlock<Block> ROYAL_SCULK_SOIL = registerBlock("royal_sculk_soil",
            () -> new SculkBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DIRT)
                    .sound(SoundType.SCULK)));

    public static final DeferredBlock<Block> ROYAL_SCULK_STONE = registerBlock("royal_sculk_stone",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE)
                    .strength(3.5F, 6.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.SCULK)));

    public static final DeferredBlock<Block> ROYAL_SCULK_CROWNSTONE = registerBlock("royal_sculk_crownstone",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.CHISELED_DEEPSLATE)
                    .strength(4.0F, 7.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.SCULK)
                    .lightLevel(state -> 4)));

    public static final DeferredBlock<Block> CROWNWOOD_LOG = registerBlock("crownwood_log",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WARPED_STEM)
                    .strength(2.0F)
                    .sound(SoundType.STEM)));

    public static final DeferredBlock<Block> DARK_ROYAL_SCULK = registerBlock("dark_royal_sculk",
            () -> new SculkBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SCULK_CATALYST)
                    .sound(SoundType.SCULK_CATALYST)));

    public static final DeferredBlock<Block> CROWNWOOD_LEAVES = registerBlock("crownwood_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES)
                    .sound(SoundType.SCULK)) {
                @Override
                public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return true;
                }

                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 45;
                }

                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 20;
                }
            });

    public static final DeferredBlock<Block> CROWNWOOD_SAPLING = registerBlock("crownwood_sapling",
            () -> new ModSaplingBlock(ModTreeGrowers.CROWNWOOD,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING)
                            .sound(SoundType.SCULK)));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register( IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
