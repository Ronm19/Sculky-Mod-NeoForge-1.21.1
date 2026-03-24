package net.ronm19.sculky.entity.client;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.ShadowPantherEntity;
import net.ronm19.sculky.entity.layer.ModModelLayers;
import net.ronm19.sculky.entity.layer.custom.ShadowPantherEyesLayer;
import org.jetbrains.annotations.NotNull;

public class ShadowPantherRenderer extends MobRenderer<ShadowPantherEntity, ShadowPantherModel<ShadowPantherEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/shadow_panther/shadow_panther.png");


    public ShadowPantherRenderer( EntityRendererProvider.Context context) {
        super(context, new ShadowPantherModel<>(context.bakeLayer(ModModelLayers.SHADOW_PANTHER)), 0.5F);
        this.addLayer(new ShadowPantherEyesLayer<>(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation( @NotNull ShadowPantherEntity pantherEntity ) {
        return TEXTURE;
    }

    @Override
    public void render(@NotNull ShadowPantherEntity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        if (entity.isBaby()) {
            poseStack.scale(0.45f, 0.45f, 0.45f);
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        poseStack.popPose();
    }

    @Override
    protected void scale( @NotNull ShadowPantherEntity livingEntity, PoseStack poseStack, float partialTickTime) {
        float scale = 1.5F;
        poseStack.scale(scale, scale, scale);
        super.scale(livingEntity, poseStack, partialTickTime);
    }
}
