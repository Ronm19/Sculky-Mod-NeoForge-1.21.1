package net.ronm19.sculky.entity.layer.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.client.SculkHeraldModel;
import net.ronm19.sculky.entity.custom.SculkHeraldEntity;

public class SculkHeraldEyesLayer extends RenderLayer<SculkHeraldEntity, SculkHeraldModel<SculkHeraldEntity>> {
    private static final ResourceLocation EYES =
            ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculk_herald/sculk_herald_eyes.png");

    public SculkHeraldEyesLayer(RenderLayerParent<SculkHeraldEntity, SculkHeraldModel<SculkHeraldEntity>> renderer) {
        super(renderer);
    }

    @Override
    public void render(
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            SculkHeraldEntity entity,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.eyes(EYES));

        this.getParentModel().renderToBuffer(
                poseStack,
                vertexConsumer,
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                -1
        );
    }
}