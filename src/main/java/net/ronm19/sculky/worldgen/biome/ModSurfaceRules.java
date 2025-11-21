package net.ronm19.sculky.worldgen.biome;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.ronm19.sculky.block.ModBlocks;
import net.ronm19.sculky.worldgen.biome.ModBiomes;

public class ModSurfaceRules {

    private static final SurfaceRules.RuleSource INFESTED_SCULK_GRASS_BLOCK = makeStateRule(ModBlocks.INFESTED_SCULK_GRASS_BLOCK.get());
    private static final SurfaceRules.RuleSource INFESTED_SCULK_DIRT_BLOCK = makeStateRule(ModBlocks.INFESTED_SCULK_DIRT_BLOCK.get());

    public static SurfaceRules.RuleSource makeSculkForestRules() {
        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.SCULK_FOREST),
                        SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, INFESTED_SCULK_GRASS_BLOCK), INFESTED_SCULK_DIRT_BLOCK)),

                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, INFESTED_SCULK_DIRT_BLOCK)
        );
    }

    private static SurfaceRules.RuleSource makeStateRule(Block block) {
        return SurfaceRules.state(block.defaultBlockState());
    }
}
