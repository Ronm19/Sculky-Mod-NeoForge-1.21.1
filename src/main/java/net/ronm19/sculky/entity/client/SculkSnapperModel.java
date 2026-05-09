package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.ronm19.sculky.entity.animation.SculkSnapperAnimations;
import net.ronm19.sculky.entity.custom.SculkSnapperEntity;
import org.jetbrains.annotations.NotNull;

public class SculkSnapperModel <T extends SculkSnapperEntity> extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart back_fin;
    private final ModelPart right_fin;
    private final ModelPart left_fin;
    private final ModelPart tail;

    public SculkSnapperModel(ModelPart root) {
        this.root = root.getChild("root");
        this.head = this.root.getChild("head");
        this.body = this.root.getChild("body");
        this.back_fin = this.root.getChild("back_fin");
        this.right_fin = this.root.getChild("right_fin");
        this.left_fin = this.root.getChild("left_fin");
        this.tail = this.root.getChild("tail");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 25).addBox(-3.3F, -5.0F, -12.4F, 7.0F, 2.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(28, 25).addBox(-3.3F, -11.0F, -12.4F, 7.0F, 2.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(16, 41).addBox(-3.3F, -9.0F, -8.4F, 7.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(16, 46).addBox(-3.3F, -9.0F, -10.4F, 7.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(50, 46).addBox(-3.3F, -6.0F, -12.4F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(54, 21).addBox(-3.3F, -6.0F, -10.3F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(54, 23).addBox(2.7F, -6.0F, -10.3F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(30, 52).addBox(-1.3F, -6.0F, -12.4F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(52, 39).addBox(0.6F, -6.0F, -12.4F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(10, 53).addBox(2.7F, -6.0F, -12.4F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(30, 49).addBox(-3.3F, -9.0F, -12.4F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(30, 54).addBox(-1.3F, -9.0F, -12.4F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(54, 46).addBox(0.6F, -9.0F, -12.4F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(10, 50).addBox(2.7F, -9.0F, -12.4F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -4.05F, -13.25F, 6.0F, 8.0F, 17.0F, new CubeDeformation(0.0F))
                .texOffs(0, 41).addBox(-3.0F, -3.45F, 3.75F, 6.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.2F, -6.9F, 7.9F));

        PartDefinition back_fin = root.addOrReplaceChild("back_fin", CubeListBuilder.create().texOffs(54, 54).addBox(-1.0F, -15.9F, -4.3F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(46, 54).addBox(-1.0F, -15.9F, 4.7F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(46, 21).addBox(-1.0F, -12.9F, 8.9F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 50).addBox(-1.0F, -17.9F, -0.3F, 2.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition right_fin = root.addOrReplaceChild("right_fin", CubeListBuilder.create().texOffs(0, 34).addBox(-5.5F, -0.5F, -3.0F, 7.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(52, 34).addBox(1.5F, -0.5F, -2.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(8.4F, -4.7F, 3.3F));

        PartDefinition left_fin = root.addOrReplaceChild("left_fin", CubeListBuilder.create().texOffs(50, 41).addBox(-2.5F, -0.5F, -2.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(26, 34).addBox(-1.5F, -0.5F, -3.0F, 7.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.3F, -4.7F, 3.3F));

        PartDefinition tail = root.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(34, 48).addBox(-1.0F, -2.2857F, -6.4143F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(46, 14).addBox(-1.0F, -3.7857F, -1.9143F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(46, 48).addBox(-1.0F, -4.7857F, -0.9143F, 2.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(46, 0).addBox(-1.0F, -1.8857F, -2.4143F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(36, 41).addBox(-1.0F, -0.0857F, -2.4143F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(46, 7).addBox(-1.0F, 1.9143F, -1.9143F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(16, 49).addBox(-1.0F, 3.9143F, -1.0143F, 2.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.7143F, 20.1143F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(SculkSnapperEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        this.animateWalk(SculkSnapperAnimations.SWIMMING, limbSwing, limbSwingAmount, 1f, 1.0f);
        this.animate(entity.idleAnimationState, SculkSnapperAnimations.IDLE, ageInTicks, 1f);
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
