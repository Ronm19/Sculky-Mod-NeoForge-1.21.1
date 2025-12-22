package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.resources.ResourceLocation;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.SculkSkeletonEntity;
import org.jetbrains.annotations.NotNull;

public class SculkSkeletonRenderer extends SkeletonRenderer<SculkSkeletonEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculk_skeleton/sculk_skeleton.png");

    public SculkSkeletonRenderer(EntityRendererProvider.Context context) {
        super(
                context,
                ModelLayers.WITHER_SKELETON,
                ModelLayers.WITHER_SKELETON_INNER_ARMOR,
                ModelLayers.WITHER_SKELETON_OUTER_ARMOR);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SculkSkeletonEntity entity) {
        return TEXTURE;
    }
}
