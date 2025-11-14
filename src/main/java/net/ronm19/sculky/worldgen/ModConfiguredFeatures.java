package net.ronm19.sculky.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.block.ModBlocks;

import java.util.List;

public class ModConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> INFESTED_SCULK_KEY = registerKey("infested_sculk");

    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_INFESTED_SCULK_ORE_KEY = registerKey("infested_sculk_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_DEEPSLATE_INFESTED_SCULK_ORE_KEY = registerKey("deeplsate_infested_sculk_ore");

    public static final ResourceKey<ConfiguredFeature<?, ?>> SCULKBLOOM_KEY = registerKey("sculkbloom");




    public static void bootstrap( BootstrapContext<ConfiguredFeature<?, ?>> context) {
        register(context, INFESTED_SCULK_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.INFESTED_SCULK_LOG.get()),
                new StraightTrunkPlacer(4, 5, 3),
                BlockStateProvider.simple(ModBlocks.INFESTED_SCULK_LEAVES.get()),
                new BlobFoliagePlacer(ConstantInt.of(4), ConstantInt.of(2), 4),
                new TwoLayersFeatureSize(1, 0, 2)).build());

        RuleTest sculkReplaceables = new TagMatchTest(BlockTags.SCULK_REPLACEABLE_WORLD_GEN);

        List<OreConfiguration.TargetBlockState> overworldInfestedSculkOres = List.of(
                OreConfiguration.target(sculkReplaceables, ModBlocks.SCULK_ORE.get().defaultBlockState()));

        List<OreConfiguration.TargetBlockState> overworldDeepslateInfestedSculkOres = List.of(
                OreConfiguration.target(sculkReplaceables, ModBlocks.DEEPSLATE_SCULK_ORE.get().defaultBlockState()));

        register(context, OVERWORLD_INFESTED_SCULK_ORE_KEY, Feature.ORE, new OreConfiguration(overworldInfestedSculkOres, 9));
        register(context, OVERWORLD_DEEPSLATE_INFESTED_SCULK_ORE_KEY, Feature.ORE, new OreConfiguration(overworldDeepslateInfestedSculkOres, 9));


        register(context, SCULKBLOOM_KEY, Feature.FLOWER, new RandomPatchConfiguration(32, 6, 2,
                PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.SCULKBLOOM.get())))));

    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey( String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register( BootstrapContext<ConfiguredFeature<?, ?>> context,
                                                                                           ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
