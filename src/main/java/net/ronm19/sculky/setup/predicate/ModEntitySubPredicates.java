package net.ronm19.sculky.setup.predicate;

import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.advancements.critereon.EntitySubPredicates;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class ModEntitySubPredicates extends EntitySubPredicates {
    public static MapCodec<SculkSlimePredicate> SCULK_SLIME;


    private static <T extends EntitySubPredicate> MapCodec register( String name, MapCodec<T> codec) {
        return (MapCodec) Registry.register(BuiltInRegistries.ENTITY_SUB_PREDICATE_TYPE, name, codec);
    }

    static{
        SCULK_SLIME = register("sculk_slime", SculkSlimePredicate.CODEC);
    }
}


