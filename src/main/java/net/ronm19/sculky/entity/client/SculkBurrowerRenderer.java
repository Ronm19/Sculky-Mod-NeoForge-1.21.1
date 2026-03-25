package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.SculkBurrowerEntity;
import net.ronm19.sculky.entity.layer.ModModelLayers;

public class SculkBurrowerRenderer extends MobRenderer<SculkBurrowerEntity, SculkBurrowerModel<SculkBurrowerEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculk_burrower/sculk_burrower.png");


    public SculkBurrowerRenderer( EntityRendererProvider.Context context) {
        super(context, new SculkBurrowerModel<>(context.bakeLayer(ModModelLayers.SCULK_BURROWER)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation( SculkBurrowerEntity sculkBurrowerEntity ) {
        return TEXTURE;
    }

    @Override
    public void render( SculkBurrowerEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight ) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
