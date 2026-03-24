package net.ronm19.sculky.entity.layer.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.ShadowPantherEntity;

public class ShadowPantherEyesLayer<T extends ShadowPantherEntity, M extends EntityModel<T>>
        extends RenderLayer<T, M> {

    private static final ResourceLocation EYES_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    SculkyMod.MOD_ID,
                    "textures/entity/shadow_panther/shadow_panther_eyes.png"
            );

    public ShadowPantherEyesLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack,
                       MultiBufferSource buffer,
                       int packedLight,
                       T entity,
                       float limbSwing,
                       float limbSwingAmount,
                       float partialTick,
                       float ageInTicks,
                       float netHeadYaw,
                       float headPitch) {

        if (!entity.isInDarkness()) {
            return;
        }

        float glow = Math.max(0.35F, entity.getEyeGlowStrength());
        int brightness = Math.max(90, Math.min(255, (int)(glow * 255.0F)));

        int red = Math.max(40, brightness / 4);
        int green = Math.max(140, Math.min(255, brightness));
        int blue = 255;
        int color = (255 << 24) | (red << 16) | (green << 8) | blue;

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.eyes(EYES_TEXTURE));
        this.getParentModel().renderToBuffer(
                poseStack,
                vertexConsumer,
                15728640,
                OverlayTexture.NO_OVERLAY,
                color
        );
    }
}