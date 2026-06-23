package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.SculkOracleEntity;
import net.ronm19.sculky.entity.layer.ModModelLayers;
import org.jetbrains.annotations.NotNull;

public class SculkOracleRenderer extends MobRenderer<SculkOracleEntity, SculkOracleModel<SculkOracleEntity>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculk_oracle/sculk_oracle.png");

    public SculkOracleRenderer( EntityRendererProvider.Context context) {
        super(context, new SculkOracleModel<>(context.bakeLayer(ModModelLayers.SCULK_ORACLE)), 0.5F);
    }

    @Override
    public void render( SculkOracleEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                        @NotNull MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.08D + entity.getHoverOffset(partialTick), 0.0D);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation( @NotNull SculkOracleEntity entity) {
        return TEXTURE;
    }
}