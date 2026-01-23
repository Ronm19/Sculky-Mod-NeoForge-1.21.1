package net.ronm19.sculky.worldgen.biome;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.worldgen.biome.region.OverworldRegion;
import terrablender.api.Regions;

public class ModBiomes {
    public static final ResourceKey<Biome> SCULK_FOREST = registerBiomeKey("sculk_forest");
    public static final ResourceKey<Biome> SCULK_WASTES = registerBiomeKey("sculk_wastes");

    public static void registerBiomes() {
        Regions.register(new OverworldRegion(ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "sculky_overworld"), 20));
    }

    public static void bootstrap( BootstrapContext<Biome> context) {
        var carver = context.lookup(Registries.CONFIGURED_CARVER);
        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);

        register(context, SCULK_FOREST, ModOverworldBiomes.sculkForest(placedFeatures, carver));
        register(context, SCULK_WASTES, ModOverworldBiomes.sculkWastes(placedFeatures, carver));
    }


    private static void register(BootstrapContext<Biome> context, ResourceKey<Biome> key, Biome biome) {
        context.register(key, biome);
    }

    private static ResourceKey<Biome> registerBiomeKey(String name) {
        return ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, name));
    }
}
