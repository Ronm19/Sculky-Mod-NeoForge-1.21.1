package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.SculkBruteEntity;
import net.ronm19.sculky.entity.layer.ModModelLayers;

public class SculkBruteRenderer extends MobRenderer<SculkBruteEntity, SculkBruteModel<SculkBruteEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculk_brute/sculk_brute.png");

    public SculkBruteRenderer( EntityRendererProvider.Context context) {
        super(context, new SculkBruteModel<>(context.bakeLayer(ModModelLayers.SCULK_BRUTE)), 0.6F);
    }

    @Override
    public ResourceLocation getTextureLocation( SculkBruteEntity sculkBruteEntity ) {
        return TEXTURE;
    }

    @Override
    public void render( SculkBruteEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight ) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    protected void scale( SculkBruteEntity livingEntity, PoseStack poseStack, float partialTickTime ) {
        float scale = 1.0F;
        poseStack.scale(scale, scale, scale);
        super.scale(livingEntity, poseStack, partialTickTime);
    }
}
