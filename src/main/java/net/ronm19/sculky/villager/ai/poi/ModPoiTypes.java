package net.ronm19.sculky.villager.ai.poi;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.village.poi.PoiType;

public class ModPoiTypes {

    private static ResourceKey<PoiType> createKey( String name) {
        return ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, ResourceLocation.withDefaultNamespace(name));
    }

    public static final ResourceKey<PoiType> SANCTUM_PORTAL = createKey("sanctum_portal");
}
