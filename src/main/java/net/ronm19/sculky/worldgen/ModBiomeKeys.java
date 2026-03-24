package net.ronm19.sculky.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

public class ModBiomeKeys {

    public static final ResourceKey<Biome> SANCTUM_WASTES =
            ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath("sculky", "sanctum_wastes"));
}