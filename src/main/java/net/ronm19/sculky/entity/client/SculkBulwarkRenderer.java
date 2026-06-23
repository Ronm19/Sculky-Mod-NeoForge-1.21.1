package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.SculkBulwarkEntity;
import net.ronm19.sculky.entity.layer.ModModelLayers;
import org.jetbrains.annotations.NotNull;

public class SculkBulwarkRenderer extends MobRenderer<SculkBulwarkEntity, SculkBulwarkModel<SculkBulwarkEntity>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculk_bulwark/sculk_bulwark.png");

    public SculkBulwarkRenderer( EntityRendererProvider.Context context) {
        super(context, new SculkBulwarkModel<>(context.bakeLayer(ModModelLayers.SCULK_BULWARK)), 0.85F);
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation( @NotNull SculkBulwarkEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render( @NotNull SculkBulwarkEntity entity, float entityYaw, float partialTicks,
                        @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    protected void scale(@NotNull SculkBulwarkEntity entity, PoseStack poseStack, float partialTickTime) {
        poseStack.scale(1.2F, 1.2F, 1.2F);
        super.scale(entity, poseStack, partialTickTime);
    }
}
