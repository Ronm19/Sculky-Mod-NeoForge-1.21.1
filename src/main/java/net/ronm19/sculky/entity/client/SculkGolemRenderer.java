package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.SculkFoxEntity;
import net.ronm19.sculky.entity.custom.SculkGolemEntity;
import net.ronm19.sculky.entity.layer.ModModelLayers;
import net.ronm19.sculky.entity.layer.custom.SculkFoxHeldItemLayer;
import org.jetbrains.annotations.NotNull;

public class SculkGolemRenderer extends MobRenderer<SculkGolemEntity, SculkGolemModel<SculkGolemEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculk_golem/sculk_golem.png");

    public SculkGolemRenderer( EntityRendererProvider.Context context) {
        super(context, new SculkGolemModel<>(context.bakeLayer(ModModelLayers.SCULK_GOLEM)), 0.7F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation( @NotNull SculkGolemEntity sculkGolemEntity ) {
        return TEXTURE;

    }

    @Override
    public void render( SculkGolemEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight ) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
