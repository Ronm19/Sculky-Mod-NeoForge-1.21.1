package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.ronm19.sculky.entity.animation.SculkHorrorAnimations;
import net.ronm19.sculky.entity.custom.SculkHorrorEntity;
import org.jetbrains.annotations.NotNull;

public class SculkHorrorModel <T extends SculkHorrorEntity> extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart neck;
    private final ModelPart body;

    public SculkHorrorModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.neck = root.getChild("neck");
        this.body = root.getChild("body");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -7.7F, -23.3F, 7.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 25.8F, 0.0F));

        PartDefinition neck = partdefinition.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(40, 20).addBox(-2.5F, -2.5F, -1.0F, 5.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, 21.3F, -17.0F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 12).addBox(-4.0F, -7.0F, -1.0F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(0, 22).addBox(-4.0F, -7.0F, -6.0F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(20, 22).addBox(-4.0F, -7.0F, -11.0F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(26, 0).addBox(-4.0F, -7.0F, -16.0F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(20, 32).addBox(-4.0F, -7.0F, 4.0F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(40, 10).addBox(-4.0F, -7.0F, 8.9F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(0, 32).addBox(-4.0F, -7.0F, 13.8F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(20, 12).addBox(-4.0F, -7.0F, 18.7F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 25.8F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim( @NotNull SculkHorrorEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        this.animateWalk(SculkHorrorAnimations.SCULK_HORROR_WALKING, limbSwing, limbSwingAmount, 2f, 2.5f);
    }

    @Override
    public void renderToBuffer( @NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        neck.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay);
    }

    @Override
    public @NotNull ModelPart root() {
        return this.root;
    }
}
