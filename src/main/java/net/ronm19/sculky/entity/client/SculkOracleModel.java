package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.ronm19.sculky.entity.animation.SculkExecutionerAnimations;
import net.ronm19.sculky.entity.animation.SculkOracleAnimations;
import net.ronm19.sculky.entity.custom.SculkOracleEntity;
import org.jetbrains.annotations.NotNull;

public class SculkOracleModel <T extends SculkOracleEntity> extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart robe;
    private final ModelPart r_arm;
    private final ModelPart l_arm;
    private final ModelPart r_leg;
    private final ModelPart l_leg;

    public SculkOracleModel(ModelPart root) {
        this.root = root.getChild("root");
        this.head = this.root.getChild("head");
        this.body = this.root.getChild("body");
        this.robe = this.body.getChild("robe");
        this.r_arm = this.root.getChild("r_arm");
        this.l_arm = this.root.getChild("l_arm");
        this.r_leg = this.root.getChild("r_leg");
        this.l_leg = this.root.getChild("l_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 34).addBox(-4.5833F, 0.9167F, -4.3333F, 9.0F, 8.0F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(0, 52).addBox(-4.5833F, -0.1833F, -0.5333F, 9.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(30, 52).addBox(-4.5833F, -2.1833F, -0.5333F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(54, 43).addBox(3.4167F, -2.1833F, -0.5333F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(54, 36).addBox(-0.2833F, -3.1833F, -0.5333F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(54, 33).addBox(-1.8833F, -1.1833F, -0.5333F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.4167F, -33.6167F, 0.2333F));

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(-1.8167F, -14.7163F, 3.5202F));

        PartDefinition body_r1 = body.addOrReplaceChild("body_r1", CubeListBuilder.create().texOffs(37, 0).addBox(-5.0F, -10.5F, -2.0F, 9.0F, 16.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.4F, 0.2497F, 0.0131F, 0.0524F, 0.0F, 0.0F));

        PartDefinition robe = body.addOrReplaceChild("robe", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition robe_r1 = robe.addOrReplaceChild("robe_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -10.5F, -4.0F, 10.0F, 25.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.1F, 0.2497F, 0.0131F, 0.0524F, 0.0F, 0.0F));

        PartDefinition r_arm = root.addOrReplaceChild("r_arm", CubeListBuilder.create().texOffs(37, 21).addBox(-2.0F, -8.5F, -2.0F, 4.0F, 17.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(4.6F, -14.1F, 3.3F));

        PartDefinition l_arm = root.addOrReplaceChild("l_arm", CubeListBuilder.create().texOffs(37, 43).addBox(-2.0F, -8.5F, -2.0F, 4.0F, 17.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.4F, -14.1F, 3.3F));

        PartDefinition r_leg = root.addOrReplaceChild("r_leg", CubeListBuilder.create().texOffs(21, 52).addBox(-1.0F, -4.5F, -1.0F, 2.0F, 9.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.6F, 3.6F));

        PartDefinition l_leg = root.addOrReplaceChild("l_leg", CubeListBuilder.create().texOffs(54, 21).addBox(-1.0F, -4.5F, -1.0F, 2.0F, 9.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.4F, -4.6F, 3.6F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(SculkOracleEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root.getAllParts().forEach(ModelPart::resetPose);
        
        this.animate(entity.idleAnimationState, SculkOracleAnimations.IDLE, ageInTicks, 1.0F);
        this.animateWalk(SculkOracleAnimations.WALKING, limbSwing, limbSwingAmount, 1.0F, 1.0F);
        this.animate(entity.attackAnimationState, SculkOracleAnimations.ATTACK, ageInTicks, 1.0F);
    }

    @Override
    public void renderToBuffer( PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public @NotNull ModelPart root() {
        return this.root;
    }
}
