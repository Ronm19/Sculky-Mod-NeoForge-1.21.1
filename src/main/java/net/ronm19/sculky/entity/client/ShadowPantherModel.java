package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.ronm19.sculky.entity.animation.SanctumWatcherAnimations;
import net.ronm19.sculky.entity.animation.ShadowPantherAnimations;
import net.ronm19.sculky.entity.custom.ShadowPantherEntity;

public class ShadowPantherModel <T extends ShadowPantherEntity> extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart leg1;
    private final ModelPart leg2;
    private final ModelPart leg3;
    private final ModelPart leg4;
    private final ModelPart tail;

    public ShadowPantherModel( ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
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

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(22, 21).addBox(-2.4403F, -1.5F, -2.1977F, 5.0F, 5.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(56, 12).addBox(-3.4403F, -3.5F, 1.1023F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(56, 15).addBox(1.4597F, -3.5F, 0.8023F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(48, 32).addBox(-1.9403F, 0.5F, -5.1977F, 4.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5597F, 12.7675F, -11.3743F, 0.0524F, 0.0F, 0.0F));

        PartDefinition head_r1 = head.addOrReplaceChild("head_r1", CubeListBuilder.create().texOffs(20, 32).addBox(-1.0F, -2.5F, -3.0F, 2.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.6395F, 1.0F, 0.9616F, 0.0F, 0.3316F, 0.0F));

        PartDefinition head_r2 = head.addOrReplaceChild("head_r2", CubeListBuilder.create().texOffs(34, 32).addBox(-1.0F, -2.5F, -3.0F, 2.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.9605F, 1.0F, 0.9616F, 0.0F, -0.4887F, 0.0F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 32).addBox(-3.5F, -3.125F, -6.5F, 7.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-3.5F, -2.675F, -3.5F, 7.0F, 6.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 14.025F, -2.1F));

        PartDefinition leg1 = partdefinition.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(42, 42).addBox(-2.0F, -6.8333F, -2.1667F, 3.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(44, 0).addBox(-2.0F, -1.8333F, -1.1667F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(42, 51).addBox(-2.0F, 3.1667F, -2.1667F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(3.7F, 19.8333F, -4.7333F));

        PartDefinition leg2 = partdefinition.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(44, 8).addBox(-1.1667F, -1.8333F, -1.1667F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(28, 51).addBox(-1.1667F, 3.1667F, -2.1667F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(14, 42).addBox(-1.1667F, -6.8333F, -2.1667F, 3.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.8333F, 19.8333F, -4.7333F));

        PartDefinition leg3 = partdefinition.addOrReplaceChild("leg3", CubeListBuilder.create().texOffs(44, 16).addBox(-1.0F, -1.8333F, -1.1667F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(14, 51).addBox(-1.0F, 3.1667F, -2.1667F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(28, 42).addBox(-1.0F, -6.8333F, -2.1667F, 3.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 19.8333F, 8.5667F));

        PartDefinition leg4 = partdefinition.addOrReplaceChild("leg4", CubeListBuilder.create().texOffs(44, 24).addBox(-2.0F, -1.8333F, -1.1667F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 50).addBox(-2.0F, 3.1667F, -2.1667F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 41).addBox(-2.0F, -6.8333F, -2.1667F, 3.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 19.8333F, 8.5667F));

        PartDefinition tail = partdefinition.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offset(-0.4F, 16.7121F, 21.0664F));

        PartDefinition cube_r1 = tail.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(48, 38).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.9F, 3.3F, -0.3316F, 0.0F, 0.0F));

        PartDefinition cube_r2 = tail.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 55).addBox(0.0F, -2.0F, 0.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 2.1789F, -1.8175F, -0.3491F, 0.0F, 0.0F));

        PartDefinition cube_r3 = tail.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(56, 0).addBox(0.0F, -2.0F, 0.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 0.2789F, -5.1175F, -0.5061F, 0.0F, 0.0F));

        PartDefinition cube_r4 = tail.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(12, 56).addBox(0.0F, -2.0F, 0.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -1.3211F, -8.6175F, -0.4363F, 0.0F, 0.0F));

        PartDefinition cube_r5 = tail.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(55, 5).addBox(0.0F, -2.0F, -1.0F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -2.9211F, -12.2175F, -0.4189F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(ShadowPantherEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.applyHeadRotation(netHeadYaw, headPitch);

        this.animateWalk(ShadowPantherAnimations.WALKING, limbSwing, limbSwingAmount, 1f, 1f);
        this.animate(entity.idleAnimationState, ShadowPantherAnimations.IDLE, ageInTicks, 1f);
        this.animate(entity.attackAnimationState, ShadowPantherAnimations.ATTACKING, ageInTicks, 1f);
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
