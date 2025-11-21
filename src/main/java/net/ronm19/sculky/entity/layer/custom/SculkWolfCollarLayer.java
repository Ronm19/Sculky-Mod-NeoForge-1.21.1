package net.ronm19.sculky.entity.layer.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.ronm19.sculky.entity.client.SculkWolfModel;
import net.ronm19.sculky.entity.custom.SculkWolfEntity;

public class SculkWolfCollarLayer extends RenderLayer<SculkWolfEntity, SculkWolfModel<SculkWolfEntity>> {
    private static final ResourceLocation WOLF_COLLAR_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/wolf/wolf_collar.png");

    public SculkWolfCollarLayer( RenderLayerParent<SculkWolfEntity, SculkWolfModel<SculkWolfEntity>> renderer) {
        super(renderer);
    }

    public void render( PoseStack poseStack, MultiBufferSource buffer, int packedLight, SculkWolfEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (livingEntity.isTame() && !livingEntity.isInvisible()) {
            int i = livingEntity.getCollarColor().getTextureDiffuseColor();
            VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(WOLF_COLLAR_LOCATION));
            ((SculkWolfModel<?>)this.getParentModel()).renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, i);
        }

    }
}