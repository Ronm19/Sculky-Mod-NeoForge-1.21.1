package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.ronm19.sculky.entity.animation.SculkBearAnimations;
import net.ronm19.sculky.entity.custom.SculkBearEntity;
import org.jetbrains.annotations.NotNull;

public class SculkBearModel <T extends SculkBearEntity> extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart spikes;
    private final ModelPart left_hind_leg;
    private final ModelPart right_hind_leg;
    private final ModelPart left_front_leg;
    private final ModelPart right_front_leg;

    public SculkBearModel(ModelPart root) {
        this.root = root.getChild("root");
        this.head = this.root.getChild("head");
        this.body = this.root.getChild("body");
        this.spikes = this.body.getChild("spikes");
        this.left_hind_leg = this.root.getChild("left_hind_leg");
        this.right_hind_leg = this.root.getChild("right_hind_leg");
        this.left_front_leg = this.root.getChild("left_front_leg");
        this.right_front_leg = this.root.getChild("right_front_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(44, 25).addBox(-3.5F, -2.25F, -1.75F, 7.0F, 7.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(50, 16).addBox(-2.5F, 1.75F, -4.75F, 5.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(50, 22).addBox(2.5F, -3.25F, 0.25F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(56, 22).addBox(-4.5F, -3.25F, 0.25F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -14.75F, -17.25F));

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-7.4546F, -6.2273F, -10.3636F, 14.0F, 14.0F, 11.0F, new CubeDeformation(0.0F))
                .texOffs(44, 57).addBox(-1.5546F, 7.7727F, -6.2636F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 25).addBox(-6.4546F, -18.2273F, -10.3636F, 12.0F, 12.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.4546F, -18.3636F, 5.2273F, 1.5708F, 0.0F, 0.0F));

        PartDefinition spikes = body.addOrReplaceChild("spikes", CubeListBuilder.create().texOffs(62, 60).addBox(-0.5F, -6.5F, -1.25F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(56, 57).addBox(-0.5F, -6.0F, -0.25F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(56, 60).addBox(-0.5F, -2.5F, -0.25F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(24, 63).addBox(-0.5F, -3.0F, -1.25F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(32, 63).addBox(-0.5F, 0.8F, -1.25F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(62, 22).addBox(-0.5F, 1.3F, -0.25F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(62, 57).addBox(-0.5F, 5.2F, -0.25F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(28, 63).addBox(-0.5F, 4.7F, -1.25F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.3546F, 0.2727F, 1.8864F));

        PartDefinition left_hind_leg = root.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(44, 39).addBox(-2.0F, -5.0F, -4.0F, 4.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(4.5F, -5.0F, 8.0F));

        PartDefinition right_hind_leg = root.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(0, 47).addBox(-2.0F, -5.0F, -4.0F, 4.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.5F, -5.0F, 8.0F));

        PartDefinition left_front_leg = root.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(24, 47).addBox(-2.0F, -5.0F, -3.0F, 4.0F, 10.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(3.5F, -5.0F, -7.0F));

        PartDefinition right_front_leg = root.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(50, 0).addBox(-2.0F, -5.0F, -3.0F, 4.0F, 10.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.5F, -5.0F, -7.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(SculkBearEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.head.xRot = headPitch * ((float) Math.PI / 180F);

        this.animate(entity.idleAnimationState, SculkBearAnimations.IDLE, ageInTicks, 1.0F);
        this.animateWalk(SculkBearAnimations.WALKING, limbSwing, limbSwingAmount, 1.5F, 1.0F);
        this.animate(entity.attackAnimationState, SculkBearAnimations.ATTACK, ageInTicks, 1.0F);
    }

    @Override
    public void renderToBuffer( @NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public @NotNull ModelPart root() {
        return this.root;
    }
}