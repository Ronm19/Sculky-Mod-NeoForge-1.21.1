package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.ronm19.sculky.entity.animation.HollowhornAnimations;
import net.ronm19.sculky.entity.animation.SculkWolfAlphaAnimations;
import net.ronm19.sculky.entity.custom.HollowhornEntity;

public class HollowhornModel <T extends HollowhornEntity> extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart r_horn;
    private final ModelPart l_horn;
    private final ModelPart body;
    private final ModelPart leg1;
    private final ModelPart leg2;
    private final ModelPart leg3;
    private final ModelPart leg4;
    private final ModelPart tail;

    public HollowhornModel(ModelPart root) {

        this.root = root;
        this.head = root.getChild("head");
        this.r_horn = root.getChild("r_horn");
        this.l_horn = root.getChild("l_horn");
        this.body = root.getChild("body");
        this.leg1 = root.getChild("leg1");
        this.leg2 = root.getChild("leg2");
        this.leg3 = root.getChild("leg3");
        this.leg4 = root.getChild("leg4");
        this.tail = root.getChild("tail");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(18, 48).addBox(-2.2148F, -0.1625F, -6.5765F, 4.0F, 3.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(40, 22).addBox(-2.7148F, -2.1625F, -1.8265F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(40, 32).addBox(-2.7148F, -2.1625F, -1.8265F, 5.0F, 0.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(0, 46).addBox(-3.2148F, -2.1625F, -0.8265F, 5.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(52, 51).addBox(0.5352F, -2.1625F, -0.8265F, 2.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(52, 60).addBox(2.2852F, -2.1625F, 1.1735F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(52, 44).addBox(-4.2148F, -2.1625F, 1.1735F, 5.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(18, 46).addBox(-0.7148F, 0.5875F, -7.5765F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.2852F, 8.2625F, -10.4235F));

        PartDefinition head_r1 = head.addOrReplaceChild("head_r1", CubeListBuilder.create().texOffs(52, 0).addBox(-0.5F, -2.5F, -2.5F, 1.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.4648F, 0.3375F, 0.6735F, 0.0F, -0.48F, 0.0F));

        PartDefinition head_r2 = head.addOrReplaceChild("head_r2", CubeListBuilder.create().texOffs(40, 62).addBox(-0.5F, -2.5F, -0.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.3148F, 0.7375F, 1.0735F, 0.0F, 1.9635F, 0.0F));

        PartDefinition head_r3 = head.addOrReplaceChild("head_r3", CubeListBuilder.create().texOffs(32, 62).addBox(-0.5F, -2.5F, -0.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.3852F, 0.6375F, 0.6735F, 0.0F, 1.2654F, 0.0F));

        PartDefinition head_r4 = head.addOrReplaceChild("head_r4", CubeListBuilder.create().texOffs(52, 10).addBox(-0.5F, -2.5F, -2.5F, 1.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.4852F, 0.3375F, 0.7735F, 0.0F, 0.2618F, 0.0F));

        PartDefinition r_horn = partdefinition.addOrReplaceChild("r_horn", CubeListBuilder.create().texOffs(0, 63).addBox(-2.3834F, 1.0161F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(3.7834F, 2.0839F, -9.0F));

        PartDefinition cube_r1 = r_horn.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(64, 5).addBox(-0.5F, -1.5F, -1.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.9507F, -0.2253F, 0.0F, 0.0F, 0.0F, 1.6755F));

        PartDefinition cube_r2 = r_horn.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(64, 0).addBox(0.0F, -4.0F, -1.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.7834F, -1.4839F, 0.0F, 0.0F, 0.0F, 0.9774F));

        PartDefinition cube_r3 = r_horn.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(12, 63).addBox(0.0F, -4.0F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.2834F, 0.9161F, 0.0F, 0.0F, 0.0F, 0.6807F));

        PartDefinition l_horn = partdefinition.addOrReplaceChild("l_horn", CubeListBuilder.create().texOffs(6, 63).addBox(-2.4589F, 0.9225F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.3589F, 2.1775F, -9.0F, 0.0F, 3.1416F, 0.0F));

        PartDefinition cube_r4 = l_horn.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(18, 63).addBox(-2.6529F, -5.7894F, -1.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.8589F, 1.6225F, 0.0F, 0.0F, 0.0F, 0.9774F));

        PartDefinition cube_r5 = l_horn.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(24, 63).addBox(-3.6825F, -1.1655F, -1.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.3249F, 2.8811F, 0.0F, 0.0F, 0.0F, 2.1642F));

        PartDefinition cube_r6 = l_horn.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(58, 60).addBox(-2.0138F, -6.4869F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.3589F, 4.0225F, 0.0F, 0.0F, 0.0F, 0.6807F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 37).addBox(-4.8667F, -4.6405F, -6.9116F, 10.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.8333F, 11.2405F, -1.0884F));

        PartDefinition body_r1 = body.addOrReplaceChild("body_r1", CubeListBuilder.create().texOffs(0, 22).addBox(-3.0F, -0.5F, -7.0F, 6.0F, 1.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.0667F, 2.2595F, 2.0884F, -0.0873F, 0.0F, 0.0F));

        PartDefinition body_r2 = body.addOrReplaceChild("body_r2", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -6.0F, -1.0F, 10.0F, 6.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.0667F, 1.7595F, -3.9116F, -0.0873F, 0.0F, 0.0F));

        PartDefinition leg1 = partdefinition.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(8, 55).addBox(-1.0F, -5.75F, -0.975F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(60, 20).addBox(-1.0F, 0.25F, -1.025F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.9F, 18.75F, -5.025F));

        PartDefinition leg2 = partdefinition.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(0, 55).addBox(-1.0F, -5.75F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(16, 56).addBox(-1.0F, 0.25F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(3.1F, 18.75F, -5.0F));

        PartDefinition leg3 = partdefinition.addOrReplaceChild("leg3", CubeListBuilder.create().texOffs(44, 55).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(36, 55).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(3.1F, 19.0F, 8.7F));

        PartDefinition leg4 = partdefinition.addOrReplaceChild("leg4", CubeListBuilder.create().texOffs(60, 27).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(24, 56).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.9F, 19.0F, 8.7F));

        PartDefinition tail = partdefinition.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offset(-1.0F, 12.2526F, 13.6214F));

        PartDefinition tail_r1 = tail.addOrReplaceChild("tail_r1", CubeListBuilder.create().texOffs(48, 37).addBox(-1.0F, 0.1F, -2.8F, 2.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.6474F, -0.5214F, -0.5585F, 0.0F, 0.0F));

        PartDefinition tail_r2 = tail.addOrReplaceChild("tail_r2", CubeListBuilder.create().texOffs(36, 48).addBox(-1.0F, 0.1F, -2.8F, 2.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.7526F, 0.9786F, -0.5585F, 0.0F, 0.0F));

        PartDefinition tail_r3 = tail.addOrReplaceChild("tail_r3", CubeListBuilder.create().texOffs(26, 37).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2526F, -3.6214F, -0.5934F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(HollowhornEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.applyHeadRotation(netHeadYaw, headPitch);

        this.animateWalk(HollowhornAnimations.walking, limbSwing, limbSwingAmount, 1f, 1f);
        this.animate(entity.idleAnimationState, HollowhornAnimations.idle, ageInTicks, 1f);
        this.animate(entity.attackAnimationState, HollowhornAnimations.attack, ageInTicks, 1f);
    }

    private void applyHeadRotation(float pNetHeadYaw, float pHeadPitch) {
        pNetHeadYaw = Mth.clamp(pNetHeadYaw, -30.0F, 30.0F);
        pHeadPitch = Mth.clamp(pHeadPitch, -25.0F, 45.0F);

        this.head.yRot = pNetHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot = pHeadPitch * ((float)Math.PI / 180F);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {

        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        r_horn.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        l_horn.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        leg1.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        leg2.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        leg3.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        leg4.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        tail.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}
