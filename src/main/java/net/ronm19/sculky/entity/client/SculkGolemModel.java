package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.ronm19.sculky.entity.animation.SculkGolemAnimations;
import net.ronm19.sculky.entity.custom.SculkGolemEntity;

public class SculkGolemModel <T extends SculkGolemEntity> extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart r_arm;
    private final ModelPart l_arm;
    private final ModelPart leg1;
    private final ModelPart leg2;

    public SculkGolemModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.r_arm = root.getChild("r_arm");
        this.l_arm = root.getChild("l_arm");
        this.leg1 = root.getChild("leg1");
        this.leg2 = root.getChild("leg2");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(76, 21).addBox(-5.4791F, -0.8441F, -5.3009F, 11.0F, 9.0F, 11.0F, new CubeDeformation(0.0F))
                .texOffs(62, 34).addBox(5.5209F, 0.5559F, -1.5009F, 4.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.2209F, -29.6559F, 2.3009F));

        PartDefinition head_r1 = head.addOrReplaceChild("head_r1", CubeListBuilder.create().texOffs(126, 129).addBox(-2.0F, -1.5F, -1.5F, 4.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.0772F, -0.7942F, -0.0882F, -3.0876F, -0.0299F, -1.8518F));

        PartDefinition head_r2 = head.addOrReplaceChild("head_r2", CubeListBuilder.create().texOffs(130, 45).addBox(-2.0F, -1.5F, -1.5F, 4.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.5832F, -4.191F, 0.0382F, -3.0987F, -0.0443F, -1.5552F));

        PartDefinition head_r3 = head.addOrReplaceChild("head_r3", CubeListBuilder.create().texOffs(70, 129).addBox(-2.0F, -1.5F, -1.5F, 4.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.9744F, 1.7034F, -0.1055F, -3.0802F, 0.0065F, -2.4617F));

        PartDefinition head_r4 = head.addOrReplaceChild("head_r4", CubeListBuilder.create().texOffs(62, 28).addBox(-2.0F, -1.5F, -1.5F, 4.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.5237F, 2.6526F, -0.04F, -3.098F, 0.0436F, -3.1416F));

        PartDefinition head_r5 = head.addOrReplaceChild("head_r5", CubeListBuilder.create().texOffs(130, 39).addBox(-2.0F, -1.5F, -1.5F, 4.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(12.5722F, -4.7942F, -0.0009F, 0.0F, 0.0F, -1.5882F));

        PartDefinition head_r6 = head.addOrReplaceChild("head_r6", CubeListBuilder.create().texOffs(112, 129).addBox(-2.0F, -1.5F, -1.5F, 4.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(12.0722F, -1.3942F, -0.0009F, 0.0F, 0.0F, -1.2915F));

        PartDefinition head_r7 = head.addOrReplaceChild("head_r7", CubeListBuilder.create().texOffs(70, 123).addBox(-2.0F, -1.5F, -1.5F, 4.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.9722F, 1.1058F, -0.0009F, 0.0F, 0.0F, -0.6807F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(62, 41).addBox(-7.175F, -13.8125F, 3.9875F, 14.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-11.375F, -10.6625F, -9.1625F, 23.0F, 13.0F, 15.0F, new CubeDeformation(0.0F))
                .texOffs(0, 47).addBox(-7.375F, 2.2375F, -8.1625F, 15.0F, 6.0F, 13.0F, new CubeDeformation(0.0F))
                .texOffs(0, 28).addBox(-8.875F, 8.2375F, -8.1625F, 18.0F, 6.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.125F, -10.6875F, 4.1125F));

        PartDefinition r_arm = partdefinition.addOrReplaceChild("r_arm", CubeListBuilder.create().texOffs(44, 75).addBox(-6.3714F, -18.4714F, -5.4714F, 13.0F, 10.0F, 11.0F, new CubeDeformation(0.0F))
                .texOffs(56, 47).addBox(-5.6714F, -8.5714F, -5.4714F, 11.0F, 17.0F, 11.0F, new CubeDeformation(0.0F))
                .texOffs(92, 75).addBox(-5.5714F, 8.3286F, -5.4714F, 11.0F, 7.0F, 11.0F, new CubeDeformation(0.0F))
                .texOffs(124, 0).addBox(-5.3714F, -7.5714F, -7.4714F, 11.0F, 16.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(72, 96).addBox(6.6286F, -7.5714F, -5.5714F, -1.0F, 16.0F, 11.0F, new CubeDeformation(0.0F))
                .texOffs(92, 111).addBox(-5.2714F, -7.5714F, -5.5714F, -1.0F, 16.0F, 11.0F, new CubeDeformation(0.0F))
                .texOffs(112, 111).addBox(-5.3714F, -7.5714F, 5.5286F, 11.0F, 16.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(16.6714F, -1.9286F, 2.4714F));

        PartDefinition l_arm = partdefinition.addOrReplaceChild("l_arm", CubeListBuilder.create().texOffs(76, 0).addBox(-6.5286F, -18.4714F, -5.4714F, 13.0F, 10.0F, 11.0F, new CubeDeformation(0.0F))
                .texOffs(0, 66).addBox(-5.5286F, -8.5714F, -5.4714F, 11.0F, 17.0F, 11.0F, new CubeDeformation(0.0F))
                .texOffs(92, 93).addBox(-5.5286F, 8.3286F, -5.4714F, 11.0F, 7.0F, 11.0F, new CubeDeformation(0.0F))
                .texOffs(0, 126).addBox(-5.6286F, -7.5714F, -7.4714F, 11.0F, 16.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(50, 110).addBox(6.3714F, -7.5714F, -5.5714F, -1.0F, 16.0F, 11.0F, new CubeDeformation(0.0F))
                .texOffs(120, 21).addBox(-5.6286F, -7.5714F, 5.5286F, 11.0F, 16.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(30, 110).addBox(-5.5286F, -7.5714F, -5.5714F, -1.0F, 16.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offset(-17.4714F, -1.9286F, 2.4714F));

        PartDefinition leg1 = partdefinition.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(100, 41).addBox(-4.0F, -13.4333F, -2.6667F, 8.0F, 11.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(100, 59).addBox(-3.0F, -2.5333F, -2.6667F, 6.0F, 8.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(0, 94).addBox(-3.0F, 5.4667F, -7.6667F, 6.0F, 2.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.9F, 16.6333F, -1.0333F));

        PartDefinition leg2 = partdefinition.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(126, 59).addBox(-3.0F, -2.5333F, -2.6667F, 6.0F, 8.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(0, 108).addBox(-4.0F, -13.4333F, -2.6667F, 8.0F, 11.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(36, 96).addBox(-3.0F, 5.4667F, -7.6667F, 6.0F, 2.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(5.1F, 16.6333F, -1.0333F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    @Override
    public void setupAnim(SculkGolemEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        this.animateWalk(SculkGolemAnimations.WALKING, limbSwing, limbSwingAmount, 2.0F, 2.5F);
        this.animate(entity.idleAnimationState, SculkGolemAnimations.IDLE, ageInTicks, 1.0F);
        this.animate(entity.attackAnimationState, SculkGolemAnimations.ATTACK, ageInTicks, 1.0F);
        this.animate(entity.roarAnimationState, SculkGolemAnimations.ROAR, ageInTicks, 1.0F);
        this.animate(entity.slamAnimationState, SculkGolemAnimations.SLAM, ageInTicks, 1.0F);
    }

    @Override
    public void renderToBuffer( PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        r_arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        l_arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        leg1.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        leg2.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}
