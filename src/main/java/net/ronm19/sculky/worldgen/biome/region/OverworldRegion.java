package net.ronm19.sculky.worldgen.biome.region;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.ronm19.sculky.block.ModBlocks;
import net.ronm19.sculky.worldgen.biome.ModBiomes;
import terrablender.api.ParameterUtils;
import terrablender.api.Region;
import terrablender.api.RegionType;
import terrablender.api.VanillaParameterOverlayBuilder;
import com.mojang.datafixers.util.Pair;
import java.util.function.Consumer;

public class OverworldRegion extends Region {
    public OverworldRegion( ResourceLocation name, int weight ) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes( Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper ) {
        VanillaParameterOverlayBuilder builder = new VanillaParameterOverlayBuilder();

            // 🌲 SCULK FOREST — Rarer, denser biome, more rugged inland
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.span(
                        ParameterUtils.Temperature.NEUTRAL,
                        ParameterUtils.Temperature.NEUTRAL)) // VERY narrow → rare
                .humidity(ParameterUtils.Humidity.span(
                        ParameterUtils.Humidity.NEUTRAL,
                        ParameterUtils.Humidity.HUMID))
                .continentalness(ParameterUtils.Continentalness.span(
                        ParameterUtils.Continentalness.FAR_INLAND,
                        ParameterUtils.Continentalness.FAR_INLAND)) // ultra rare
                .erosion(ParameterUtils.Erosion.span(
                        ParameterUtils.Erosion.EROSION_2,
                        ParameterUtils.Erosion.EROSION_3))
                .depth(ParameterUtils.Depth.SURFACE)
                .weirdness(ParameterUtils.Weirdness.span(
                        ParameterUtils.Weirdness.LOW_SLICE_VARIANT_ASCENDING,
                        ParameterUtils.Weirdness.LOW_SLICE_VARIANT_ASCENDING)) // CRITICAL RARITY
                .build().forEach(point -> builder.add(point, ModBiomes.SCULK_FOREST));

        // ✅ Register both to the mapper
            builder.build().forEach(mapper);
        }
    }