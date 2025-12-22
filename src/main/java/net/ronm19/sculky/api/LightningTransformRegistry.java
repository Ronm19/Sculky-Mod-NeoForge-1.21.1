package net.ronm19.sculky.api;

import net.ronm19.sculky.api.interfaces.LightningTransform;
import net.minecraft.world.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

public class LightningTransformRegistry {

    private static final Map<EntityType<?>, LightningTransform> TRANSFORMS = new HashMap<>();

    public static void register(EntityType<?> type, LightningTransform transform) {
        TRANSFORMS.put(type, transform);
    }

    public static LightningTransform get(EntityType<?> type) {
        return TRANSFORMS.get(type);
    }
}
