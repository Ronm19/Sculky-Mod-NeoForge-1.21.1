package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.ronm19.sculky.entity.animation.SculkBurrowerAnimations;
import net.ronm19.sculky.entity.custom.SculkBurrowerEntity;
import org.jetbrains.annotations.NotNull;

public class SculkBurrowerModel <T extends SculkBurrowerEntity> extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart leg1;
    private final ModelPart leg2;
    private final ModelPart leg3;
    private final ModelPart leg4;

    public SculkBurrowerModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.leg1 = root.getChild("leg1");
        this.leg2 = root.getChild("leg2");
        this.leg3 = root.getChild("leg3");
        this.leg4 = root.getChild("leg4");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 21).addBox(-4.0F, -2.6667F, -0.3333F, 8.0F, 4.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(16, 40).addBox(-4.0F, -0.6667F, -2.3333F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(40, 21).addBox(3.0F, -0.6667F, -2.3333F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 20.0667F, -9.3667F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -5.9F, -3.8F, 10.0F, 5.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(24, 39).addBox(-2.1F, -7.9F, -2.6F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(32, 39).addBox(1.1F, -7.9F, 0.1F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 40).addBox(-1.7F, -7.9F, 4.3F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(8, 40).addBox(1.2F, -7.9F, 7.3F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 22.3F, 0.0F));

        PartDefinition leg1 = partdefinition.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(0, 31).addBox(-1.0F, -2.5F, -2.0F, 2.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, 21.5F, -1.1F));

        PartDefinition leg2 = partdefinition.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(28, 30).addBox(-1.0F, -2.5F, -2.0F, 2.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(6.1F, 21.5F, 10.0F));

        PartDefinition leg3 = partdefinition.addOrReplaceChild("leg3", CubeListBuilder.create().texOffs(12, 31).addBox(-1.0F, -2.5F, -2.0F, 2.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, 21.5F, 10.1F));

        PartDefinition leg4 = partdefinition.addOrReplaceChild("leg4", CubeListBuilder.create().texOffs(28, 21).addBox(-1.0F, -2.5F, -2.0F, 2.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, 21.5F, -1.5F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(SculkBurrowerEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        this.animate(entity.idleAnimationState, SculkBurrowerAnimations.idle, ageInTicks, 1.0F);
        this.animate(entity.walkAnimationState, SculkBurrowerAnimations.walking, ageInTicks, 1.0F);
        this.animate(entity.attackAnimationState, SculkBurrowerAnimations.attacking, ageInTicks, 1.0F);
        this.animate(entity.burrowAnimationState, SculkBurrowerAnimations.burrowing, ageInTicks, 1.0F);
        this.animate(entity.emergeAnimationState, SculkBurrowerAnimations.emerging, ageInTicks, 1.0F);
    }



    @Override
    public void renderToBuffer( @NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
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
