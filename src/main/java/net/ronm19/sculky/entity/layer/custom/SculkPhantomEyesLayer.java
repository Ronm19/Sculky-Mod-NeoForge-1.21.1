package net.ronm19.sculky.entity.layer.custom;

import net.minecraft.client.model.PhantomModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.SculkPhantomEntity;

public class SculkPhantomEyesLayer <T extends SculkPhantomEntity> extends EyesLayer<T, PhantomModel<T>> {
    private static final RenderType SCULK_PHANTOM_EYES = RenderType.eyes(ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculk_phantom/sculk_phantom_eyes.png"));

    public SculkPhantomEyesLayer(RenderLayerParent<T, PhantomModel<T>> context) {
        super(context);
    }

    public RenderType renderType() {
        return SCULK_PHANTOM_EYES;
    }
}
