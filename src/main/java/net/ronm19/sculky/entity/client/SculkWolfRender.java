package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.WolfArmorLayer;
import net.minecraft.client.renderer.entity.layers.WolfCollarLayer;
import net.minecraft.resources.ResourceLocation;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.SculkWolfEntity;
import net.ronm19.sculky.entity.layer.ModModelLayers;
import net.ronm19.sculky.entity.layer.custom.SculkWolfArmorLayer;
import net.ronm19.sculky.entity.layer.custom.SculkWolfCollarLayer;
import org.jetbrains.annotations.NotNull;

public class SculkWolfRender extends MobRenderer<SculkWolfEntity, SculkWolfModel<SculkWolfEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculk_wolf/sculk_wolf.png");

    public SculkWolfRender( EntityRendererProvider.Context context) {
        super(context, new SculkWolfModel<>(context.bakeLayer(ModModelLayers.SCULK_WOLF)), 0.5F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation( @NotNull SculkWolfEntity sculkWolfEntity ) {
        return TEXTURE;
    }

    @Override
    public void render( SculkWolfEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight ) {
        if(entity.isBaby()) {
            poseStack.scale(0.45f, 0.45f, 0.45f);
        }
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
