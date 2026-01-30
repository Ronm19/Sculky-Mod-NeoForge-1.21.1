package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.ronm19.sculky.entity.animation.SculkSandsnareAnimations;
import net.ronm19.sculky.entity.animation.SculkWolfAlphaAnimations;
import net.ronm19.sculky.entity.custom.SculkSandsnareEntity;
import org.jetbrains.annotations.NotNull;

public class SculkSandsnareModel <T extends SculkSandsnareEntity> extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart tail;

    public SculkSandsnareModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.tail = root.getChild("tail");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(50, 44).addBox(-5.7714F, 2.9F, -0.9F, 11.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(50, 42).addBox(-5.7714F, -4.1F, -0.9F, 11.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.9F, 19.7F, -23.3F, 0.0F, 3.1416F, 0.0F));

        PartDefinition head_r1 = head.addOrReplaceChild("head_r1", CubeListBuilder.create().texOffs(50, 46).addBox(-3.0F, -0.5F, -0.5F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.7286F, -0.1F, -0.4F, 0.0F, 0.0F, -1.5708F));

        PartDefinition head_r2 = head.addOrReplaceChild("head_r2", CubeListBuilder.create().texOffs(52, 20).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.7286F, 2.4F, -0.4F, 0.0F, 0.0F, -1.5708F));

        PartDefinition head_r3 = head.addOrReplaceChild("head_r3", CubeListBuilder.create().texOffs(56, 22).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0286F, 2.4F, -0.4F, 0.0F, 0.0F, -1.5708F));

        PartDefinition head_r4 = head.addOrReplaceChild("head_r4", CubeListBuilder.create().texOffs(64, 20).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.4286F, 2.4F, -0.4F, 0.0F, 0.0F, -1.5708F));

        PartDefinition head_r5 = head.addOrReplaceChild("head_r5", CubeListBuilder.create().texOffs(56, 20).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5714F, 2.4F, -0.4F, 0.0F, 0.0F, -1.5708F));

        PartDefinition head_r6 = head.addOrReplaceChild("head_r6", CubeListBuilder.create().texOffs(64, 46).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.2714F, 2.4F, -0.4F, 0.0F, 0.0F, -1.5708F));

        PartDefinition head_r7 = head.addOrReplaceChild("head_r7", CubeListBuilder.create().texOffs(68, 12).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.1714F, -2.6F, -0.4F, 0.0F, 0.0F, -1.5708F));

        PartDefinition head_r8 = head.addOrReplaceChild("head_r8", CubeListBuilder.create().texOffs(52, 22).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5714F, -2.6F, -0.4F, 0.0F, 0.0F, -1.5708F));

        PartDefinition head_r9 = head.addOrReplaceChild("head_r9", CubeListBuilder.create().texOffs(64, 22).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.1714F, -2.6F, -0.4F, 0.0F, 0.0F, -1.5708F));

        PartDefinition head_r10 = head.addOrReplaceChild("head_r10", CubeListBuilder.create().texOffs(60, 22).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.3286F, -2.6F, -0.4F, 0.0F, 0.0F, -1.5708F));

        PartDefinition head_r11 = head.addOrReplaceChild("head_r11", CubeListBuilder.create().texOffs(60, 20).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.7286F, -2.6F, -0.4F, 0.0F, 0.0F, -1.5708F));

        PartDefinition head_r12 = head.addOrReplaceChild("head_r12", CubeListBuilder.create().texOffs(52, 0).addBox(-3.0F, -0.5F, -0.5F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.2714F, -0.1F, -0.4F, 0.0F, 0.0F, -1.5708F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.3212F, -8.9F, -22.4552F, 11.0F, 9.0F, 15.0F, new CubeDeformation(0.0F))
                .texOffs(68, 0).addBox(0.1788F, -11.2F, -2.5552F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(68, 6).addBox(0.2788F, -11.2F, 3.8448F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 24).addBox(-3.3212F, -7.9F, -7.5552F, 9.0F, 8.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.1F, 0.0F));

        PartDefinition tail = partdefinition.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 48).addBox(-1.2F, -9.9F, 8.6F, 5.0F, 8.0F, 15.0F, new CubeDeformation(0.0F))
                .texOffs(40, 48).addBox(-1.2F, -8.9F, 23.5F, 5.0F, 5.0F, 15.0F, new CubeDeformation(0.0F))
                .texOffs(50, 24).addBox(-0.2F, -8.9F, 38.7F, 3.0F, 3.0F, 15.0F, new CubeDeformation(0.0F))
                .texOffs(52, 14).addBox(0.1F, -13.2F, 12.1F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(52, 8).addBox(0.2F, -13.2F, 19.1F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(52, 2).addBox(0.4F, -12.4F, 26.1F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(60, 2).addBox(0.3F, -12.4F, 32.1F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(60, 8).addBox(0.2F, -12.4F, 41.1F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(60, 14).addBox(0.4F, -12.4F, 49.1F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 26.1F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(@NotNull SculkSandsnareEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

        // Reset pose every frame (CRITICAL)
        this.root().getAllParts().forEach(ModelPart::resetPose);

        // Time base
        float time = ageInTicks * 0.05F;

    /* =========================
       BODY – breathing / pulse
       ========================= */

        body.y += Mth.sin(time * 0.8F) * 0.25F;
        body.xRot += Mth.sin(time * 0.6F) * 0.03F;

    /* =========================
       TAIL – delayed wave motion
       ========================= */

        tail.yRot += Mth.sin(time * 0.6F) * 0.12F;
        tail.xRot += Mth.cos(time * 0.5F) * 0.08F;

        // slight vertical lag for weight
        tail.y += Mth.sin(time * 0.5F + 1.2F) * 0.3F;
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        tail.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public @NotNull ModelPart root() {
        return this.root;
    }
}
