package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.EvokerFangsModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.projectile.SculkFangsEntity;
import org.jetbrains.annotations.NotNull;

public class SculkFangsRenderer extends EntityRenderer<SculkFangsEntity> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculk_fangs/sculk_fangs.png");

    private final EvokerFangsModel<SculkFangsEntity> model;

    public SculkFangsRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new EvokerFangsModel<>(context.bakeLayer(ModelLayers.EVOKER_FANGS));
    }

    @Override
    public void render(@NotNull SculkFangsEntity entity, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        float animationProgress = entity.getAnimationProgress(partialTicks);

        if (animationProgress != 0.0F) {
            float scale = 2.0F;

            if (animationProgress > 0.9F) {
                scale *= (1.0F - animationProgress) / 0.1F;
            }

            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F - entity.getYRot()));
            poseStack.scale(-scale, -scale, scale);
            poseStack.translate(0.0D, -0.626D, 0.0D);
            poseStack.scale(0.5F, 0.5F, 0.5F);

            this.model.setupAnim(entity, animationProgress, 0.0F, 0.0F, entity.getYRot(), entity.getXRot());

            VertexConsumer vertexConsumer = buffer.getBuffer(this.model.renderType(TEXTURE));
            this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);

            poseStack.popPose();

            super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        }
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SculkFangsEntity entity) {
        return TEXTURE;
    }
}