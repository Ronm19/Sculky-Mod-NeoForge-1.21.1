package net.ronm19.sculky.worldgen.ore;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.ronm19.sculky.SculkyMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.ronm19.sculky.SculkyMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.ronm19.sculky.SculkyMod;
import org.jetbrains.annotations.NotNull;

public class ModPlacementModifierTypes {

    public static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_MODIFIER_TYPES =
            DeferredRegister.create(Registries.PLACEMENT_MODIFIER_TYPE, SculkyMod.MOD_ID);

    public static final DeferredHolder<PlacementModifierType<?>, PlacementModifierType<NearSculkPlacement>> NEAR_SCULK =
            PLACEMENT_MODIFIER_TYPES.register("near_sculk",
                    () -> new PlacementModifierType<NearSculkPlacement>() {
                        @Override
                        public com.mojang.serialization.@NotNull MapCodec<NearSculkPlacement> codec() {
                            return NearSculkPlacement.MAP_CODEC;
                        }
                    });

    public static void register(IEventBus eventBus) {
        PLACEMENT_MODIFIER_TYPES.register(eventBus);
    }
}

