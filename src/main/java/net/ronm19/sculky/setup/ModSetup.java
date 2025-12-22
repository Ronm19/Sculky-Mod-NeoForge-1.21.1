package net.ronm19.sculky.setup;

import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.IEventBus;
import net.ronm19.sculky.api.LightningTransformRegistry;
import net.ronm19.sculky.transform.HorseToSculkHorseTransform;

public class ModSetup {

    public static void register(IEventBus modEventBus ) {

        LightningTransformRegistry.register(EntityType.HORSE, new HorseToSculkHorseTransform());

    }
}