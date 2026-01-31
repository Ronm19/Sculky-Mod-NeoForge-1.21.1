package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.SilverfishModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.SculkParasiteEntity;
import net.ronm19.sculky.entity.custom.SculkRatEntity;
import net.ronm19.sculky.entity.layer.ModModelLayers;
import org.jetbrains.annotations.NotNull;

public class SculkRatRenderer extends MobRenderer<SculkRatEntity, SculkRatModel<SculkRatEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculk_rat/sculk_rat.png");

    public SculkRatRenderer( EntityRendererProvider.Context context) {
        super(context, new SculkRatModel<>(context.bakeLayer(ModModelLayers.SCULK_RAT)), 0.5F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SculkRatEntity sculkRatEntity ) {
        return TEXTURE;
    }

    @Override
    public void render(SculkRatEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
