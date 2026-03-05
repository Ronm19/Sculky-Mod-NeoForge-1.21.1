package net.ronm19.sculky.worldgen;

import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.worldgen.biome.ModBiomes;

public class ModBiomeModifiers {
    public static final ResourceKey<BiomeModifier> ADD_TREE_INFESTED_SCULK = registerKey("add_tree_infested_sculk");
    public static final ResourceKey<BiomeModifier> ADD_TREE_SCULK_JUNGLE = registerKey("add_tree_sculk_jungle");
    public static final ResourceKey<BiomeModifier> ADD_TREE_MEGA_SCULK_JUNGLE = registerKey("add_tree_mega_sculk_jungle");

    public static final ResourceKey<BiomeModifier> ADD_INFESTED_SCULK_ORE = registerKey("add_infested_sculk_ore");
    public static final ResourceKey<BiomeModifier> ADD_DEEPSLATE_INFESTED_SCULK_ORE = registerKey("add_deepslate_infested_sculk_ore");

    public static final ResourceKey<BiomeModifier> ADD_SCULKBLOOM = registerKey("add_sculkbloom");
    public static final ResourceKey<BiomeModifier> ADD_ECHOBLOOM = registerKey("add_echobloom");



    public static void bootstrap( BootstrapContext<BiomeModifier> context) {
        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        var biomes = context.lookup(Registries.BIOME);

        context.register(ADD_TREE_INFESTED_SCULK, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(ModBiomes.SCULK_FOREST)),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.INFESTED_SCULK_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));

        context.register(ADD_TREE_SCULK_JUNGLE, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(ModBiomes.SCULK_JUNGLE)),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.INFESTED_JUNGLE_SCULK_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));

        context.register(ADD_TREE_MEGA_SCULK_JUNGLE, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(ModBiomes.SCULK_JUNGLE)),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.INFESTED_MEGA_JUNGLE_SCULK_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));
        



        context.register(ADD_DEEPSLATE_INFESTED_SCULK_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.DEEPSLATE_INFESTED_SCULK_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_INFESTED_SCULK_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.INFESTED_SCULK_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES));



        context.register(ADD_SCULKBLOOM, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(ModBiomes.SCULK_FOREST)),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.SCULKBLOOM_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));

        context.register(ADD_ECHOBLOOM, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(ModBiomes.SCULK_JUNGLE)),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.ECHOBLOOM_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));


    }


    private static ResourceKey<BiomeModifier> registerKey( String name) {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, name));
    }
}
