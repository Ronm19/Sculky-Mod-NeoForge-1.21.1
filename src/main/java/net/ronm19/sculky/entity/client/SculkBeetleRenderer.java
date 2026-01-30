package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.SculkBeetleEntity;
import net.ronm19.sculky.entity.layer.ModModelLayers;
import org.jetbrains.annotations.NotNull;

public class SculkBeetleRenderer extends MobRenderer<SculkBeetleEntity, SculkBeetleModel<SculkBeetleEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculk_beetle/sculk_beetle.png");

    public SculkBeetleRenderer( EntityRendererProvider.Context context) {
        super(context, new SculkBeetleModel<>(context.bakeLayer(ModModelLayers.SCULK_BEETLE)), 0.4F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation( @NotNull SculkBeetleEntity sculkBeetleEntity ) {
        return TEXTURE;
    }

    @Override
    public @NotNull Vec3 getRenderOffset(@NotNull SculkBeetleEntity entity, float partialTicks) {
        // 1 pixel = 1/16 = 0.0625
        return new Vec3(0.0D, 0.15D, 0.0D);
    }

    protected float getFlipDegrees(@NotNull SculkBeetleEntity livingEntity) {
        return 180.0F;
    }

    @Override
    public void render( SculkBeetleEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight ) {
        if(entity.isBaby()) {
            poseStack.scale(0.45f, 0.45f, 0.45f);
        }
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
