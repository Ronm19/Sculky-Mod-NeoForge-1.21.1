package net.ronm19.sculky.setup.predicate;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.ronm19.sculky.entity.custom.SculkSlimeEntity;

import javax.annotation.Nullable;

public record SculkSlimePredicate(MinMaxBounds.Ints size) implements EntitySubPredicate {
    public static final MapCodec<SculkSlimePredicate> CODEC = RecordCodecBuilder.mapCodec(( p_337395_) -> p_337395_.group(MinMaxBounds.Ints.CODEC.optionalFieldOf("size", MinMaxBounds.Ints.ANY).forGetter(SculkSlimePredicate::size)).apply(p_337395_, SculkSlimePredicate::new));

    public static SculkSlimePredicate sized( MinMaxBounds.Ints size) {
        return new SculkSlimePredicate(size);
    }

    public boolean matches(Entity entity, ServerLevel level, @Nullable Vec3 position) {
        boolean var10000;
        if (entity instanceof SculkSlimeEntity sculkSlimeEntity) {
            var10000 = this.size.matches(sculkSlimeEntity.getSize());
        } else {
            var10000 = false;
        }

        return var10000;
    }

    public MapCodec<? extends EntitySubPredicate> codec() {
        return ModEntitySubPredicates.SCULK_SLIME;
    }
}
