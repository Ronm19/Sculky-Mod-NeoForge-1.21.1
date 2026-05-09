package net.ronm19.sculky.entity.client;

import net.minecraft.client.model.DolphinModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.DolphinCarryingItemLayer;
import net.minecraft.resources.ResourceLocation;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.SculkDolphinEntity;
import net.ronm19.sculky.entity.layer.custom.SculkDolphinCarryingItemLayer;
import org.jetbrains.annotations.NotNull;

public class SculkDolphinRenderer extends MobRenderer<SculkDolphinEntity, DolphinModel<SculkDolphinEntity>> {
    private static final ResourceLocation SCULK_DOLPHIN_LOCATION = ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculk_dolphin/sculk_dolphin.png");

    public SculkDolphinRenderer( EntityRendererProvider.Context context) {
        super(context, new DolphinModel<>(context.bakeLayer(ModelLayers.DOLPHIN)), 0.7F);
        this.addLayer(new SculkDolphinCarryingItemLayer(this, context.getItemInHandRenderer()));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation( @NotNull SculkDolphinEntity sculkDolphinEntity ) {
        return SCULK_DOLPHIN_LOCATION;
    }
}
