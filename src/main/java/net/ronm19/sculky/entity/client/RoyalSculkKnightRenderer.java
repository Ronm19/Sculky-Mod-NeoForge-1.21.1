package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.RoyalSculkKnightEntity;
import net.ronm19.sculky.entity.layer.ModModelLayers;

public class RoyalSculkKnightRenderer extends MobRenderer<RoyalSculkKnightEntity, RoyalSculkKnightModel<RoyalSculkKnightEntity>> {
    private static final ResourceLocation NORMAL_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/royal_sculk_knight/royal_sculk_knight.png");

    private static final ResourceLocation ENRAGED_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/royal_sculk_knight/royal_sculk_knight_enraged.png");

    public RoyalSculkKnightRenderer( EntityRendererProvider.Context context) {
        super(context, new RoyalSculkKnightModel<>(context.bakeLayer(ModModelLayers.ROYAL_SCULK_KNIGHT)), 0.7F);
    }

    @Override
    public ResourceLocation getTextureLocation( RoyalSculkKnightEntity entity) {
        return entity.isEnraged() ? ENRAGED_TEXTURE : NORMAL_TEXTURE;
    }

    @Override
    public void render( RoyalSculkKnightEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight ) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    protected void scale( RoyalSculkKnightEntity livingEntity, PoseStack poseStack, float partialTickTime ) {
        float scale = 1.5F;
        poseStack.scale(scale, scale, scale);
        super.scale(livingEntity, poseStack, partialTickTime);
    }
}
