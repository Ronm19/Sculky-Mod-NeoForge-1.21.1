package net.ronm19.sculky.worldgen.ore;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.ronm19.sculky.worldgen.ore.ModPlacementModifierTypes;

import java.util.stream.Stream;

public class NearSculkPlacement extends PlacementModifier {

    public static final MapCodec<NearSculkPlacement> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(Codec.INT.optionalFieldOf("radius", 6).forGetter(p -> p.radius))
                    .apply(instance, NearSculkPlacement::new));

    private final int radius;

    public NearSculkPlacement(int radius) {
        this.radius = radius;
    }

    @Override
    public PlacementModifierType<?> type() {
        return ModPlacementModifierTypes.NEAR_SCULK.get();
    }

    public static NearSculkPlacement of(int radius) {
        return new NearSculkPlacement(radius);
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos) {
        boolean nearSculk = BlockPos.betweenClosedStream(
                pos.offset(-radius, -radius, -radius),
                pos.offset(radius, radius, radius)
        ).anyMatch(checkPos -> context.getLevel().getBlockState(checkPos).is(Blocks.SCULK));
        return nearSculk ? Stream.of(pos) : Stream.empty();
    }
}

