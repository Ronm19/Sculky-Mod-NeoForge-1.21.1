package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.projectile.ShadowBoltEntity;

public class ShadowBoltRenderer extends EntityRenderer<ShadowBoltEntity> {

    private static final ResourceLocation DUMMY_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/shadow_bolt/shadow_bolt.png");

    public ShadowBoltRenderer( EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(ShadowBoltEntity entity,
                       float entityYaw,
                       float partialTick,
                       PoseStack poseStack,
                       MultiBufferSource buffer,
                       int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(ShadowBoltEntity entity) {
        return DUMMY_TEXTURE;
    }
}