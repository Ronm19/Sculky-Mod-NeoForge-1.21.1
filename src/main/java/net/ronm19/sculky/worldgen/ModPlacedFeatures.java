package net.ronm19.sculky.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.block.ModBlocks;
import net.ronm19.sculky.worldgen.ore.ModOrePlacements;
import net.ronm19.sculky.worldgen.ore.NearSculkPlacement;

import java.util.List;

public class ModPlacedFeatures {
    public static final ResourceKey<PlacedFeature> INFESTED_SCULK_PLACED_KEY = registerKey("infested_sculk_placed");
    public static final ResourceKey<PlacedFeature> INFESTED_JUNGLE_SCULK_PLACED_KEY = registerKey("infested_jungle_sculk_placed");
    public static final ResourceKey<PlacedFeature> INFESTED_MEGA_JUNGLE_SCULK_PLACED_KEY = registerKey("infested_mega_jungle_sculk_placed");
    public static final ResourceKey<PlacedFeature> CROWNWOOD_PLACED_KEY = registerKey("crownwood_placed");

    public static final ResourceKey<PlacedFeature> INFESTED_SCULK_ORE_PLACED_KEY = registerKey("infested_sculk_ore_placed");
    public static final ResourceKey<PlacedFeature> DEEPSLATE_INFESTED_SCULK_ORE_PLACED_KEY = registerKey("deepslate_infested_sculk_ore_placed");

    public static final ResourceKey<PlacedFeature> SCULKBLOOM_PLACED_KEY = registerKey("sculkbloom_placed");
    public static final ResourceKey<PlacedFeature> ECHOBLOOM_PLACED_KEY = registerKey("echobloom_placed");





    public static void bootstrap( BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        // ---- Trees ----- //

        register(context, INFESTED_SCULK_PLACED_KEY,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.INFESTED_SCULK_KEY),
                List.of(
                        PlacementUtils.countExtra(3, 0.1f, 2),
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES),

                        BlockPredicateFilter.forPredicate(
                                BlockPredicate.matchesTag(BlockPos.ZERO.below(), BlockTags.DIRT)
                        ),

                        BiomeFilter.biome()
                )
        );

        register(context, CROWNWOOD_PLACED_KEY,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.CROWNWOOD_KEY),
                List.of(
                        PlacementUtils.countExtra(2, 0.1F, 1),
                        InSquarePlacement.spread(),
                        HeightmapPlacement.onHeightmap(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES),

                        BlockPredicateFilter.forPredicate(
                                BlockPredicate.anyOf(
                                        BlockPredicate.matchesBlocks(BlockPos.ZERO.below(), ModBlocks.ROYAL_SCULK_GRASS_BLOCK.get()),
                                        BlockPredicate.matchesBlocks(BlockPos.ZERO.below(), ModBlocks.ROYAL_SCULK_SOIL.get())
                                )
                        ),

                        BiomeFilter.biome()
                )
        );

        register(context, INFESTED_JUNGLE_SCULK_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.SCULK_JUNGLE_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(1, 0.1f, 2),
                        ModBlocks.SCULK_JUNGLE_SAPLING.get()));

        register(context, INFESTED_MEGA_JUNGLE_SCULK_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.SCULK_JUNGLE_MEGA_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(2, 0.1f, 2),
                        ModBlocks.SCULK_JUNGLE_SAPLING.get()));



        // ---- Ores ----- //

        register(context,
                INFESTED_SCULK_ORE_PLACED_KEY,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.OVERWORLD_INFESTED_SCULK_ORE_KEY),
                List.of(
                        CountPlacement.of(8), // number of veins per chunk
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(
                                VerticalAnchor.absolute(-48), // lower limit
                                VerticalAnchor.absolute(16)   // upper limit
                        ),
                        NearSculkPlacement.of(8), // custom sculk-proximity radius
                        BiomeFilter.biome()
                ));

        register(context,
                DEEPSLATE_INFESTED_SCULK_ORE_PLACED_KEY,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.OVERWORLD_DEEPSLATE_INFESTED_SCULK_ORE_KEY),
                List.of(
                        CountPlacement.of(8), // number of veins per chunk
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(
                                VerticalAnchor.absolute(-48), // lower limit
                                VerticalAnchor.absolute(16)   // upper limit
                        ),
                        NearSculkPlacement.of(8), // custom sculk-proximity radius
                        BiomeFilter.biome()));

        // ---- Plants ----- //

        register(context, SCULKBLOOM_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.SCULKBLOOM_KEY),
                List.of(RarityFilter.onAverageOnceEvery(16), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));

        register(context, ECHOBLOOM_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.ECHOBLOOM_KEY),
                List.of(RarityFilter.onAverageOnceEvery(16), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));

    }

    private static ResourceKey<PlacedFeature> registerKey( String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, name));
    }

    private static void register(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuration,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}
