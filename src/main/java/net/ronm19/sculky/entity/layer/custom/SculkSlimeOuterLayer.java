package net.ronm19.sculky.entity.layer.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.SculkSlimeEntity;

public class SculkSlimeOuterLayer extends RenderLayer<SculkSlimeEntity, SlimeModel<SculkSlimeEntity>> {

    private static final ResourceLocation OUTER_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID,
                    "textures/entity/sculk_slime/sculk_slime_outer.png");

    private final SlimeModel<SculkSlimeEntity> model;

    public SculkSlimeOuterLayer( RenderLayerParent<SculkSlimeEntity, SlimeModel<SculkSlimeEntity>> parent, EntityModelSet modelSet) {
        super(parent);
        this.model = new SlimeModel<>(modelSet.bakeLayer(ModelLayers.SLIME_OUTER));
    }

    @Override
    public void render( PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                        SculkSlimeEntity entity, float limbSwing, float limbSwingAmount,
                        float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

        VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.entityTranslucent(OUTER_TEXTURE));

        this.getParentModel().copyPropertiesTo(this.model);
        this.model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
        this.model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        this.model.renderToBuffer(poseStack, vertexconsumer, packedLight,
                LivingEntityRenderer.getOverlayCoords(entity, 0.0F)
        );
    }
}