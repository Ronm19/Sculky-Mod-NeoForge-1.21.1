package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.HollowhornEntity;
import net.ronm19.sculky.entity.custom.SalvatoreEntity;
import net.ronm19.sculky.entity.layer.ModModelLayers;
import org.jetbrains.annotations.NotNull;

public class SalvatoreRenderer extends MobRenderer<SalvatoreEntity, SalvatoreModel<SalvatoreEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/salvatore/salvatore.png");

    public SalvatoreRenderer(EntityRendererProvider.Context context) {
        super(context, new SalvatoreModel<>(context.bakeLayer(ModModelLayers.SALVATORE)), 0.7F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SalvatoreEntity salvatoreEntity) {
        return TEXTURE;
    }

    @Override
    public void render(SalvatoreEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight ) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    protected void scale(@NotNull SalvatoreEntity livingEntity, PoseStack poseStack, float partialTickTime) {
        float scale = 1.5F;
        poseStack.scale(scale, scale, scale);
        super.scale(livingEntity, poseStack, partialTickTime);
    }
}