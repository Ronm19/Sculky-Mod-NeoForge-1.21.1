package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.ronm19.sculky.entity.animation.SculkBulwarkAnimations;
import net.ronm19.sculky.entity.custom.SculkBulwarkEntity;
import org.jetbrains.annotations.NotNull;

public class SculkBulwarkModel <T extends SculkBulwarkEntity> extends HierarchicalModel<T> implements ArmedModel {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart crown;
    private final ModelPart body;
    private final ModelPart r_arm;
    private final ModelPart l_arm;
    private final ModelPart r_leg;
    private final ModelPart l_leg;

    public SculkBulwarkModel(ModelPart root) {
        this.root = root.getChild("root");
        this.head = this.root.getChild("head");
        this.crown = this.head.getChild("crown");
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

        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(66, 77).addBox(-4.0667F, -6.1917F, -3.6F, 8.0F, 7.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0667F, -25.8083F, 1.6F));

        PartDefinition crown = head.addOrReplaceChild("crown", CubeListBuilder.create().texOffs(67, 93).addBox(-4.9333F, -11.6833F, -4.4F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(51, 15).addBox(-4.9333F, -8.6833F, -4.4F, 10.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(82, 61).addBox(-4.9333F, -8.6833F, 5.1F, 10.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(82, 49).addBox(4.0667F, -8.6833F, -4.4F, 1.0F, 1.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(90, 0).addBox(-4.9333F, -8.6833F, -3.6F, 1.0F, 1.0F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(34, 81).addBox(-2.4333F, -13.5833F, -4.2F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(47, 91).addBox(1.3667F, -13.5833F, -4.6F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(26, 92).addBox(-0.5333F, -11.7833F, -4.7F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(62, 93).addBox(4.0667F, -11.6833F, -4.4F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(52, 91).addBox(4.0667F, -13.6833F, 5.1F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(57, 91).addBox(-0.7333F, -13.6833F, 5.2F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(21, 92).addBox(-4.9333F, -13.7833F, 5.2F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0667F, 2.6917F, -0.4F));

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-7.05F, -11.375F, -5.375F, 14.0F, 8.0F, 11.0F, new CubeDeformation(0.0F))
                .texOffs(66, 66).addBox(-6.05F, -3.375F, -3.375F, 12.0F, 3.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(21, 81).addBox(-2.95F, -0.375F, -3.375F, 6.0F, 10.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(34, 91).addBox(-2.95F, -0.375F, 3.425F, 6.0F, 10.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.05F, -13.625F, 2.375F));

        PartDefinition r_arm = root.addOrReplaceChild("r_arm", CubeListBuilder.create().texOffs(0, 47).addBox(-4.7F, -11.44F, -5.5F, 9.0F, 7.0F, 11.0F, new CubeDeformation(0.0F))
                .texOffs(0, 20).addBox(-4.7F, -4.44F, -5.5F, 8.0F, 15.0F, 11.0F, new CubeDeformation(0.0F))
                .texOffs(39, 66).addBox(3.3F, -1.24F, -5.5F, 2.0F, 13.0F, 11.0F, new CubeDeformation(0.0F))
                .texOffs(51, 0).addBox(-4.7F, 10.56F, -5.5F, 8.0F, 3.0F, 11.0F, new CubeDeformation(0.0F))
                .texOffs(78, 15).addBox(-4.7F, -13.44F, -5.5F, 4.0F, 2.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offset(11.7F, -14.36F, 2.5F));

        PartDefinition l_arm = root.addOrReplaceChild("l_arm", CubeListBuilder.create().texOffs(0, 47).addBox(-4.7667F, -13.8333F, -5.5F, 9.0F, 7.0F, 11.0F, new CubeDeformation(0.0F))
                .texOffs(0, 20).addBox(-3.8667F, -6.8333F, -5.5F, 8.0F, 15.0F, 11.0F, new CubeDeformation(0.0F))
                .texOffs(51, 0).addBox(-3.8667F, 8.1667F, -5.5F, 8.0F, 3.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offset(-11.2333F, -11.9667F, 2.5F));

        PartDefinition r_leg = root.addOrReplaceChild("r_leg", CubeListBuilder.create().texOffs(0, 81).addBox(-2.5F, -7.0F, -2.5F, 5.0F, 14.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, -7.0F, 2.5F));

        PartDefinition l_leg = root.addOrReplaceChild("l_leg", CubeListBuilder.create().texOffs(82, 29).addBox(-2.5F, -7.0F, -2.5F, 5.0F, 14.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.5F, -7.0F, 2.5F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root.getAllParts().forEach(ModelPart::resetPose);

        this.animate(entity.idleAnimationState, SculkBulwarkAnimations.IDLE, ageInTicks, 1.0F);
        this.animateWalk(SculkBulwarkAnimations.WALK, limbSwing, limbSwingAmount, 1.4F, 1.8F);
        this.animate(entity.bashAnimationState, SculkBulwarkAnimations.SHIELD_BASH, ageInTicks, 1.0F);

        boolean bashing = entity.getBashAnimationTicks() > 0;

        if (!bashing) {
            float clampedYaw = Mth.clamp(netHeadYaw, -20.0F, 20.0F);
            float clampedPitch = Mth.clamp(headPitch, -10.0F, 12.0F);

            this.head.yRot += clampedYaw * Mth.DEG_TO_RAD * 0.45F;
            this.head.xRot += clampedPitch * Mth.DEG_TO_RAD * 0.35F;
        }
    }

    @Override
    public void renderToBuffer( @NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public @NotNull ModelPart root() {
        return this.root;
    }

    @Override
    public void translateToHand(HumanoidArm side, PoseStack poseStack) {
        this.root.translateAndRotate(poseStack);

        ModelPart armPart = this.getArm(side);
        armPart.translateAndRotate(poseStack);

        boolean left = side == HumanoidArm.LEFT;

        // Forearm-mounted shield placement, pushed slightly outward.
        poseStack.translate(left ? 0.06F : -0.06F, -0.65F, -0.18F);

        poseStack.mulPose(Axis.YP.rotationDegrees(left ? 180.0F : 0.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(left ? -4.0F : 4.0F));

        poseStack.scale(1.2F, 1.2F, 1.2F);
    }

    private ModelPart getArm(HumanoidArm side) {
        return side == HumanoidArm.LEFT ? this.l_arm : this.r_arm;
    }
}
