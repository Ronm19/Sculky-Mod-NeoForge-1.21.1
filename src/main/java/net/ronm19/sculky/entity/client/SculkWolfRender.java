package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Wolf;
import net.ronm19.sculky.SculkyMod;
import org.jetbrains.annotations.NotNull;

public class SculkWolfRender extends WolfRenderer {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculk_wolf/sculk_wolf.png");

    public SculkWolfRender( EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation( @NotNull Wolf sculkWolfEntity ) {
        return TEXTURE;
    }

    @Override
    public void render( Wolf entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight ) {
        if(entity.isBaby()) {
            poseStack.scale(0.45f, 0.45f, 0.45f);
        }
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
