package net.ronm19.sculky.entity.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.SculkSpiderEntity;
import net.ronm19.sculky.entity.layer.ModModelLayers;

public class SculkSpiderRenderer extends MobRenderer<SculkSpiderEntity, SculkSpiderModel<SculkSpiderEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculk_spider/sculk_spider.png");

    public SculkSpiderRenderer( EntityRendererProvider.Context context) {
        super(context, new SculkSpiderModel<>(context.bakeLayer(ModModelLayers.SCULK_SPIDER)), 0.8F);
    }

    @Override
    public ResourceLocation getTextureLocation( SculkSpiderEntity sculkSpiderEntity ) {
        return TEXTURE;
    }

    protected float getFlipDegrees(SculkSpiderEntity livingEntity) {
        return 180.0F;
    }
}
