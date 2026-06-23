package net.ronm19.sculky.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ShieldModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.ronm19.sculky.SculkyMod;
import org.jetbrains.annotations.NotNull;

public class SculkShieldRenderer extends BlockEntityWithoutLevelRenderer {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/shield/sculk_shield.png");

    private ShieldModel shieldModel;

    public SculkShieldRenderer() {
        this(
                Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels()
        );
    }

    public SculkShieldRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher, EntityModelSet entityModelSet) {
        super(blockEntityRenderDispatcher, entityModelSet);
    }

    private ShieldModel getShieldModel() {
        if (this.shieldModel == null) {
            this.shieldModel = new ShieldModel(
                    Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.SHIELD)
            );
        }

        return this.shieldModel;
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack,
                             @NotNull ItemDisplayContext displayContext,
                             @NotNull PoseStack poseStack,
                             @NotNull MultiBufferSource buffer,
                             int packedLight,
                             int packedOverlay) {
        ShieldModel model = this.getShieldModel();

        poseStack.pushPose();

        poseStack.scale(1.0F, -1.0F, -1.0F);

        VertexConsumer vertexConsumer = ItemRenderer.getFoilBuffer(
                buffer,
                model.renderType(TEXTURE),
                true,
                stack.hasFoil()
        );

        model.handle().render(poseStack, vertexConsumer, packedLight, packedOverlay);
        model.plate().render(poseStack, vertexConsumer, packedLight, packedOverlay);

        poseStack.popPose();
    }
}