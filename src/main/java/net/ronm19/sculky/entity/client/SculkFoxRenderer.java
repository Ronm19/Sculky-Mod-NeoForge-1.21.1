package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.FoxModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.FoxHeldItemLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Fox;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.SculkFoxEntity;
import net.ronm19.sculky.entity.layer.ModModelLayers;
import net.ronm19.sculky.entity.layer.custom.SculkFoxHeldItemLayer;
import org.jetbrains.annotations.NotNull;

public class SculkFoxRenderer extends MobRenderer<SculkFoxEntity, SculkFoxModel<SculkFoxEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculk_fox/sculk_fox.png");

    public SculkFoxRenderer( EntityRendererProvider.Context context) {
        super(context, new SculkFoxModel<>(context.bakeLayer(ModModelLayers.SCULK_FOX)), 0.4F);
        this.addLayer(new SculkFoxHeldItemLayer(this, context.getItemInHandRenderer()));

    }

    @Override
    public @NotNull ResourceLocation getTextureLocation( @NotNull SculkFoxEntity sculkFoxEntity ) {
        return TEXTURE;
    }

    protected void setupRotations( SculkFoxEntity entity, PoseStack poseStack, float bob, float yBodyRot, float partialTick, float scale) {
        super.setupRotations(entity, poseStack, bob, yBodyRot, partialTick, scale);
        if (entity.isPouncing() || entity.isFaceplanted()) {
            float f = -Mth.lerp(partialTick, entity.xRotO, entity.getXRot());
            poseStack.mulPose(Axis.XP.rotationDegrees(f));
        }

    }

    @Override
    public void render( SculkFoxEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight ) {
        if(entity.isBaby()) {
            poseStack.scale(0.45f, 0.45f, 0.45f);
        }
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}