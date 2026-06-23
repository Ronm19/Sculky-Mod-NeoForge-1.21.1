package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.ronm19.sculky.entity.animation.SculkKingAnimations;
import net.ronm19.sculky.entity.custom.SculkKingEntity;

public class SculkKingModel <T extends SculkKingEntity> extends HierarchicalModel<T> implements ArmedModel {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart r_arm;
    private final ModelPart l_arm;
    private final ModelPart r_leg;
    private final ModelPart l_leg;

    public SculkKingModel(ModelPart root) {
        this.root = root.getChild("root");
        this.head = this.root.getChild("head");
        this.body = this.root.getChild("body");
        this.r_arm = this.root.getChild("r_arm");
        this.l_arm = this.root.getChild("l_arm");
        this.r_leg = this.root.getChild("r_leg");
        this.l_leg = this.root.getChild("l_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(39, 16).addBox(-3.9714F, -0.9286F, -4.3429F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(67, 55).addBox(-4.9714F, -8.0286F, -4.3429F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(43, 62).addBox(-0.4714F, -11.0286F, -4.9429F, 1.0F, 10.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(67, 63).addBox(4.0286F, -8.0286F, -4.3429F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(70, 4).addBox(2.9286F, -5.0286F, 3.6571F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(57, 69).addBox(-0.4714F, -7.0286F, 3.6571F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(57, 69).addBox(-4.0714F, -5.0286F, 3.6571F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.2714F, -33.6714F, 2.3429F));

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 16).addBox(-6.0F, -7.0F, -2.3F, 12.0F, 13.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(15, 62).addBox(-3.0F, 4.8F, -2.5F, 6.0F, 10.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-9.0F, -7.9F, -3.2F, 18.0F, 6.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.2F, -18.8F, 1.3F));

        PartDefinition body_r1 = body.addOrReplaceChild("body_r1", CubeListBuilder.create().texOffs(28, 62).addBox(-3.0F, -5.0F, 0.0F, 6.0F, 10.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 9.8F, 4.8F, 0.0F, 3.1416F, 0.0F));

        PartDefinition r_arm = root.addOrReplaceChild("r_arm", CubeListBuilder.create().texOffs(25, 37).addBox(-3.4F, -4.32F, -3.04F, 6.0F, 18.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(51, 69).addBox(-2.2F, -5.02F, -0.74F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(55, 0).addBox(1.8F, -4.12F, -3.04F, 1.0F, 5.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(67, 33).addBox(-3.2F, -4.12F, -4.04F, 6.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(67, 41).addBox(-3.2F, -4.12F, 2.86F, 6.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(12.5F, -22.28F, 2.74F));

        PartDefinition l_arm = root.addOrReplaceChild("l_arm", CubeListBuilder.create().texOffs(0, 62).addBox(-2.7833F, -2.5F, -3.0833F, 1.0F, 5.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(67, 41).addBox(-2.7833F, -2.5F, -4.0833F, 6.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(67, 41).addBox(-2.7833F, -2.5F, 2.9167F, 6.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(39, 33).addBox(0.5167F, -3.5F, -0.0833F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(70, 0).addBox(-1.3833F, -3.5F, -1.5833F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(25, 37).addBox(-2.6833F, -2.6F, -3.0833F, 6.0F, 18.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-11.8167F, -23.9F, 3.0833F));

        PartDefinition r_leg = root.addOrReplaceChild("r_leg", CubeListBuilder.create().texOffs(50, 51).addBox(-2.0F, -6.5F, -2.0F, 4.0F, 13.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.6F, -6.3F, 1.5F));

        PartDefinition l_leg = root.addOrReplaceChild("l_leg", CubeListBuilder.create().texOffs(50, 33).addBox(-2.0F, -6.5F, -2.0F, 4.0F, 13.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(3.8F, -6.3F, 1.5F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(SculkKingEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        this.animate(entity.idleAnimationState, SculkKingAnimations.IDLE, ageInTicks, 1.0F);
        this.animateWalk(SculkKingAnimations.WALKING, limbSwing, limbSwingAmount, 1.5F, 2.2F);

        this.animate(entity.attackAnimationState, SculkKingAnimations.ATTACK, ageInTicks, 1.0F);
        this.animate(entity.roarAnimationState, SculkKingAnimations.ROAR, ageInTicks, 1.0F);

        // The King has a tall crown, so full head tracking looks goofy.
        // Keep it slower and more controlled.
        boolean lockedPose = entity.getAttackAnimationTicks() > 0 || entity.getRoarAnimationTicks() > 0;

        if (!lockedPose) {
            float clampedYaw = Mth.clamp(netHeadYaw, -25.0F, 25.0F);
            float clampedPitch = Mth.clamp(headPitch, -10.0F, 14.0F);

            this.head.yRot += clampedYaw * Mth.DEG_TO_RAD * 0.55F;
            this.head.xRot += clampedPitch * Mth.DEG_TO_RAD * 0.45F;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void translateToHand(HumanoidArm side, PoseStack poseStack) {
        this.root.translateAndRotate(poseStack);

        ModelPart armPart = this.getArm(side);
        armPart.translateAndRotate(poseStack);

        // Move from the shoulder pivot down to the lower hand area.
        // Positive Y moves down the model arm.
        if (side == HumanoidArm.RIGHT) {
            poseStack.translate(-0.04F, 0.32F, 0.02F);
        } else {
            poseStack.translate(0.04F, 0.32F, 0.02F);
        }
    }


    protected ModelPart getArm(HumanoidArm side) {
        return side == HumanoidArm.LEFT ? this.l_arm : this.r_arm;
    }
}
