package net.ronm19.sculky.worldgen.biome;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.ronm19.sculky.block.ModBlocks;

public class ModSurfaceRules {

    private static final SurfaceRules.RuleSource INFESTED_SCULK_GRASS =
            makeStateRule(ModBlocks.INFESTED_SCULK_GRASS_BLOCK.get());
    private static final SurfaceRules.RuleSource INFESTED_SCULK_DIRT =
            makeStateRule(ModBlocks.INFESTED_SCULK_DIRT_BLOCK.get());

    private static final SurfaceRules.RuleSource INFESTED_SCULK_SAND =
            makeStateRule(ModBlocks.INFESTED_SCULK_SAND.get());

    private static final SurfaceRules.RuleSource INFESTED_SCULK_PODZOL =
            makeStateRule(ModBlocks.INFESTED_SCULK_PODZOL_BLOCK.get());
    private static final SurfaceRules.RuleSource INFESTED_SCULK_ROOTED_DIRT =
            makeStateRule(ModBlocks.INFESTED_SCULK_ROOTED_DIRT_BLOCK.get());

    public static SurfaceRules.RuleSource makeOverworldRules() {
        return SurfaceRules.sequence(
                biomeSurface(ModBiomes.SCULK_FOREST, INFESTED_SCULK_GRASS, INFESTED_SCULK_DIRT),
                biomeSurface(ModBiomes.SCULK_WASTES, INFESTED_SCULK_SAND, INFESTED_SCULK_SAND),
                biomeSurface(ModBiomes.SCULK_JUNGLE, INFESTED_SCULK_PODZOL, INFESTED_SCULK_ROOTED_DIRT)
        );
    }

    private static SurfaceRules.RuleSource biomeSurface(ResourceKey<Biome> biome,
                                                        SurfaceRules.RuleSource top,
                                                        SurfaceRules.RuleSource under) {
        return SurfaceRules.ifTrue(
                SurfaceRules.isBiome(biome),
                SurfaceRules.ifTrue(
                        SurfaceRules.abovePreliminarySurface(),
                        SurfaceRules.sequence(
                                // Paint cliff/slope faces first
                                SurfaceRules.ifTrue(SurfaceRules.steep(), under),

                                // Then normal topsoil
                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, top),
                                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, under),
                                SurfaceRules.ifTrue(SurfaceRules.DEEP_UNDER_FLOOR, under)
                        )
                )
        );
    }

    private static SurfaceRules.RuleSource makeStateRule(Block block) {
        return SurfaceRules.state(block.defaultBlockState());
    }
}