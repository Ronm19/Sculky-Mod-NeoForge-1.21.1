package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.InfestedEyeEntity;
import net.ronm19.sculky.entity.custom.SculkWolfAlphaEntity;
import net.ronm19.sculky.entity.layer.ModModelLayers;
import org.jetbrains.annotations.NotNull;

public class InfestedEyeRenderer extends MobRenderer<InfestedEyeEntity, InfestedEyeModel<InfestedEyeEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/infested_eye/infested_eye.png");

    public InfestedEyeRenderer( EntityRendererProvider.Context context) {
        super(context, new InfestedEyeModel<>(context.bakeLayer(ModModelLayers.INFESTED_EYE)), 0.5F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull InfestedEyeEntity infestedEyeEntity ) {
        return TEXTURE;
    }

    @Override
    public void render(@NotNull InfestedEyeEntity entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight ) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
