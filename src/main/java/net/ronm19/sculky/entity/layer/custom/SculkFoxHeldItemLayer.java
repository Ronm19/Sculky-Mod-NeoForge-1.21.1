package net.ronm19.sculky.entity.layer.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.FoxModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.ronm19.sculky.entity.client.SculkFoxModel;
import net.ronm19.sculky.entity.custom.SculkFoxEntity;
import org.jetbrains.annotations.NotNull;

public class SculkFoxHeldItemLayer extends RenderLayer<SculkFoxEntity, SculkFoxModel<SculkFoxEntity>> {
    private final ItemInHandRenderer itemInHandRenderer;

    public SculkFoxHeldItemLayer( RenderLayerParent<SculkFoxEntity, SculkFoxModel<SculkFoxEntity>> renderer, ItemInHandRenderer itemInHandRenderer) {
        super(renderer);
        this.itemInHandRenderer = itemInHandRenderer;
    }

    public void render( PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight, SculkFoxEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        boolean flag = livingEntity.isSleeping();
        boolean flag1 = livingEntity.isBaby();
        poseStack.pushPose();
        if (flag1) {
            float f = 0.75F;
            poseStack.scale(0.75F, 0.75F, 0.75F);
            poseStack.translate(0.0F, 0.5F, 0.209375F);
        }

        poseStack.translate(((SculkFoxModel<?>)this.getParentModel()).head.x / 16.0F, ((SculkFoxModel<?>)this.getParentModel()).head.y / 16.0F, ((SculkFoxModel<?>)this.getParentModel()).head.z / 16.0F);
        float f1 = livingEntity.getHeadRollAngle(partialTicks);
        poseStack.mulPose(Axis.ZP.rotation(f1));
        poseStack.mulPose(Axis.YP.rotationDegrees(netHeadYaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(headPitch));
        if (livingEntity.isBaby()) {
            if (flag) {
                poseStack.translate(0.4F, 0.26F, 0.15F);
            } else {
                poseStack.translate(0.06F, 0.26F, -0.5F);
            }
        } else if (flag) {
            poseStack.translate(0.46F, 0.26F, 0.22F);
        } else {
            poseStack.translate(0.06F, 0.27F, -0.5F);
        }

        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        if (flag) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
        }

        ItemStack itemstack = livingEntity.getItemBySlot(EquipmentSlot.MAINHAND);
        this.itemInHandRenderer.renderItem(livingEntity, itemstack, ItemDisplayContext.GROUND, false, poseStack, buffer, packedLight);
        poseStack.popPose();
    }
}
