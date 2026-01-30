package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.SculkSandsnareEntity;
import net.ronm19.sculky.entity.layer.ModModelLayers;
import org.jetbrains.annotations.NotNull;

public class SculkSandsnareRenderer extends MobRenderer<SculkSandsnareEntity, SculkSandsnareModel<SculkSandsnareEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculk_sandsnare/sculk_sandsnare.png");

    public SculkSandsnareRenderer(EntityRendererProvider.Context context) {
        super(context, new SculkSandsnareModel<>(context.bakeLayer(ModModelLayers.SCULK_SANDSNARE)), 0.5F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SculkSandsnareEntity sculkSandsnareEntity) {
        return TEXTURE;
    }

    @Override
    public void render(@NotNull SculkSandsnareEntity entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}