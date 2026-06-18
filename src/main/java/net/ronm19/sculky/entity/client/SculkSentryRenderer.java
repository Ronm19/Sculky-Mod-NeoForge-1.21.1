package net.ronm19.sculky.entity.client;

import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.IllagerRenderer;
import net.minecraft.client.renderer.entity.VindicatorRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Vindicator;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.SculkSentryEntity;
import org.jetbrains.annotations.NotNull;

public class SculkSentryRenderer extends VindicatorRenderer {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculk_sentry/sculk_sentry.png");

    public SculkSentryRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull Vindicator entity) {
        return TEXTURE;
    }
}