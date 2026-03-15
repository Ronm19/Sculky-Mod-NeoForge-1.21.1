package net.ronm19.sculky.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.ronm19.sculky.SculkyMod;

public class ModTags {
    public static class Blocks {

        public static final TagKey<Block> INCORRECT_FOR_INFESTED_SCULK_TOOL = createTag("incorrect_for_infested_sculk_tool");
        public static final TagKey<Block> NEEDS_INFESTED_SCULK_TOOL = createTag("needs_infested_sculk_tool");
        public static final TagKey<Block> SCULK_TRANSFORMABLE =  createTag("sculk_transformable");

        public static final TagKey<Block> SCULK_SPREADABLE = createTag("sculk_spreadable");

        public static final TagKey<Block> INFESTED_SCULK_SAND = createTag("sculk_sand");
        public static final TagKey<Block> SCULK_SPEED_BLOCKS = createTag("sculk_speed_blocks");

        private static TagKey<Block> createTag( String name ) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, name));
        }
    }

    public static class Items {

        public static final TagKey<Item> SCULK_RAT_STAFF = createTag("sculk_rat_staff");


        private static TagKey<Item> createTag( String name ) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, name));
        }
    }

    public static class Entities {
        public static final TagKey<EntityType<?>> SCULK_ALLIES = createTag("sculk_allies");
        public static final TagKey<EntityType<?>> SCULK_MOBS = createTag("sculk_mobs");

        private static TagKey<EntityType<?>> createTag( String name ) {
            return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, name));
        }
    }
}
