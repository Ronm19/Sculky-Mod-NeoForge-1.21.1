package net.ronm19.sculky.entity.client;

import net.minecraft.Util;
import net.minecraft.client.model.SilverfishModel;
import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SpiderEyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.SculkParasiteEntity;
import net.ronm19.sculky.entity.custom.SculkStalkerEntity;
import net.ronm19.sculky.entity.custom.SculkZombieEntity;
import net.ronm19.sculky.entity.variant.CorruptedSculkStalkerVariant;
import net.ronm19.sculky.entity.variant.CorruptedSculkZombieVariant;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

public class SculkStalkerRenderer extends MobRenderer<SculkStalkerEntity, SpiderModel<SculkStalkerEntity>> {
    private static final Map<CorruptedSculkStalkerVariant, ResourceLocation> TEXTURE_BY_VARIANT =
            Util.make(new EnumMap<>(CorruptedSculkStalkerVariant.class), map -> {
                map.put(
                        CorruptedSculkStalkerVariant.NORMAL,
                        ResourceLocation.fromNamespaceAndPath(
                                SculkyMod.MOD_ID,
                                "textures/entity/sculk_stalker/sculk_stalker.png"
                        )
                );
                map.put(
                        CorruptedSculkStalkerVariant.CORRUPTED,
                        ResourceLocation.fromNamespaceAndPath(
                                SculkyMod.MOD_ID,
                                "textures/entity/sculk_stalker/sculk_stalker_corrupted.png"
                        )
                );
            });

    public SculkStalkerRenderer( EntityRendererProvider.Context context) {
        super(context, new SpiderModel<>(context.bakeLayer(ModelLayers.SPIDER)), 0.6F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(
            @NotNull SculkStalkerEntity entity ) {
        CorruptedSculkStalkerVariant variant = entity.getVariant();
        return TEXTURE_BY_VARIANT.getOrDefault(entity.getVariant(), TEXTURE_BY_VARIANT.get(CorruptedSculkStalkerVariant.NORMAL));

    }



    protected float getFlipDegrees(@NotNull SculkStalkerEntity livingEntity) {
        return 180.0F;
    }
}
