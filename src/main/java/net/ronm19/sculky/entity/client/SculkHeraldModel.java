package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.ronm19.sculky.entity.animation.SculkBruteAnimations;
import net.ronm19.sculky.entity.animation.SculkHeraldAnimations;
import net.ronm19.sculky.entity.custom.SculkHeraldEntity;
import org.jetbrains.annotations.NotNull;

public class SculkHeraldModel <T extends SculkHeraldEntity> extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart r_arm;
    private final ModelPart l_arm;
    private final ModelPart leg1;
    private final ModelPart leg2;
    private final ModelPart heralds_staff;

    public SculkHeraldModel(ModelPart root) {
        this.root = root.getChild("root");
        this.head = this.root.getChild("head");
        this.body = this.root.getChild("body");
        this.r_arm = this.root.getChild("r_arm");
        this.l_arm = this.root.getChild("l_arm");
        this.leg1 = this.root.getChild("leg1");
        this.leg2 = this.root.getChild("leg2");
        this.heralds_staff = this.root.getChild("herald's staff");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(29, 44).addBox(-3.5545F, 1.7273F, -2.7818F, 7.0F, 7.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(19, 58).addBox(-3.9545F, 0.3273F, -3.4818F, 8.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(58, 21).addBox(-3.9545F, 0.2273F, 2.3182F, 8.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(11, 74).addBox(-0.8545F, -4.4727F, -3.5818F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(32, 74).addBox(2.3455F, -3.4727F, -3.5818F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(26, 74).addBox(-2.9545F, -3.4727F, -3.5818F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(40, 74).addBox(-3.4545F, -3.5727F, 2.3182F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(55, 73).addBox(-1.1545F, -4.7727F, 2.4182F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(18, 74).addBox(2.3455F, -3.4727F, 2.3182F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.0455F, -32.8273F, 1.6818F));

        PartDefinition head_r1 = head.addOrReplaceChild("head_r1", CubeListBuilder.create().texOffs(38, 58).addBox(-3.0F, -1.0F, -0.5F, 6.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.9545F, 1.2273F, 0.3182F, 0.0F, -1.5708F, 0.0F));

        PartDefinition head_r2 = head.addOrReplaceChild("head_r2", CubeListBuilder.create().texOffs(58, 35).addBox(-3.0F, -1.0F, -0.5F, 6.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.8455F, 1.2273F, 0.3182F, 0.0F, -1.5708F, 0.0F));

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(29, 23).addBox(-4.0F, -9.4985F, -4.7372F, 8.0F, 14.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -14.7015F, 3.6372F));

        PartDefinition body_r1 = body.addOrReplaceChild("body_r1", CubeListBuilder.create().texOffs(1, 23).addBox(-6.0F, -11.0F, 0.0F, 13.0F, 22.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.4991F, 1.7023F, 0.0349F, 0.0F, 0.0F));

        PartDefinition r_arm = root.addOrReplaceChild("r_arm", CubeListBuilder.create().texOffs(45, 0).addBox(-2.36F, 0.4F, -2.6467F, 4.0F, 15.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(69, 48).addBox(-2.36F, -1.1F, -2.7467F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(11, 68).addBox(1.64F, -0.6F, -2.1467F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(64, 6).addBox(1.84F, -1.6F, -2.1467F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(19, 62).addBox(-2.36F, -1.6F, -2.1467F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(30, 62).addBox(-1.76F, -2.6F, -2.1467F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(22, 68).addBox(1.74F, -2.6F, -2.1467F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(64, 18).addBox(-2.36F, -0.6F, 1.4533F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(64, 18).addBox(-2.36F, -0.6F, 0.4533F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(69, 54).addBox(-2.36F, -0.6F, -0.5467F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(69, 57).addBox(-2.36F, -0.6F, -1.5467F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(69, 42).addBox(-2.36F, -0.6F, -2.0467F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(6.26F, -23.7F, 1.7467F));

        PartDefinition l_arm = root.addOrReplaceChild("l_arm", CubeListBuilder.create().texOffs(69, 60).addBox(-2.36F, -0.6F, -2.0467F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(69, 63).addBox(-2.36F, -0.6F, -1.5467F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(69, 69).addBox(-2.36F, -0.6F, -0.5467F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(66, 72).addBox(-2.36F, -0.6F, 0.4533F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(73, 25).addBox(-2.36F, -0.6F, 1.4533F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(33, 68).addBox(1.74F, -2.6F, -2.1467F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(41, 62).addBox(-1.76F, -2.6F, -2.1467F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(64, 12).addBox(1.84F, -1.6F, -2.1467F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(64, 0).addBox(-2.36F, -1.6F, -2.1467F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(44, 68).addBox(1.64F, -0.6F, -2.1467F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(73, 28).addBox(-2.36F, -1.1F, -2.7467F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 46).addBox(-2.36F, 0.4F, -2.6467F, 4.0F, 15.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.36F, -23.7F, 1.4533F, 0.0F, 3.1416F, 0.0F));

        PartDefinition leg1 = root.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(56, 44).addBox(-1.5F, -4.5F, -1.5F, 3.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.2F, -6.5F, 1.6F));

        PartDefinition leg2 = root.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(56, 57).addBox(-1.5F, -4.5F, -1.5F, 3.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(2.1F, -6.5F, 1.6F));

        PartDefinition heralds_staff = root.addOrReplaceChild("herald's staff", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5833F, -0.45F, -0.3333F, 1.0F, 1.0F, 21.0F, new CubeDeformation(0.0F))
                .texOffs(0, 67).addBox(-1.9833F, -2.05F, -1.3333F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(58, 32).addBox(-3.9833F, -0.45F, -1.3333F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(19, 51).addBox(-3.9833F, -0.45F, -4.3333F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(19, 46).addBox(3.0167F, -0.45F, -4.3333F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(58, 25).addBox(-1.9833F, -1.65F, -4.3333F, 4.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.7367F, -8.15F, -15.36F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(SculkHeraldEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        this.animateWalk(SculkHeraldAnimations.WALKING, limbSwing, limbSwingAmount, 1f, 1f);
        this.animate(entity.idleAnimationState, SculkHeraldAnimations.IDLE, ageInTicks, 1f);
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
