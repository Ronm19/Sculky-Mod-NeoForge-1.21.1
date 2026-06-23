package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.SculkBearEntity;
import net.ronm19.sculky.entity.custom.SculkOracleEntity;
import net.ronm19.sculky.entity.custom.ShadowPantherEntity;
import net.ronm19.sculky.entity.layer.ModModelLayers;
import org.jetbrains.annotations.NotNull;

public class SculkBearRenderer extends MobRenderer<SculkBearEntity, SculkBearModel<SculkBearEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculk_bear/sculk_bear.png");

    public SculkBearRenderer( EntityRendererProvider.Context context) {
        super(context, new SculkBearModel<>(context.bakeLayer(ModModelLayers.SCULK_BEAR)), 0.5F);
    }

    @Override
    public void render( @NotNull SculkBearEntity entity, float entityYaw, float partialTicks,
                        PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        if (entity.isBaby()) {
            poseStack.scale(0.45f, 0.45f, 0.45f);
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation( @NotNull SculkBearEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void scale( @NotNull SculkBearEntity livingEntity, PoseStack poseStack, float partialTickTime) {
        poseStack.scale(1.2F, 1.2F, 1.2F);
        super.scale(livingEntity, poseStack, partialTickTime);
    }
}
