package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.SalvatoreEntity;
import net.ronm19.sculky.entity.custom.SanctumWatcherEntity;
import net.ronm19.sculky.entity.layer.ModModelLayers;
import org.jetbrains.annotations.NotNull;

public class SanctumWatcherRenderer extends MobRenderer<SanctumWatcherEntity, SanctumWatcherModel<SanctumWatcherEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sanctum_watcher/sanctum_watcher.png");


    public SanctumWatcherRenderer( EntityRendererProvider.Context context) {
        super(context, new SanctumWatcherModel<>(context.bakeLayer(ModModelLayers.SANCTUM_WATCHER)), 0.9F);
    }

    @Override
    public ResourceLocation getTextureLocation( SanctumWatcherEntity watcher ) {
        return TEXTURE;
    }

    @Override
    public void render( SanctumWatcherEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight ) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    protected void scale( @NotNull SanctumWatcherEntity livingEntity, PoseStack poseStack, float partialTickTime) {
        float scale = 1.0F;
        poseStack.scale(scale, scale, scale);
        super.scale(livingEntity, poseStack, partialTickTime);
    }
}
