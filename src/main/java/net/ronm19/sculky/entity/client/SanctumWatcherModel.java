package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.ronm19.sculky.entity.animation.SalvatoreAnimations;
import net.ronm19.sculky.entity.animation.SanctumWatcherAnimations;
import net.ronm19.sculky.entity.custom.SanctumWatcherEntity;

public class SanctumWatcherModel <T extends SanctumWatcherEntity> extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart r_arm;
    private final ModelPart l_arm;
    private final ModelPart leg1;
    private final ModelPart leg2;

    public SanctumWatcherModel(ModelPart root) {
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

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.1F, -14.9F, 1.2F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 16).addBox(-3.5F, -20.2747F, -2.4619F, 7.0F, 19.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.1F, 9.3747F, 1.2619F));

        PartDefinition body_r1 = body.addOrReplaceChild("body_r1", CubeListBuilder.create().texOffs(24, 36).addBox(-4.5F, -9.5F, 1.5F, 9.0F, 20.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.6253F, -4.8619F, -0.0698F, 0.0F, 0.0F));

        PartDefinition body_r2 = body.addOrReplaceChild("body_r2", CubeListBuilder.create().texOffs(24, 16).addBox(-4.5F, -9.5F, 1.5F, 9.0F, 20.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.1253F, 1.8381F, 0.0524F, 0.0F, 0.0F));

        PartDefinition r_arm = partdefinition.addOrReplaceChild("r_arm", CubeListBuilder.create().texOffs(0, 40).addBox(-1.4667F, -20.7667F, -1.5F, 3.0F, 23.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(32, 8).addBox(-1.1667F, 2.1333F, -1.5F, 1.0F, 5.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(42, 38).addBox(0.1333F, 2.1333F, -1.5F, 1.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.9333F, 10.3667F, 1.0F));

        PartDefinition l_arm = partdefinition.addOrReplaceChild("l_arm", CubeListBuilder.create().texOffs(12, 40).addBox(-1.5F, -20.7667F, -1.5F, 3.0F, 23.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(42, 46).addBox(0.2F, 2.1333F, -1.5F, 1.0F, 5.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(32, 0).addBox(-1.2F, 2.1333F, -1.5F, 1.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(3.9F, 10.3667F, 1.0F));

        PartDefinition leg1 = partdefinition.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(42, 19).addBox(-1.0F, -8.5F, -1.0F, 2.0F, 17.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.1F, 16.1F, 0.9F));

        PartDefinition leg2 = partdefinition.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(42, 0).addBox(-1.0F, -8.5F, -1.0F, 2.0F, 17.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 16.1F, 0.9F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(SanctumWatcherEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        this.animateWalk(SanctumWatcherAnimations.WALKING, limbSwing, limbSwingAmount, 1f, 1f);
        this.animate(entity.idleAnimationState, SanctumWatcherAnimations.IDLE, ageInTicks, 1f);
        this.animate(entity.attackAnimationState, SanctumWatcherAnimations.ATTACK, ageInTicks, 1f);
    }



    @Override
    public void renderToBuffer( PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay);
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
