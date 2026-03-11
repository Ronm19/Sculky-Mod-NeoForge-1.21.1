package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.HollowhornEntity;
import net.ronm19.sculky.entity.layer.ModModelLayers;
import org.jetbrains.annotations.NotNull;

public class HollowhornRenderer extends MobRenderer<HollowhornEntity, HollowhornModel<HollowhornEntity>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/hollow_horn/hollow_horn.png");

    public HollowhornRenderer(EntityRendererProvider.Context context) {
        super(context, new HollowhornModel<>(context.bakeLayer(ModModelLayers.HOLLOW_HORN)), 0.7F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull HollowhornEntity hollowhornEntity) {
        return TEXTURE;
    }

    @Override
    public void render(HollowhornEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight ) {
        if(entity.isBaby()) {
            poseStack.scale(0.45f, 0.45f, 0.45f);
        }
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    protected void scale(@NotNull HollowhornEntity livingEntity, PoseStack poseStack, float partialTickTime) {
        float scale = 1.5F;
        poseStack.scale(scale, scale, scale);
        super.scale(livingEntity, poseStack, partialTickTime);
    }
}