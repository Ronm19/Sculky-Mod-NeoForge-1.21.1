package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.ronm19.sculky.entity.custom.SculkWolfEntity;

public class SculkWolfModel <T extends SculkWolfEntity> extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart mane;
    private final ModelPart body;
    private final ModelPart leg1;
    private final ModelPart leg2;
    private final ModelPart leg3;
    private final ModelPart leg4;
    private final ModelPart tail;

    public SculkWolfModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.mane = root.getChild("mane");
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

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -3.0F, -2.0F, 6.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(16, 14).addBox(2.0F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(16, 14).addBox(-2.0F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 10).addBox(-0.5F, -0.02F, -5.0F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 13.5F, -7.0F));

        PartDefinition mane = partdefinition.addOrReplaceChild("mane", CubeListBuilder.create().texOffs(21, 0).addBox(-3.0F, -3.0F, -3.0F, 8.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 14.0F, -3.0F, 1.5708F, 0.0F, 0.0F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(18, 14).addBox(-3.0F, -2.0F, -3.0F, 6.0F, 9.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 14.0F, 2.0F, 1.5708F, 0.0F, 0.0F));

        PartDefinition leg1 = partdefinition.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(0, 18).addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.5F, 16.0F, 7.0F));

        PartDefinition leg2 = partdefinition.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(0, 18).addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 16.0F, 7.0F));

        PartDefinition leg3 = partdefinition.addOrReplaceChild("leg3", CubeListBuilder.create().texOffs(0, 18).addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.5F, 16.0F, -4.0F));

        PartDefinition leg4 = partdefinition.addOrReplaceChild("leg4", CubeListBuilder.create().texOffs(0, 18).addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 16.0F, -4.0F));

        PartDefinition tail = partdefinition.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(9, 18).addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 12.0F, 10.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void setupAnim(SculkWolfEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {

        // ------------------------------
        // HEAD ROTATION
        // ------------------------------
        this.head.xRot = headPitch * (Mth.PI / 180F);
        this.head.yRot = netHeadYaw * (Mth.PI / 180F);


        // ------------------------------
        // SITTING POSE
        // ------------------------------
        if (entity.isInSittingPose()) {

            // Body position
            this.body.setPos(0, 18, 0);
            this.body.xRot = Mth.PI / 4;

            // Mane follows chest
            this.mane.setPos(-1, 16, -3);
            this.mane.xRot = 1.2566F;

            // Tail raised while sitting
            this.tail.setPos(-1, 21, 6);

            // Back legs folded
            this.leg1.setPos(-2.5F, 22.7F, 2);
            this.leg2.setPos(0.5F, 22.7F, 2);
            this.leg1.xRot = Mth.PI * 1.5F;
            this.leg2.xRot = Mth.PI * 1.5F;

            // Front legs curled
            this.leg3.setPos(-2.49F, 17, -4);
            this.leg4.setPos(0.51F, 17, -4);
            this.leg3.xRot = 5.811947F;
            this.leg4.xRot = 5.811947F;

        } else {

            // ------------------------------
            // STANDING / WALKING POSE
            // ------------------------------

            // Body
            this.body.setPos(0, 14, 2);
            this.body.xRot = Mth.HALF_PI;

            // Mane
            this.mane.setPos(-1, 14, -3);
            this.mane.xRot = this.body.xRot;

            // Tail
            this.tail.setPos(-1, 12, 10);

            // Leg reset positions
            this.leg1.setPos(-2.5F, 16, 7);
            this.leg2.setPos(0.5F, 16, 7);
            this.leg3.setPos(-2.5F, 16, -4);
            this.leg4.setPos(0.5F, 16, -4);

            // Walking animation (opposite legs move together)
            this.leg1.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
            this.leg2.xRot = Mth.cos(limbSwing * 0.6662F + Mth.PI) * 1.4F * limbSwingAmount;
            this.leg3.xRot = Mth.cos(limbSwing * 0.6662F + Mth.PI) * 1.4F * limbSwingAmount;
            this.leg4.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;

            // ------------------------------
            // FIXED VANILLA-STYLE TAIL MOVEMENT
            // ------------------------------

            // Reset unwanted axes
            this.tail.yRot = 0.0F;
            this.tail.zRot = 0.0F;

            // Angry → tail stiff and lifted slightly
            if (entity.isAngry()) {
                this.tail.xRot = -0.2F;
            }

            // Sitting → tail raised upward
            else if (entity.isInSittingPose()) {
                this.tail.xRot = -0.7F;
            }

            // Walking → tail bobs UP/DOWN (vanilla behavior)
            else if (limbSwingAmount > 0.05F) {
                this.tail.xRot = Mth.cos(limbSwing * 0.6662F) * 0.3F * limbSwingAmount;
            }

            // Idle → gentle breathing motion
            else {
                this.tail.xRot = -0.1F + (Mth.cos(ageInTicks * 0.1F) * 0.03F);
            }
        }
    }

    @Override
    public void renderToBuffer( PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        mane.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        leg1.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        leg2.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        leg3.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        leg4.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        tail.render(poseStack, vertexConsumer, packedLight, packedOverlay);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}
