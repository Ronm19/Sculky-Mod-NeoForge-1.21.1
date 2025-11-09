package net.ronm19.sculky.block;


import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.block.custom.InfestedCakeBlock;
import net.ronm19.sculky.item.ModItems;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(SculkyMod.MOD_ID);

    public static final DeferredBlock<Block> INFESTED_SCULK_BLOCK = registerBlock("infested_sculk_block",
            () -> new Block(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops().sound(SoundType.SCULK)));
    public static final DeferredBlock<Block> RAW_INFESTED_SCULK_BLOCK = registerBlock("raw_infested_sculk_block",
            () -> new Block(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops().sound(SoundType.SCULK)));

    public static final DeferredBlock<Block> SCULK_ORE = registerBlock("sculk_ore",
            () -> new DropExperienceBlock(UniformInt.of(1, 5), BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops().sound(SoundType.SCULK)));
    public static final DeferredBlock<Block> DEEPSLATE_SCULK_ORE = registerBlock("deepslate_sculk_ore",
            () -> new DropExperienceBlock(UniformInt.of(2, 7),BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops().sound(SoundType.SCULK)));

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
