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
    public OverworldRegion(ResourceLocation name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry,
                          Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {

        VanillaParameterOverlayBuilder builder = new VanillaParameterOverlayBuilder();

        // 🌲 SCULK FOREST — Core Infection Biome
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.span(
                        ParameterUtils.Temperature.NEUTRAL,
                        ParameterUtils.Temperature.WARM))

                // Avoid vanilla jungle overlap
                .humidity(ParameterUtils.Humidity.span(
                        ParameterUtils.Humidity.NEUTRAL,
                        ParameterUtils.Humidity.HUMID))

                .continentalness(ParameterUtils.Continentalness.span(
                        ParameterUtils.Continentalness.NEAR_INLAND,
                        ParameterUtils.Continentalness.MID_INLAND))

                // Mid-erosion = rolling terrain
                .erosion(ParameterUtils.Erosion.span(
                        ParameterUtils.Erosion.EROSION_3,
                        ParameterUtils.Erosion.EROSION_5))

                .depth(ParameterUtils.Depth.SURFACE)

                // Mid weirdness lane
                .weirdness(ParameterUtils.Weirdness.span(
                        ParameterUtils.Weirdness.MID_SLICE_NORMAL_ASCENDING,
                        ParameterUtils.Weirdness.MID_SLICE_NORMAL_DESCENDING))

                .build()
                .forEach(point -> builder.add(point, ModBiomes.SCULK_FOREST));


        // 🌴 SCULK JUNGLE — Expanded but separated
        new ParameterUtils.ParameterPointListBuilder()

                .temperature(ParameterUtils.Temperature.span(
                        ParameterUtils.Temperature.NEUTRAL,
                        ParameterUtils.Temperature.HOT))

                .humidity(ParameterUtils.Humidity.span(
                        ParameterUtils.Humidity.WET,
                        ParameterUtils.Humidity.WET)) // exclusive lane

                .continentalness(ParameterUtils.Continentalness.span(
                        ParameterUtils.Continentalness.MID_INLAND,
                        ParameterUtils.Continentalness.FAR_INLAND))

                .erosion(ParameterUtils.Erosion.span(
                        ParameterUtils.Erosion.EROSION_5,
                        ParameterUtils.Erosion.EROSION_6))

                .depth(ParameterUtils.Depth.SURFACE)

                .weirdness(ParameterUtils.Weirdness.span(
                        ParameterUtils.Weirdness.LOW_SLICE_NORMAL_DESCENDING,
                        ParameterUtils.Weirdness.LOW_SLICE_VARIANT_ASCENDING))

                .build()
                .forEach(point -> builder.add(point, ModBiomes.SCULK_JUNGLE));


        // 🏜️ SCULK WASTES — Dry / Corrupted Variant
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.span(
                        ParameterUtils.Temperature.NEUTRAL,
                        ParameterUtils.Temperature.WARM))

                // Opposite of Jungle
                .humidity(ParameterUtils.Humidity.span(
                        ParameterUtils.Humidity.ARID,
                        ParameterUtils.Humidity.DRY))

                .continentalness(ParameterUtils.Continentalness.span(
                        ParameterUtils.Continentalness.MID_INLAND,
                        ParameterUtils.Continentalness.FAR_INLAND))

                // Rough terrain
                .erosion(ParameterUtils.Erosion.span(
                        ParameterUtils.Erosion.EROSION_0,
                        ParameterUtils.Erosion.EROSION_2))

                .depth(ParameterUtils.Depth.SURFACE)

                // High weirdness = isolated biome
                .weirdness(ParameterUtils.Weirdness.span(
                        ParameterUtils.Weirdness.HIGH_SLICE_VARIANT_ASCENDING,
                        ParameterUtils.Weirdness.HIGH_SLICE_VARIANT_DESCENDING))

                .build()
                .forEach(point -> builder.add(point, ModBiomes.SCULK_WASTES));


        // 🔧 Finalize region injection
        builder.build().forEach(mapper);
    }
}





