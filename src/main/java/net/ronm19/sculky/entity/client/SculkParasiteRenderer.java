package net.ronm19.sculky.entity.client;

import net.minecraft.client.model.SilverfishModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Silverfish;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.SculkParasiteEntity;
import org.jetbrains.annotations.NotNull;

public class SculkParasiteRenderer extends MobRenderer<SculkParasiteEntity, SilverfishModel<SculkParasiteEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculk_parasite/sculk_parasite.png");

    public SculkParasiteRenderer( EntityRendererProvider.Context context) {
        super(context, new SilverfishModel<>(context.bakeLayer(ModelLayers.SILVERFISH)), 0.3F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation( @NotNull SculkParasiteEntity sculkParasiteEntity ) {
        return TEXTURE;
    }

    protected float getFlipDegrees(@NotNull SculkParasiteEntity livingEntity) {
        return 180.0F;
    }
}
