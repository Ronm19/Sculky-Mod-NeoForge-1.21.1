package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.SculkCreeperEntity;
import net.ronm19.sculky.entity.layer.custom.SculkCreeperPowerLayer;
import net.ronm19.sculky.entity.variant.CorruptedSculkCreeperVariant;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

public class SculkCreeperRenderer extends MobRenderer<SculkCreeperEntity, CreeperModel<SculkCreeperEntity>> {

    private static final Map<CorruptedSculkCreeperVariant, ResourceLocation> TEXTURE_BY_VARIANT =
            Util.make(new EnumMap<>(CorruptedSculkCreeperVariant.class), map -> {
                map.put(CorruptedSculkCreeperVariant.NORMAL, ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculk_creeper/sculk_creeper.png"));

                map.put(CorruptedSculkCreeperVariant.CORRUPTED, ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculk_creeper/sculk_creeper_corrupted.png"));
            });

    public SculkCreeperRenderer(EntityRendererProvider.Context context) {
        super(context, new CreeperModel<>(context.bakeLayer(ModelLayers.CREEPER)), 0.5F);
        this.addLayer(new SculkCreeperPowerLayer(this, context.getModelSet()));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SculkCreeperEntity entity) {
        return TEXTURE_BY_VARIANT.getOrDefault(entity.getVariant(), TEXTURE_BY_VARIANT.get(CorruptedSculkCreeperVariant.NORMAL));
    }

    @Override
    protected void scale(SculkCreeperEntity entity, PoseStack poseStack, float partialTickTime) {
        float f = entity.getSwelling(partialTickTime);
        float f1 = 1.0F + Mth.sin(f * 100.0F) * f * 0.01F;

        f = Mth.clamp(f, 0.0F, 1.0F);
        f *= f;
        f *= f;

        float scaleXZ = (1.0F + f * 0.4F) * f1;
        float scaleY  = (1.0F + f * 0.1F) / f1;

        poseStack.scale(scaleXZ, scaleY, scaleXZ);
    }

    @Override
    protected float getWhiteOverlayProgress(SculkCreeperEntity entity, float partialTicks) {
        float f = entity.getSwelling(partialTicks);
        return (int)(f * 10.0F) % 2 == 0
                ? 0.0F
                : Mth.clamp(f, 0.5F, 1.0F);
    }
}

