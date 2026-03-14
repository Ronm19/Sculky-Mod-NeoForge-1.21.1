package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.SculkHunterEntity;

import net.ronm19.sculky.entity.layer.ModModelLayers;
import net.ronm19.sculky.entity.layer.custom.SculkHunterEyesLayer;
import org.jetbrains.annotations.NotNull;

public class SculkHunterRenderer extends MobRenderer<SculkHunterEntity, SculkHunterModel<SculkHunterEntity>> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculk_hunter/sculk_hunter.png");

    public SculkHunterRenderer(EntityRendererProvider.Context context) {
        super(context, new SculkHunterModel<>(context.bakeLayer(ModModelLayers.SCULK_HUNTER)), 0.45F);
        this.addLayer(new SculkHunterEyesLayer<>(this));
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SculkHunterEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(@NotNull SculkHunterEntity entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}