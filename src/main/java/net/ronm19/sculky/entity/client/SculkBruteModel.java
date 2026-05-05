package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.ronm19.sculky.entity.animation.HollowhornAnimations;
import net.ronm19.sculky.entity.animation.SculkBruteAnimations;
import net.ronm19.sculky.entity.custom.SculkBruteEntity;
import org.jetbrains.annotations.NotNull;

public class SculkBruteModel <T extends SculkBruteEntity> extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart r_arm;
    private final ModelPart l_arm;
    private final ModelPart leg1;
    private final ModelPart leg2;

    public SculkBruteModel(ModelPart root) {
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

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 79).addBox(-4.0F, -8.8F, -4.3F, 8.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(1.1F, -13.1F, 2.5F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-10.05F, -16.6F, -6.5F, 20.0F, 11.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(40, 23).addBox(-6.45F, -5.6F, -5.5F, 13.0F, 11.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(1.45F, 1.5F, 2.7F));

        PartDefinition r_arm = partdefinition.addOrReplaceChild("r_arm", CubeListBuilder.create().texOffs(72, 71).addBox(-4.5F, -10.6F, -5.0F, 11.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(80, 60).addBox(-2.5F, -11.5F, -2.2F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(40, 44).addBox(-4.5F, -0.7F, -5.3F, 10.0F, 17.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(80, 44).addBox(-4.5F, 16.2F, -5.3F, 10.0F, 6.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(16.0F, -4.2F, 2.7F));

        PartDefinition l_arm = partdefinition.addOrReplaceChild("l_arm", CubeListBuilder.create().texOffs(0, 23).addBox(-5.3F, -13.4333F, -5.5F, 10.0F, 17.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(86, 20).addBox(-5.3F, 3.4667F, -5.6F, 10.0F, 6.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(64, 0).addBox(-6.9F, -23.3333F, -5.0F, 12.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(86, 36).addBox(-2.1F, -24.2333F, -2.2F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-13.2F, 8.5333F, 2.7F));

        PartDefinition leg1 = partdefinition.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(0, 50).addBox(-7.0F, -18.0F, -3.0F, 7.0F, 18.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition leg2 = partdefinition.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(36, 71).addBox(2.2F, -18.0F, -3.0F, 7.0F, 18.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(SculkBruteEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.applyHeadRotation(netHeadYaw, headPitch);

        this.animateWalk(SculkBruteAnimations.WALKING, limbSwing, limbSwingAmount, 1f, 1f);
        this.animate(entity.idleAnimationState, SculkBruteAnimations.IDLE, ageInTicks, 1f);
        this.animate(entity.attackAnimationState, SculkBruteAnimations.ATTACKING, ageInTicks, 1f);
    }

    private void applyHeadRotation(float pNetHeadYaw, float pHeadPitch) {
        pNetHeadYaw = Mth.clamp(pNetHeadYaw, -30.0F, 30.0F);
        pHeadPitch = Mth.clamp(pHeadPitch, -25.0F, 45.0F);

        this.head.yRot = pNetHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot = pHeadPitch * ((float)Math.PI / 180F);
    }

    @Override
    public void renderToBuffer( @NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
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
