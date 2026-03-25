package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SlimeOuterLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.SculkSlimeEntity;
import net.ronm19.sculky.entity.layer.custom.SculkSlimeOuterLayer;
import org.jetbrains.annotations.NotNull;

public class SculkSlimeRenderer extends MobRenderer<SculkSlimeEntity, SlimeModel<SculkSlimeEntity>> {

    private static final ResourceLocation SCULK_SLIME_LOCATION =
            ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID,
                    "textures/entity/sculk_slime/sculk_slime.png");

    public SculkSlimeRenderer(EntityRendererProvider.Context context) {
        super(context, new SlimeModel<>(context.bakeLayer(ModelLayers.SLIME)), 0.25F);
        this.addLayer(new SculkSlimeOuterLayer(this, context.getModelSet()));
    }

    @Override
    public void render(SculkSlimeEntity entity, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer,
                       int packedLight) {

        this.shadowRadius = 0.25F * entity.getSize();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    protected void scale(SculkSlimeEntity entity, PoseStack poseStack, float partialTickTime) {
        poseStack.scale(0.999F, 0.999F, 0.999F);
        poseStack.translate(0.0F, 0.001F, 0.0F);

        float size = entity.getSize();
        float squish = Mth.lerp(partialTickTime, entity.oSquish, entity.squish)
                / (size * 0.5F + 1.0F);

        float inverse = 1.0F / (squish + 1.0F);

        poseStack.scale(inverse * size, 1.0F / inverse * size, inverse * size);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SculkSlimeEntity entity) {
        return SCULK_SLIME_LOCATION;
    }
}