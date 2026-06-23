package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.ronm19.sculky.entity.animation.ThroneboundWraithAnimations;
import net.ronm19.sculky.entity.custom.ThroneboundWraithEntity;
import org.jetbrains.annotations.NotNull;

public class ThroneboundWraithModel <T extends ThroneboundWraithEntity> extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart headwear;
    private final ModelPart body;
    private final ModelPart right_arm;
    private final ModelPart left_arm;
    private final ModelPart legsfog;

    public ThroneboundWraithModel(ModelPart root) {
        this.root = root.getChild("root");
        this.head = this.root.getChild("head");
        this.headwear = this.head.getChild("headwear");
        this.body = this.root.getChild("body");
        this.right_arm = this.root.getChild("right_arm");
        this.left_arm = this.root.getChild("left_arm");
        this.legsfog = this.root.getChild("legsfog");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 12.4F, 0.0F));

        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(31, 43).addBox(-4.5833F, 1.3333F, -4.3333F, 9.0F, 8.0F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(45, 16).addBox(-4.5833F, 0.0333F, -0.5333F, 9.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(17, 54).addBox(-4.5833F, -2.9667F, -0.5333F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(22, 54).addBox(3.4167F, -2.9667F, -0.5333F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(66, 16).addBox(-0.2833F, -3.9667F, -0.5333F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(45, 19).addBox(-1.8833F, -0.9667F, -0.5333F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.4167F, -32.2333F, 0.2333F));

        PartDefinition headwear = head.addOrReplaceChild("headwear", CubeListBuilder.create().texOffs(0, 1).addBox(-5.5F, -5.0F, -5.5F, 11.0F, 10.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.0833F, 5.1333F, 0.2667F));

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(45, 0).addBox(-5.0F, -8.0F, -5.0F, 10.0F, 9.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 23).addBox(-5.0F, 0.9F, -4.0F, 10.0F, 9.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.8167F, -14.7163F, 3.5202F));

        PartDefinition right_arm = root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 54).addBox(-2.45F, -4.5F, -2.0F, 4.0F, 17.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(34, 61).addBox(-2.55F, -6.5F, -2.5F, 6.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(5.05F, -16.1F, 2.3F));

        PartDefinition left_arm = root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(17, 61).addBox(-1.7F, -4.5F, -2.0F, 4.0F, 17.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(57, 61).addBox(-3.3F, -6.5F, -2.5F, 6.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-9.0F, -16.2F, 1.6F));

        PartDefinition legsfog = root.addOrReplaceChild("legsfog", CubeListBuilder.create().texOffs(31, 23).addBox(-6.0F, -7.0F, -3.0F, 12.0F, 13.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.8F, 2.1F, 1.95F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(ThroneboundWraithEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.head.xRot = headPitch * ((float) Math.PI / 180F);

        this.animate(entity.idleAnimationState, ThroneboundWraithAnimations.IDLE, ageInTicks, 1.0F);
        this.animate(entity.attackAnimationState, ThroneboundWraithAnimations.ATTACK, ageInTicks, 1.0F);
        this.animateWalk(ThroneboundWraithAnimations.FLYING, limbSwing, limbSwingAmount, 1.0F, 2.0F);
    }

    @Override
    public void renderToBuffer( @NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}
