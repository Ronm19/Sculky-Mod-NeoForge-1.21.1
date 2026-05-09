package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.SculkSnapperEntity;
import net.ronm19.sculky.entity.layer.ModModelLayers;
import org.jetbrains.annotations.NotNull;

public class SculkSnapperRenderer extends MobRenderer<SculkSnapperEntity, SculkSnapperModel<SculkSnapperEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculk_snapper/sculk_snapper.png");

    public SculkSnapperRenderer( EntityRendererProvider.Context context) {
        super(context, new SculkSnapperModel<>(context.bakeLayer(ModModelLayers.SCULK_SNAPPER)), 0.5F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation( @NotNull SculkSnapperEntity sculkSnapperEntity ) {
        return TEXTURE;
    }

    @Override
    public void render( SculkSnapperEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight ) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
