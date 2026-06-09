package net.ronm19.sculky.entity.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.SculkmiteEntity;
import org.jetbrains.annotations.NotNull;


public class CrownedSculkmiteRenderer extends SculkmiteRenderer {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            SculkyMod.MOD_ID, "textures/entity/crowned_sculkmite/crowned_sculkmite.png"
    );

    public CrownedSculkmiteRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SculkmiteEntity entity) {
        return TEXTURE;
    }
}