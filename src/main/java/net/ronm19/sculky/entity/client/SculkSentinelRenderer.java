package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.IronGolem;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.SculkSentinelEntity;
import net.ronm19.sculky.entity.layer.ModModelLayers;

public class SculkSentinelRenderer extends MobRenderer<SculkSentinelEntity, SculkSentinelModel<SculkSentinelEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculk_sentinel/sculk_sentinel.png");


    public SculkSentinelRenderer( EntityRendererProvider.Context context ) {
        super(context, new SculkSentinelModel<>(context.bakeLayer(ModModelLayers.SCULK_SENTINEL)), 0.7F);
    }

    @Override
    public ResourceLocation getTextureLocation( SculkSentinelEntity sculkSentinelEntity ) {
        return TEXTURE;
    }

    protected void setupRotations( SculkSentinelEntity entity, PoseStack poseStack, float bob, float yBodyRot, float partialTick, float scale ) {
        super.setupRotations(entity, poseStack, bob, yBodyRot, partialTick, scale);
        if (!((double) entity.walkAnimation.speed() < 0.01)) {
            float f = 13.0F;
            float f1 = entity.walkAnimation.position(partialTick) + 6.0F;
            float f2 = (Math.abs(f1 % 13.0F - 6.5F) - 3.25F) / 3.25F;
            poseStack.mulPose(Axis.ZP.rotationDegrees(6.5F * f2));
        }
    }
}
