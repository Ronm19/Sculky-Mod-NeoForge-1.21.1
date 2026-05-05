package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.SculkHeraldEntity;
import net.ronm19.sculky.entity.layer.ModModelLayers;
import net.ronm19.sculky.entity.layer.custom.SculkHeraldEyesLayer;
import org.jetbrains.annotations.NotNull;

public class SculkHeraldRenderer extends MobRenderer<SculkHeraldEntity, SculkHeraldModel<SculkHeraldEntity>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculk_herald/sculk_herald.png");

    public SculkHeraldRenderer(EntityRendererProvider.Context context) {
        super(context, new SculkHeraldModel<>(context.bakeLayer(ModModelLayers.SCULK_HERALD)), 0.55F);

        this.addLayer(new SculkHeraldEyesLayer(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation( @NotNull SculkHeraldEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void scale( @NotNull SculkHeraldEntity entity, PoseStack poseStack, float partialTickTime) {
        poseStack.scale(1.0F, 1.0F, 1.0F);
        super.scale(entity, poseStack, partialTickTime);
    }
}