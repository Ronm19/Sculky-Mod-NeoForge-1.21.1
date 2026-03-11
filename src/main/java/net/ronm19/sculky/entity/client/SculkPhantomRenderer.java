package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.PhantomModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.PhantomRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Phantom;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.SculkPhantomEntity;
import net.ronm19.sculky.entity.layer.custom.SculkPhantomEyesLayer;

public class SculkPhantomRenderer extends MobRenderer<SculkPhantomEntity, PhantomModel<SculkPhantomEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculk_phantom/sculk_phantom.png");


    public SculkPhantomRenderer(EntityRendererProvider.Context context) {
        super(context, new PhantomModel<>(context.bakeLayer(ModelLayers.PHANTOM)), 0.6F);
        this.addLayer(new SculkPhantomEyesLayer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(SculkPhantomEntity sculkPhantomEntity) {
        return TEXTURE;
    }

    @Override
    public void render(SculkPhantomEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    protected void scale(SculkPhantomEntity livingEntity, PoseStack poseStack, float partialTickTime) {
        int i = livingEntity.getPhantomSize();
        float f = 1.0F + 0.15F * (float)i;
        poseStack.scale(f, f, f);
        poseStack.translate(0.0F, 1.3125F, 0.1875F);
    }

    protected void setupRotations(SculkPhantomEntity entity, PoseStack poseStack, float bob, float yBodyRot, float partialTick, float scale) {
        super.setupRotations(entity, poseStack, bob, yBodyRot, partialTick, scale);
        poseStack.mulPose(Axis.XP.rotationDegrees(entity.getXRot()));
    }
}
