package net.ronm19.sculky.entity.client;

import net.minecraft.Util;
import net.minecraft.client.model.EndermiteModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.SculkmiteEntity;
import net.ronm19.sculky.entity.variant.SculkmiteVariant;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class SculkmiteRenderer
        extends MobRenderer<SculkmiteEntity, EndermiteModel<SculkmiteEntity>> {

    private static final Map<SculkmiteVariant, ResourceLocation> TEXTURE_BY_VARIANT =
            Util.make(new EnumMap<>(SculkmiteVariant.class), map -> {
                map.put(SculkmiteVariant.DEFAULT, ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculkmite/sculkmite.png"));

                map.put(SculkmiteVariant.KING, ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculkmite/sculkmite_king.png"));
            });

    public SculkmiteRenderer(EntityRendererProvider.Context context) {
        super(context, new EndermiteModel<>(context.bakeLayer(ModelLayers.ENDERMITE)), 0.3F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SculkmiteEntity entity) {
        return TEXTURE_BY_VARIANT.getOrDefault(entity.getVariant(), TEXTURE_BY_VARIANT.get(SculkmiteVariant.DEFAULT));
    }

    protected float getFlipDegrees(@NotNull SculkmiteEntity livingEntity) {
        return 180.0F;
    }
}
