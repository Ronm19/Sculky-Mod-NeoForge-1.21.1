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

        // 🌲 SCULK FOREST (slightly wider so it competes)
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.span(
                        ParameterUtils.Temperature.NEUTRAL,
                        ParameterUtils.Temperature.NEUTRAL))
                .humidity(ParameterUtils.Humidity.span(
                        ParameterUtils.Humidity.NEUTRAL,
                        ParameterUtils.Humidity.HUMID))
                .continentalness(ParameterUtils.Continentalness.span(
                        ParameterUtils.Continentalness.MID_INLAND,
                        ParameterUtils.Continentalness.FAR_INLAND))
                .erosion(ParameterUtils.Erosion.span(
                        ParameterUtils.Erosion.EROSION_2,
                        ParameterUtils.Erosion.EROSION_5)) // was 2–4
                .depth(ParameterUtils.Depth.SURFACE)
                .weirdness(ParameterUtils.Weirdness.span(
                        ParameterUtils.Weirdness.LOW_SLICE_NORMAL_DESCENDING,
                        ParameterUtils.Weirdness.LOW_SLICE_VARIANT_ASCENDING))
                .build()
                .forEach(point -> builder.add(point, ModBiomes.SCULK_FOREST));


// 🏜️ SCULK WASTES (wider but still desert-only)
        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.span(
                        ParameterUtils.Temperature.HOT,
                        ParameterUtils.Temperature.HOT))
                .humidity(ParameterUtils.Humidity.span(
                        ParameterUtils.Humidity.ARID,
                        ParameterUtils.Humidity.ARID))
                .continentalness(ParameterUtils.Continentalness.span(
                ParameterUtils.Continentalness.MID_INLAND,
                ParameterUtils.Continentalness.FAR_INLAND)) // FAR only
                .erosion(ParameterUtils.Erosion.span(
                        ParameterUtils.Erosion.EROSION_2,
                        ParameterUtils.Erosion.EROSION_5)) // narrow
                // <-- widen one step
                .depth(ParameterUtils.Depth.SURFACE)
                .weirdness(ParameterUtils.Weirdness.span(
                        ParameterUtils.Weirdness.LOW_SLICE_NORMAL_DESCENDING,
                        ParameterUtils.Weirdness.LOW_SLICE_VARIANT_ASCENDING))
                .build()
                .forEach(point -> builder.add(point, ModBiomes.SCULK_WASTES));

        // ✅ THIS is what makes the region actually work:
        builder.build().forEach(mapper);
    }
}





