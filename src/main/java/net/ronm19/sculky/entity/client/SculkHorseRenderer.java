package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.SculkHorseEntity;
import net.ronm19.sculky.entity.layer.custom.SculkHorseArmorLayer;
import org.jetbrains.annotations.NotNull;

public class SculkHorseRenderer extends MobRenderer<SculkHorseEntity, HorseModel<SculkHorseEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculk_horse/sculk_horse.png");


    public SculkHorseRenderer( EntityRendererProvider.Context context) {
        super(context, new HorseModel<>(context.bakeLayer(ModelLayers.HORSE)), 1.1F);
        this.addLayer(new SculkHorseArmorLayer(this, context.getModelSet()));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation( @NotNull SculkHorseEntity sculkHorseEntity ) {
        return TEXTURE;
    }

    @Override
    protected void scale(SculkHorseEntity entity, PoseStack poseStack, float partialTickTime) {
        float scale = entity.isBaby() ? 0.6F : 1.15F;
        poseStack.scale(scale, scale, scale);
    }
}
