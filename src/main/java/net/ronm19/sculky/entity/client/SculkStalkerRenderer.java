package net.ronm19.sculky.entity.client;

import net.minecraft.client.model.SilverfishModel;
import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SpiderEyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.SculkParasiteEntity;
import net.ronm19.sculky.entity.custom.SculkStalkerEntity;
import org.jetbrains.annotations.NotNull;

public class SculkStalkerRenderer extends MobRenderer<SculkStalkerEntity, SpiderModel<SculkStalkerEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculk_stalker/sculk_stalker.png");

    public SculkStalkerRenderer( EntityRendererProvider.Context context) {
        super(context, new SpiderModel<>(context.bakeLayer(ModelLayers.SPIDER)), 0.8F);
   }

    @Override
    public @NotNull ResourceLocation getTextureLocation( @NotNull SculkStalkerEntity sculkStalkerEntity ) {
        return TEXTURE;
    }

    protected float getFlipDegrees(@NotNull SculkStalkerEntity livingEntity) {
        return 180.0F;
    }
}
