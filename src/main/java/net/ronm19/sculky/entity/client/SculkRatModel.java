package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.ronm19.sculky.entity.animation.SculkHorrorAnimations;
import net.ronm19.sculky.entity.animation.SculkRatAnimations;
import net.ronm19.sculky.entity.custom.SculkRatEntity;
import org.jetbrains.annotations.NotNull;

public class SculkRatModel <T extends SculkRatEntity> extends HierarchicalModel<T> {

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart neck;
    private final ModelPart body;
    private final ModelPart tail;
    private final ModelPart tail2;
    private final ModelPart leg1;
    private final ModelPart leg2;
    private final ModelPart leg3;
    private final ModelPart leg4;

    public SculkRatModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.neck = root.getChild("neck");
        this.body = root.getChild("body");
        this.tail = root.getChild("tail");
        this.tail2 = root.getChild("tail2");
        this.leg1 = root.getChild("leg1");
        this.leg2 = root.getChild("leg2");
        this.leg3 = root.getChild("leg3");
        this.leg4 = root.getChild("leg4");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(22, 23).addBox(-2.0267F, -0.7833F, -1.5836F, 4.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(24, 17).addBox(-1.5267F, 0.2167F, -3.5836F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(24, 21).addBox(-2.4267F, -0.3833F, -0.6836F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(30, 21).addBox(0.3733F, -0.3833F, -0.6836F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0267F, 19.7833F, -9.4164F));

        PartDefinition head_r1 = head.addOrReplaceChild("head_r1", CubeListBuilder.create().texOffs(34, 2).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.3816F, -1.0833F, 1.5433F, 0.0F, 0.6632F, 0.0F));

        PartDefinition head_r2 = head.addOrReplaceChild("head_r2", CubeListBuilder.create().texOffs(34, 0).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.4885F, -1.0833F, 1.491F, 0.0F, -0.5061F, 0.0F));

        PartDefinition neck = partdefinition.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(26, 0).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 20.7F, -8.1F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(24, 11).addBox(-2.0F, -1.5F, -3.7F, 4.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-3.0F, -2.0F, -1.3F, 6.0F, 4.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 20.7F, -3.4F));

        PartDefinition tail = partdefinition.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offset(0.0F, 21.2559F, 6.3883F));

        PartDefinition tail_r1 = tail.addOrReplaceChild("tail_r1", CubeListBuilder.create().texOffs(0, 23).addBox(-1.0F, -1.0F, -4.5F, 2.0F, 2.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1222F, 0.0F, 0.0F));

        PartDefinition tail2 = partdefinition.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(0, 11).addBox(-1.0F, -1.0F, -5.0F, 2.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 21.8F, 15.9F));

        PartDefinition leg1 = partdefinition.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(32, 8).addBox(-0.55F, 0.25F, -1.45F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 34).addBox(-0.45F, -1.75F, -0.55F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(3.55F, 22.95F, -4.75F));

        PartDefinition leg2 = partdefinition.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(22, 29).addBox(-0.5F, 0.25F, -1.85F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(22, 33).addBox(-0.5F, -1.75F, -0.65F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(3.6F, 22.95F, 1.35F));

        PartDefinition leg3 = partdefinition.addOrReplaceChild("leg3", CubeListBuilder.create().texOffs(30, 29).addBox(-0.5F, -1.75F, -0.65F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(26, 4).addBox(-0.5F, 0.25F, -1.85F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.5F, 22.95F, 1.35F));

        PartDefinition leg4 = partdefinition.addOrReplaceChild("leg4", CubeListBuilder.create().texOffs(28, 33).addBox(-0.45F, -1.75F, -0.55F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(26, 8).addBox(-0.55F, 0.25F, -1.45F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.45F, 22.95F, -4.45F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(SculkRatEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        this.animateWalk(SculkRatAnimations.SCULK_RAT_WALKING, limbSwing, limbSwingAmount, 1f, 2.5f);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        neck.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        tail.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        tail2.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        leg1.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        leg2.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        leg3.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        leg4.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public @NotNull ModelPart root() {
        return this.root;
    }
}