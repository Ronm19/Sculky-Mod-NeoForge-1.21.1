package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.SculkExecutionerEntity;
import net.ronm19.sculky.entity.custom.SculkKingEntity;
import net.ronm19.sculky.entity.layer.ModModelLayers;
import org.jetbrains.annotations.NotNull;

public class SculkExecutionerRenderer extends MobRenderer<SculkExecutionerEntity, SculkExecutionerModel<SculkExecutionerEntity>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculk_executioner/sculk_executioner.png");

    public SculkExecutionerRenderer(EntityRendererProvider.Context context) {
        super(context, new SculkExecutionerModel<>(context.bakeLayer(ModModelLayers.SCULK_EXECUTIONER)), 0.85F);
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation( @NotNull SculkExecutionerEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render( @NotNull SculkExecutionerEntity entity, float entityYaw, float partialTicks,
                        @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    protected void scale(@NotNull SculkExecutionerEntity entity, PoseStack poseStack, float partialTickTime) {
        poseStack.scale(1.5F, 1.5F, 1.5F);
        super.scale(entity, poseStack, partialTickTime);
    }
}