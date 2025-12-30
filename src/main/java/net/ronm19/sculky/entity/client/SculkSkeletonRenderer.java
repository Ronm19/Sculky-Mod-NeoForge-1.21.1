package net.ronm19.sculky.entity.client;

import net.minecraft.Util;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.resources.ResourceLocation;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.SculkSkeletonEntity;
import net.ronm19.sculky.entity.variant.CorruptedSculkSkeletonVariant;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

public class SculkSkeletonRenderer extends SkeletonRenderer<SculkSkeletonEntity> {

    private static final Map<CorruptedSculkSkeletonVariant, ResourceLocation> TEXTURE_BY_VARIANT =
            Util.make(new EnumMap<>(CorruptedSculkSkeletonVariant.class), map -> {
                map.put(
                        CorruptedSculkSkeletonVariant.NORMAL,
                        ResourceLocation.fromNamespaceAndPath(
                                SculkyMod.MOD_ID,
                                "textures/entity/sculk_skeleton/sculk_skeleton.png"
                        )
                );
                map.put(
                        CorruptedSculkSkeletonVariant.CORRUPTED,
                        ResourceLocation.fromNamespaceAndPath(
                                SculkyMod.MOD_ID,
                                "textures/entity/sculk_skeleton/sculk_skeleton_corrupted.png"
                        )
                );
            });

    public SculkSkeletonRenderer(EntityRendererProvider.Context context) {
        this(
                context,
                ModelLayers.WITHER_SKELETON,
                ModelLayers.WITHER_SKELETON_INNER_ARMOR,
                ModelLayers.WITHER_SKELETON_OUTER_ARMOR
        );
    }

    public SculkSkeletonRenderer(
            EntityRendererProvider.Context context,
            ModelLayerLocation skeletonLayer,
            ModelLayerLocation innerArmor,
            ModelLayerLocation outerArmor) {
        super(context);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SculkSkeletonEntity entity) {
        return TEXTURE_BY_VARIANT.getOrDefault(
                entity.getVariant(),
                TEXTURE_BY_VARIANT.get(CorruptedSculkSkeletonVariant.NORMAL)
        );
    }
}
