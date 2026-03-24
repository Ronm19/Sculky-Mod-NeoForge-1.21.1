package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.ronm19.sculky.entity.animation.HollowhornAnimations;
import net.ronm19.sculky.entity.animation.RoyalSculkKnightAnimations;
import net.ronm19.sculky.entity.custom.RoyalSculkKnightEntity;

public class RoyalSculkKnightModel <T extends RoyalSculkKnightEntity> extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart right_arm;
    private final ModelPart left_arm;
    private final ModelPart right_leg;
    private final ModelPart left_leg;

    public RoyalSculkKnightModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.right_arm = root.getChild("right_arm");
        this.left_arm = root.getChild("left_arm");
        this.right_leg = root.getChild("right_leg");
        this.left_leg = root.getChild("left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(33, 16).addBox(-3.0396F, -0.588F, -1.5286F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(52, 70).addBox(-3.0396F, -1.588F, -2.5286F, 2.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(72, 68).addBox(-1.0396F, -0.588F, -2.5286F, 2.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(76, 32).addBox(-1.4396F, -2.388F, -1.3286F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(68, 76).addBox(-0.4396F, -2.388F, -1.3286F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(73, 76).addBox(0.5604F, -2.388F, -1.3286F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(50, 12).addBox(-0.4396F, -3.388F, -1.3286F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(72, 59).addBox(0.9604F, -1.588F, -2.5286F, 2.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0396F, -1.612F, -2.6714F));

        PartDefinition head_r1 = head.addOrReplaceChild("head_r1", CubeListBuilder.create().texOffs(41, 12).addBox(-1.0F, -0.5F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.3345F, -1.7997F, 1.3714F, 0.0F, 0.0F, 1.2915F));

        PartDefinition head_r2 = head.addOrReplaceChild("head_r2", CubeListBuilder.create().texOffs(76, 27).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.8538F, -0.4375F, 1.2714F, -0.0615F, -0.0153F, 1.1483F));

        PartDefinition head_r3 = head.addOrReplaceChild("head_r3", CubeListBuilder.create().texOffs(0, 66).addBox(-1.0F, -0.5F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.5702F, -1.8725F, 1.2714F, 0.0F, 0.0F, -1.0123F));

        PartDefinition head_r4 = head.addOrReplaceChild("head_r4", CubeListBuilder.create().texOffs(59, 73).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.6462F, -0.1375F, 1.2714F, 0.0F, 0.0F, -1.0123F));

        PartDefinition head_r5 = head.addOrReplaceChild("head_r5", CubeListBuilder.create().texOffs(9, 73).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.4462F, 0.5625F, 1.3714F, 0.0F, 0.0F, -0.2443F));

        PartDefinition head_r6 = head.addOrReplaceChild("head_r6", CubeListBuilder.create().texOffs(0, 73).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5572F, 0.5884F, 1.3714F, 0.0F, 0.0F, 0.2618F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 16).addBox(-3.9455F, -0.9636F, -4.4091F, 8.0F, 5.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(40, 48).addBox(-4.3455F, 4.0364F, -4.4091F, 1.0F, 6.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(58, 12).addBox(3.4545F, 4.0364F, -4.4091F, 1.0F, 6.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(19, 70).addBox(-2.5455F, 3.7364F, -4.5091F, 5.0F, 9.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(64, 0).addBox(-3.5455F, 3.9364F, 3.6909F, 7.0F, 10.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-4.9455F, -5.9636F, -5.4091F, 10.0F, 5.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(0, 30).addBox(-5.9455F, -7.9636F, -5.4091F, 1.0F, 6.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(41, 0).addBox(-5.0455F, -6.9636F, -5.4091F, 1.0F, 1.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(17, 47).addBox(3.9545F, -6.9636F, -5.4091F, 1.0F, 1.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(0, 70).addBox(-4.0455F, -6.9636F, 3.5909F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(23, 30).addBox(4.9545F, -7.9636F, -5.4091F, 1.0F, 6.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.0545F, 9.7636F, -0.5909F));

        PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(63, 41).addBox(-2.06F, -5.78F, -2.02F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(46, 29).addBox(-2.56F, -4.78F, -2.02F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(59, 48).addBox(-3.66F, 0.22F, -2.02F, 6.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(13, 59).addBox(-2.66F, 0.22F, 3.08F, 4.0F, 6.0F, -6.0F, new CubeDeformation(0.0F))
                .texOffs(41, 70).addBox(1.44F, -4.88F, -2.02F, 1.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(7.96F, 8.38F, -0.98F));

        PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(38, 63).addBox(-1.34F, -5.7F, -2.5F, 4.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(30, 70).addBox(-2.54F, -4.9F, -2.0F, 1.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 47).addBox(-1.54F, -4.8F, -2.0F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(17, 59).addBox(-2.54F, 0.2F, -2.0F, 6.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(12, 59).addBox(-1.54F, 0.2F, 3.0F, 4.0F, 6.0F, -6.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.26F, 8.6F, -1.0F));

        PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(63, 27).addBox(-1.5F, -5.0F, -1.5F, 3.0F, 10.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 19.0F, -1.2F));

        PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(59, 59).addBox(-1.5F, -5.0F, -1.5F, 3.0F, 10.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(2.3F, 19.0F, -1.2F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(RoyalSculkKnightEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.applyHeadRotation(netHeadYaw, headPitch);

        this.animateWalk(RoyalSculkKnightAnimations.WALKING, limbSwing, limbSwingAmount, 1f, 0.75f);
        this.animate(entity.idleAnimationState, RoyalSculkKnightAnimations.IDLE, ageInTicks, 1f);
    }

    private void applyHeadRotation(float pNetHeadYaw, float pHeadPitch) {
        pNetHeadYaw = Mth.clamp(pNetHeadYaw, -30.0F, 30.0F);
        pHeadPitch = Mth.clamp(pHeadPitch, -25.0F, 45.0F);

        this.head.yRot = pNetHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot = pHeadPitch * ((float)Math.PI / 180F);
    }

    @Override
    public void renderToBuffer( PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        right_arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        left_arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        right_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        left_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}
