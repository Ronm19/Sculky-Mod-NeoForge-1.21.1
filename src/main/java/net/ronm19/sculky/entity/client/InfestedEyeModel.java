package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.ronm19.sculky.entity.animation.InfestedEyeAnimations;
import net.ronm19.sculky.entity.animation.SculkRatAnimations;
import net.ronm19.sculky.entity.custom.InfestedEyeEntity;
import org.jetbrains.annotations.NotNull;

public class InfestedEyeModel <T extends InfestedEyeEntity> extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart halo;
    private final ModelPart body;

    public InfestedEyeModel(ModelPart root) {
        this.root = root;
        this.halo = root.getChild("halo");
        this.body = root.getChild("body");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition halo = partdefinition.addOrReplaceChild("halo", CubeListBuilder.create().texOffs(0, 18).addBox(-4.5F, -0.5F, -4.0F, 9.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.6F, -2.5F, 3.4F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, -4.5F, -4.5F, 9.0F, 9.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, 4.5F, 3.5F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(@NotNull InfestedEyeEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        this.animateWalk(InfestedEyeAnimations.flying, limbSwing, limbSwingAmount, 1f, 1.5f);
        this.animate(entity.idleAnimationState, InfestedEyeAnimations.idle, 1F);
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        halo.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public @NotNull ModelPart root() {
        return this.root;
    }
}
