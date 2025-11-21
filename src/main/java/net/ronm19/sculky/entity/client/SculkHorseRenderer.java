package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.HorseArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.SculkHorseEntity;
import net.ronm19.sculky.entity.custom.SculkWolfEntity;
import net.ronm19.sculky.entity.layer.custom.SculkHorseArmorLayer;

public class SculkHorseRenderer extends MobRenderer<SculkHorseEntity, HorseModel<SculkHorseEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculk_horse/sculk_horse.png");


    public SculkHorseRenderer( EntityRendererProvider.Context context) {
        super(context, new HorseModel<>(context.bakeLayer(ModelLayers.HORSE)), 1.1F);
        this.addLayer(new SculkHorseArmorLayer(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation( SculkHorseEntity sculkHorseEntity ) {
        return TEXTURE;
    }

    @Override
    public void render( SculkHorseEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight ) {
        if(entity.isBaby()) {
            poseStack.scale(0.45f, 0.45f, 0.45f);
        }
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
