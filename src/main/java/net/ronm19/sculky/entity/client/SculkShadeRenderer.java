package net.ronm19.sculky.entity.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.SculkShadeEntity;
import net.ronm19.sculky.entity.layer.ModModelLayers;
import org.jetbrains.annotations.NotNull;

public class SculkShadeRenderer extends MobRenderer<SculkShadeEntity, SculkShadeModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculk_shade/sculk_shade.png");


    public SculkShadeRenderer( EntityRendererProvider.Context context) {
        super(context, new SculkShadeModel(context.bakeLayer(ModModelLayers.SCULK_SHADE)), 0.3f);
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation( @NotNull SculkShadeEntity sculkShadeEntity ) {
        return TEXTURE;
    }

    protected int getBlockLightLevel( @NotNull SculkShadeEntity entity, @NotNull BlockPos pos) {
        return 15;
    }


}
