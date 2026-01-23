package net.ronm19.sculky.worldgen.biome;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.ronm19.sculky.block.ModBlocks;
import net.ronm19.sculky.worldgen.biome.ModBiomes;

public class ModSurfaceRules {

    // Forest blocks
    private static final SurfaceRules.RuleSource INFESTED_SCULK_GRASS =
            makeStateRule(ModBlocks.INFESTED_SCULK_GRASS_BLOCK.get());
    private static final SurfaceRules.RuleSource INFESTED_SCULK_DIRT =
            makeStateRule(ModBlocks.INFESTED_SCULK_DIRT_BLOCK.get());

    // Wastes blocks (use your own block names here)
    private static final SurfaceRules.RuleSource INFESTED_SCULK_SAND = makeStateRule(ModBlocks.INFESTED_SCULK_SAND.get()); // <- your new sand block
    // Optional: if you add a sandstone-ish block later
    // private static final SurfaceRules.RuleSource SCULK_SANDSTONE =
    //         makeStateRule(ModBlocks.SCULK_SANDSTONE.get());

    /** Call this one in SurfaceRuleManager. */
    public static SurfaceRules.RuleSource makeOverworldRules() {
        return SurfaceRules.sequence(
                makeSculkForestRules(),
                makeSculkWastesRules()
        );
    }

    public static SurfaceRules.RuleSource makeSculkForestRules() {
        return SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.SCULK_FOREST),
                SurfaceRules.sequence(
                        SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, INFESTED_SCULK_GRASS),
                        SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, INFESTED_SCULK_DIRT),
                        INFESTED_SCULK_DIRT // fallback ONLY inside this biome
                )
        );
    }

    public static SurfaceRules.RuleSource makeSculkWastesRules() {
        return SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.SCULK_WASTES),
                SurfaceRules.sequence(
                        SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, INFESTED_SCULK_SAND),
                        SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, INFESTED_SCULK_SAND),
                        INFESTED_SCULK_SAND // fallback ONLY inside this biome
                )
        );
    }

    private static SurfaceRules.RuleSource makeStateRule(Block block) {
        return SurfaceRules.state(block.defaultBlockState());
    }
}
