package net.ronm19.sculky.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.enchantment.ModEnchantments;
import net.ronm19.sculky.worldgen.ModBiomeModifiers;
import net.ronm19.sculky.worldgen.ModConfiguredFeatures;
import net.ronm19.sculky.worldgen.ModPlacedFeatures;
import net.ronm19.sculky.worldgen.biome.ModBiomes;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModDataRegistryProvider extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.ENCHANTMENT, ModEnchantments ::bootstrap)
            .add(Registries.CONFIGURED_FEATURE, ModConfiguredFeatures ::bootstrap)
            .add(Registries.PLACED_FEATURE, ModPlacedFeatures ::bootstrap)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ModBiomeModifiers ::bootstrap)
            .add(Registries.BIOME, ModBiomes::bootstrap);





    public ModDataRegistryProvider( PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(SculkyMod.MOD_ID));
    }
}
