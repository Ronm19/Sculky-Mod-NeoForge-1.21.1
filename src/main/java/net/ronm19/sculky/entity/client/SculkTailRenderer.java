package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.SculkSpiderEntity;
import net.ronm19.sculky.entity.custom.SculkTailEntity;
import net.ronm19.sculky.entity.layer.ModModelLayers;

public class SculkTailRenderer extends MobRenderer<SculkTailEntity, SculkTailModel<SculkTailEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculk_tail/sculk_tail.png");

    public SculkTailRenderer( EntityRendererProvider.Context context) {
        super(context, new SculkTailModel<>(context.bakeLayer(ModModelLayers.SCULK_TAIL)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation( SculkTailEntity sculkSpiderEntity ) {
        return TEXTURE;
    }

    @Override
    public void render( SculkTailEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight ) {
        if(entity.isBaby()) {
            poseStack.scale(0.45f, 0.45f, 0.45f);
        }
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
