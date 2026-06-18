package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.VindicatorRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Vindicator;
import net.ronm19.sculky.SculkyMod;
import org.jetbrains.annotations.NotNull;

public class SculkVindicatorRenderer extends VindicatorRenderer {
    public SculkVindicatorRenderer( EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation( @NotNull Vindicator entity ) {
        return ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID,"textures/entity/sculk_vindicator/sculk_vindicator.png");
    }

    @Override
    public void render( Vindicator entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight ) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
