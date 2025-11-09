package net.ronm19.sculky.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.ronm19.sculky.SculkyMod;

public class ModTags {
    public static class Blocks {

        public static final TagKey<Block> INCORRECT_FOR_INFESTED_SCULK_TOOL = createTag("incorrect_for_infested_sculk_tool");
        public static final TagKey<Block> NEEDS_INFESTED_SCULK_TOOL = createTag("needs_infested_sculk_tool");

        private static TagKey<Block> createTag( String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, name));
        }
    }

    public static class Items {


        private static TagKey<Item> createTag( String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, name));
        }
    }
}