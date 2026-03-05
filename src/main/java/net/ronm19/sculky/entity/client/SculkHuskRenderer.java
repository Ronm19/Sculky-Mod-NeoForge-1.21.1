package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.SculkHuskEntity;
import org.jetbrains.annotations.NotNull;

public class SculkHuskRenderer extends MobRenderer<SculkHuskEntity, ZombieModel<SculkHuskEntity>> {
    public SculkHuskRenderer(EntityRendererProvider.Context context) {
        super(context, new ZombieModel<>(context.bakeLayer(ModelLayers.HUSK)), 0.5F);
    }

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculk_husk/sculk_husk.png");

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SculkHuskEntity entity) {
        return TEXTURE;
    }

    protected void scale(@NotNull SculkHuskEntity livingEntity, PoseStack poseStack, float partialTickTime) {
        float f = 1.0625F;
        poseStack.scale(1.0625F, 1.0625F, 1.0625F);
        super.scale(livingEntity, poseStack, partialTickTime);
    }
}
