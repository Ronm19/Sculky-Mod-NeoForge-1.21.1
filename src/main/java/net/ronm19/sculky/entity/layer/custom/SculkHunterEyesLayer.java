package net.ronm19.sculky.entity.layer.custom;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.client.SculkHunterModel;
import net.ronm19.sculky.entity.custom.SculkHunterEntity;
import org.jetbrains.annotations.NotNull;

public class SculkHunterEyesLayer<T extends SculkHunterEntity> extends EyesLayer<T, SculkHunterModel<T>> {
    private static final RenderType SCULK_HUNTER_EYES = RenderType.eyes(ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculk_hunter/sculk_hunter_eyes.png"));

    public SculkHunterEyesLayer(RenderLayerParent<T, SculkHunterModel<T>> context) {
        super(context);
    }

    public @NotNull RenderType renderType() {
        return SCULK_HUNTER_EYES;
    }
}
