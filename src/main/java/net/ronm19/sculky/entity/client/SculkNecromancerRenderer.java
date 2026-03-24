package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.BoggedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.SkeletonClothingLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Bogged;
import net.ronm19.sculky.SculkyMod;
import org.jetbrains.annotations.NotNull;

public class SculkNecromancerRenderer extends BoggedRenderer {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID,
                    "textures/entity/sculk_necromancer/sculk_necromancer.png");

    private static final ResourceLocation OUTER_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID,
                    "textures/entity/sculk_necromancer/sculk_necromancer_outer.png");

    public SculkNecromancerRenderer(EntityRendererProvider.Context context) {
        super(context);

        // Outer clothing layer (like bogged moss layer)
        this.addLayer(new SkeletonClothingLayer<>(this, context.getModelSet(), ModelLayers.BOGGED_OUTER_LAYER, OUTER_TEXTURE));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation( @NotNull Bogged entity) {
        return TEXTURE;
    }

    @Override
    public void render(Bogged entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight ) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    protected void scale(Bogged livingEntity, PoseStack poseStack, float partialTickTime ) {
        poseStack.scale(1.0F, 1.05F, 1.0F);
        super.scale(livingEntity, poseStack, partialTickTime);
    }
}