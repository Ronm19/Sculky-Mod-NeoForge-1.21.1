package net.ronm19.sculky.entity.client;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.SculkSpiritEntity;
import org.jetbrains.annotations.NotNull;

public class SculkSpiritRenderer extends MobRenderer<SculkSpiritEntity, SculkSpiritModel> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculk_spirit/sculk_spirit.png");

    private static final ResourceLocation CHARGING_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculk_spirit/sculk_spirit_charging.png");

    public SculkSpiritRenderer(EntityRendererProvider.Context context) {
        super(context, new SculkSpiritModel(context.bakeLayer(ModelLayers.VEX)), 0.3F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SculkSpiritEntity entity) {
        return entity.isCharging() ? CHARGING_TEXTURE : TEXTURE;
    }

    @Override
    protected int getBlockLightLevel(@NotNull SculkSpiritEntity entity, @NotNull BlockPos pos) {
        return 15;
    }
}