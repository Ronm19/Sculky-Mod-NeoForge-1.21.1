package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.monster.Monster;
import net.ronm19.sculky.entity.animation.SculkExecutionerAnimations;
import net.ronm19.sculky.entity.custom.SculkExecutionerEntity;
import net.ronm19.sculky.entity.custom.SculkKingEntity;

public class SculkExecutionerModel <T extends SculkExecutionerEntity> extends HierarchicalModel<T> implements ArmedModel {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart crown;
    private final ModelPart headwear;
    private final ModelPart body;
    private final ModelPart r_arm;
    private final ModelPart l_arm;
    private final ModelPart r_leg;
    private final ModelPart l_leg;

    public SculkExecutionerModel(ModelPart root) {
        this.root = root.getChild("root");
        this.head = this.root.getChild("head");
        this.crown = this.head.getChild("crown");
        this.headwear = this.root.getChild("headwear");
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

        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.4857F, -32.1357F, 3.1714F));

        PartDefinition head_r1 = head.addOrReplaceChild("head_r1", CubeListBuilder.create().texOffs(39, 40).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0143F, 1.5357F, -0.2714F, 0.0F, 3.1416F, 0.0F));

        PartDefinition crown = head.addOrReplaceChild("crown", CubeListBuilder.create().texOffs(72, 51).addBox(-4.9714F, -4.0286F, -5.9429F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(47, 37).addBox(-4.9714F, -1.0286F, -5.9429F, 10.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(70, 37).addBox(-4.9714F, -1.0286F, 3.5571F, 10.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(63, 56).addBox(4.0286F, -1.0286F, -5.9429F, 1.0F, 1.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(47, 24).addBox(-4.9714F, -1.0286F, -5.1429F, 1.0F, 1.0F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(34, 68).addBox(-2.4714F, -6.2286F, -6.1429F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(47, 16).addBox(1.1286F, -6.2286F, -6.1429F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(34, 61).addBox(-0.5714F, -4.1286F, -6.2429F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(77, 51).addBox(4.0286F, -4.0286F, -5.9429F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(28, 79).addBox(4.0286F, -6.0286F, 3.5571F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(80, 7).addBox(-0.7714F, -6.0286F, 3.6571F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(34, 75).addBox(-4.9714F, -6.1286F, 3.6571F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.0143F, -1.5357F, 0.2714F));

        PartDefinition headwear = root.addOrReplaceChild("headwear", CubeListBuilder.create().texOffs(4, 19).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.5F, -31.2F, 2.8F));

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 40).addBox(-6.0F, -7.0F, -2.3F, 12.0F, 13.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(72, 40).addBox(-3.0F, 4.8F, 4.8F, 6.0F, 10.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.2F, -18.8F, 1.3F));

        PartDefinition body_r1 = body.addOrReplaceChild("body_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-9.0F, -3.0F, -4.5F, 18.0F, 6.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -4.9F, 1.3F, 0.0F, 3.1416F, 0.0F));

        PartDefinition body_r2 = body.addOrReplaceChild("body_r2", CubeListBuilder.create().texOffs(0, 80).addBox(-3.0F, -13.0F, 0.0F, 6.0F, 18.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 11.4F, -2.4F, 3.1416F, 0.0F, 3.1416F));

        PartDefinition r_arm = root.addOrReplaceChild("r_arm", CubeListBuilder.create().texOffs(68, 25).addBox(1.8F, -4.42F, -3.04F, 1.0F, 5.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(13, 79).addBox(-3.2F, -4.42F, -4.04F, 6.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(79, 68).addBox(-3.2F, -4.42F, 2.86F, 6.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(12.5F, -22.28F, 2.74F));

        PartDefinition r_arm_r1 = r_arm.addOrReplaceChild("r_arm_r1", CubeListBuilder.create().texOffs(55, 0).addBox(-2.5F, -9.5F, -3.0F, 6.0F, 18.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.9F, 5.18F, -0.04F, 0.0F, 3.1416F, 0.0F));

        PartDefinition l_arm = root.addOrReplaceChild("l_arm", CubeListBuilder.create().texOffs(64, 68).addBox(-2.9833F, -2.7F, -3.0833F, 1.0F, 5.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(79, 75).addBox(-3.0833F, -2.8F, -3.5833F, 6.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(80, 0).addBox(-2.9833F, -2.7F, 2.9167F, 6.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-11.2167F, -23.9F, 3.0833F));

        PartDefinition l_arm_r1 = l_arm.addOrReplaceChild("l_arm_r1", CubeListBuilder.create().texOffs(55, 0).addBox(-2.5F, -9.5F, -3.0F, 6.0F, 18.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.6167F, 6.9F, -0.0833F, 0.0F, -3.1241F, 0.0F));

        PartDefinition r_leg = root.addOrReplaceChild("r_leg", CubeListBuilder.create(), PartPose.offset(-3.6F, -6.3F, 1.5F));

        PartDefinition r_leg_r1 = r_leg.addOrReplaceChild("r_leg_r1", CubeListBuilder.create().texOffs(17, 61).addBox(-2.0F, -6.5F, -2.0F, 4.0F, 13.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 3.098F, 0.0F));

        PartDefinition l_leg = root.addOrReplaceChild("l_leg", CubeListBuilder.create(), PartPose.offset(3.8F, -6.3F, 1.5F));

        PartDefinition l_leg_r1 = l_leg.addOrReplaceChild("l_leg_r1", CubeListBuilder.create().texOffs(17, 61).addBox(-2.0F, -6.5F, -2.0F, 4.0F, 13.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -3.1241F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(SculkExecutionerEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        this.root.getAllParts().forEach(ModelPart::resetPose);

        this.animate(entity.idleAnimationState, SculkExecutionerAnimations.IDLE, ageInTicks, 1.0F);
        this.animateWalk(SculkExecutionerAnimations.WALKING, limbSwing, limbSwingAmount, 1.6F, 2.0F);
        this.animate(entity.attackAnimationState, SculkExecutionerAnimations.ATTACK, ageInTicks, 1.0F);

        boolean attacking = entity.getAttackAnimationTicks() > 0;

        if (!attacking) {
            float clampedYaw = Mth.clamp(netHeadYaw, -25.0F, 25.0F);
            float clampedPitch = Mth.clamp(headPitch, -12.0F, 12.0F);

            this.head.yRot += clampedYaw * Mth.DEG_TO_RAD * 0.55F;
            this.head.xRot += clampedPitch * Mth.DEG_TO_RAD * 0.45F;

            // Your headwear is not a child of head, so it needs to follow the head manually.
            this.headwear.yRot += clampedYaw * Mth.DEG_TO_RAD * 0.55F;
            this.headwear.xRot += clampedPitch * Mth.DEG_TO_RAD * 0.45F;
        }
    }

    @Override
    public void renderToBuffer( PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

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
