package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.layer.ModModelLayers;
import net.ronm19.sculky.entity.custom.ThroneboundWraithEntity;
import org.jetbrains.annotations.NotNull;

public class ThroneboundWraithRenderer extends MobRenderer<ThroneboundWraithEntity, ThroneboundWraithModel<ThroneboundWraithEntity>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/thronebound_wraith/thronebound_wraith.png");

    public ThroneboundWraithRenderer(EntityRendererProvider.Context context) {
        super(context, new ThroneboundWraithModel<>(context.bakeLayer(ModModelLayers.THRONEBOUND_WRAITH)), 0.5F);
    }

    @Override
    public void render(ThroneboundWraithEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        // Visual hover only. Does not affect hitbox or pathfinding.
        poseStack.translate(0.0D, 0.12D + entity.getHoverOffset(partialTick), 0.0D);

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation( ThroneboundWraithEntity entity) {
        return TEXTURE;
    }
}