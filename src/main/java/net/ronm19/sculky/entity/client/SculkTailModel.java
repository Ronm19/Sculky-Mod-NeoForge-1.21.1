package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.ronm19.sculky.entity.custom.SculkTailEntity;

public class SculkTailModel <T extends SculkTailEntity> extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart tail1;
    private final ModelPart tail2;
    private final ModelPart tail3;
    private final ModelPart tail4;
    private final ModelPart tail5;

    public SculkTailModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.tail1 = root.getChild("tail1");
        this.tail2 = root.getChild("tail2");
        this.tail3 = root.getChild("tail3");
        this.tail4 = root.getChild("tail4");
        this.tail5 = root.getChild("tail5");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 31).addBox(-4.0F, -3.0F, -20.0F, 5.0F, 3.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition head_r1 = head.addOrReplaceChild("head_r1", CubeListBuilder.create().texOffs(12, 50).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.6F, 1.3F, -22.0F, 0.0F, 0.1222F, 0.0F));

        PartDefinition head_r2 = head.addOrReplaceChild("head_r2", CubeListBuilder.create().texOffs(0, 50).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 1.3F, -22.0F, 0.0F, -0.1047F, 0.0F));

        PartDefinition head_r3 = head.addOrReplaceChild("head_r3", CubeListBuilder.create().texOffs(24, 49).addBox(1.0F, -5.0F, -1.0F, 0.0F, 4.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.1F, -2.0F, -19.3F, 0.0F, -0.1396F, 0.0F));

        PartDefinition head_r4 = head.addOrReplaceChild("head_r4", CubeListBuilder.create().texOffs(38, 49).addBox(1.0F, -5.0F, -1.0F, 0.0F, 4.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -2.0F, -19.0F, 0.0F, 0.1745F, 0.0F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition body_r1 = body.addOrReplaceChild("body_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-11.0F, -1.0F, -15.0F, 10.0F, 0.0F, 31.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.2F, 0.0F, 3.7F, 0.0F, -0.0175F, 0.0F));

        PartDefinition tail1 = partdefinition.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(24, 31).addBox(-4.0F, -2.0F, -12.9F, 5.0F, 2.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(52, 49).addBox(-1.1F, -5.0F, -10.9F, 0.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition tail2 = partdefinition.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(0, 54).addBox(-2.5F, -5.0F, -4.1F, 0.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition body_r2 = tail2.addOrReplaceChild("body_r2", CubeListBuilder.create().texOffs(24, 40).addBox(-4.0F, -2.0F, -3.0F, 5.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.3F, 0.0F, -2.9F, 0.0F, -0.0873F, 0.0F));

        PartDefinition tail3 = partdefinition.addOrReplaceChild("tail3", CubeListBuilder.create().texOffs(10, 54).addBox(-2.5F, -4.0F, 1.7F, 0.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(5, 53).addBox(-3.4F, -5.0F, 4.5F, 0.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition body_r3 = tail3.addOrReplaceChild("body_r3", CubeListBuilder.create().texOffs(0, 41).addBox(-4.0F, -2.0F, -3.0F, 5.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.7F, 0.0F, 3.7F, 0.0F, -0.0175F, 0.0F));

        PartDefinition tail4 = partdefinition.addOrReplaceChild("tail4", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition body_r4 = tail4.addOrReplaceChild("body_r4", CubeListBuilder.create().texOffs(48, 31).addBox(-4.0F, -2.0F, -3.0F, 5.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.2F, 0.0F, 9.9F, 0.0F, 0.1745F, 0.0F));

        PartDefinition tail5 = partdefinition.addOrReplaceChild("tail5", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition body_r5 = tail5.addOrReplaceChild("body_r5", CubeListBuilder.create().texOffs(46, 38).addBox(-4.0F, -2.0F, -3.0F, 5.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.1F, 0.0F, 16.8F, 0.0F, 0.1745F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim( SculkTailEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch ) {
        // ----------------------------
        // IDLE BREATHING
        // ----------------------------
        float idle = Mth.sin(ageInTicks * 0.08F) * 0.02F;
        this.body.xRot = idle;

        // ----------------------------
        // MOVEMENT INTENSITY
        // ----------------------------
        float move = Mth.sin(limbSwing * 0.6F) * limbSwingAmount;
        float moveStrength = Math.min(limbSwingAmount, 1.0F);

        // ----------------------------
        // TAIL WAVE SETTINGS
        // ----------------------------
        float waveSpeed = 0.18F;
        float waveAmplitude = 0.35F * (0.3F + moveStrength);

        // Increase motion if agitated
        if (entity.isAgitated()) {
            waveAmplitude *= 1.4F;
            this.body.xRot += 0.08F;
        }

        float baseWave = ageInTicks * waveSpeed + limbSwing * 0.9F;

        // ----------------------------
        // 5-SEGMENT WAVE PROPAGATION
        // ----------------------------

        this.tail2.yRot = Mth.sin(baseWave + 0.5F) * waveAmplitude;
        this.tail3.yRot = Mth.sin(baseWave + 1.0F) * waveAmplitude;
        this.tail4.yRot = Mth.sin(baseWave + 1.5F) * waveAmplitude;
        this.tail5.yRot = Mth.sin(baseWave + 2.0F) * waveAmplitude;

        // Slight vertical undulation (organic feel)

        this.tail2.xRot = Mth.cos(baseWave + 0.5F) * 0.08F;
        this.tail3.xRot = Mth.cos(baseWave + 1.0F) * 0.08F;
        this.tail4.xRot = Mth.cos(baseWave + 1.5F) * 0.08F;
        this.tail5.xRot = Mth.cos(baseWave + 2.0F) * 0.08F;

        // ----------------------------
        // BODY SWAY (VERY SUBTLE)
        // ----------------------------
        this.body.yRot = move * 0.15F;
    }


    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        tail1.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        tail2.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        tail3.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        tail4.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        tail5.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}