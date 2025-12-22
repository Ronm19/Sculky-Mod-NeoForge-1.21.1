package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.SculkHorrorEntity;
import net.ronm19.sculky.entity.layer.ModModelLayers;

public class SculkHorrorRenderer extends MobRenderer<SculkHorrorEntity, SculkHorrorModel<SculkHorrorEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculk_horror/sculk_horror.png");


    public SculkHorrorRenderer( EntityRendererProvider.Context context) {
        super(context, new SculkHorrorModel<>(context.bakeLayer(ModModelLayers.SCULK_HORROR)), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation( SculkHorrorEntity sculkHorrorEntity ) {
        return TEXTURE;
    }

    @Override
    public void render( SculkHorrorEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight ) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    protected void scale( SculkHorrorEntity livingEntity, PoseStack poseStack, float partialTickTime) {
        poseStack.scale(1.1F, 1.1F, 1.1F);
    }
}
