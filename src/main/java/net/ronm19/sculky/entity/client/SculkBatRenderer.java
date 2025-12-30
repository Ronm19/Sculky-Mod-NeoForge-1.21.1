package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.model.BatModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.SculkBatEntity;
import net.ronm19.sculky.entity.layer.ModModelLayers;
import net.ronm19.sculky.entity.variant.CorruptedSculkBatVariant;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class SculkBatRenderer
        extends MobRenderer<SculkBatEntity, SculkBatModel<SculkBatEntity>> {

    private static final Map<CorruptedSculkBatVariant, ResourceLocation> TEXTURE_BY_VARIANT =
            Util.make(new EnumMap<>(CorruptedSculkBatVariant.class), map -> {
                map.put(
                        CorruptedSculkBatVariant.NORMAL,
                        ResourceLocation.fromNamespaceAndPath(
                                SculkyMod.MOD_ID,
                                "textures/entity/sculk_bat/sculk_bat.png"
                        )
                );
                map.put(
                        CorruptedSculkBatVariant.CORRUPTED,
                        ResourceLocation.fromNamespaceAndPath(
                                SculkyMod.MOD_ID,
                                "textures/entity/sculk_bat/sculk_bat_corrupted.png"
                        )
                );
            });

    public SculkBatRenderer(EntityRendererProvider.Context context) {
        super(
                context,
                new SculkBatModel<>(context.bakeLayer(ModModelLayers.SCULK_BAT)),
                0.25F
        );
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(
            @NotNull SculkBatEntity entity
    ) {
        return TEXTURE_BY_VARIANT.getOrDefault(
                entity.getVariant(),
                TEXTURE_BY_VARIANT.get(CorruptedSculkBatVariant.NORMAL)
        );
    }
}
