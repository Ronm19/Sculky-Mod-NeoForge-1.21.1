package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.ronm19.sculky.entity.animation.HollowhornAnimations;
import net.ronm19.sculky.entity.animation.SalvatoreAnimations;
import net.ronm19.sculky.entity.custom.SalvatoreEntity;

public class SalvatoreModel <T  extends SalvatoreEntity> extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart cape;
    private final ModelPart r_arm;
    private final ModelPart l_arm;
    private final ModelPart right_leg;
    private final ModelPart left_leg;
    private final ModelPart sculk_edge;
    private final ModelPart blade;
    private final ModelPart body2;
    private final ModelPart handle;

    public SalvatoreModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.cape = root.getChild("cape");
        this.r_arm = root.getChild("r_arm");
        this.l_arm = root.getChild("l_arm");
        this.right_leg = root.getChild("right_leg");
        this.left_leg = root.getChild("left_leg");
        this.sculk_edge = root.getChild("sculk_edge");
        this.blade = this.sculk_edge.getChild("blade");
        this.body2 = this.sculk_edge.getChild("body2");
        this.handle = this.sculk_edge.getChild("handle");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(47, 48).addBox(-3.5854F, -1.7122F, -3.43F, 7.0F, 6.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(72, 87).addBox(-1.5854F, 3.5378F, -2.43F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.3146F, -10.8878F, 2.58F));

        PartDefinition head_r1 = head.addOrReplaceChild("head_r1", CubeListBuilder.create().texOffs(60, 92).addBox(-3.0F, -3.0F, 0.0F, 6.0F, 6.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(93, 16).addBox(-2.0F, -4.0F, 0.3F, 6.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0646F, 1.7878F, 2.42F, 0.0F, 0.0F, -1.5708F));

        PartDefinition head_r2 = head.addOrReplaceChild("head_r2", CubeListBuilder.create().texOffs(30, 71).addBox(-0.5F, -3.0F, -3.0F, 1.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0646F, -4.4622F, -0.58F, 0.0F, 0.0F, -1.5708F));

        PartDefinition head_r3 = head.addOrReplaceChild("head_r3", CubeListBuilder.create().texOffs(16, 71).addBox(-0.5F, -3.0F, -3.0F, 1.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0646F, -2.2622F, -0.58F, 0.0F, 0.0F, -1.5708F));

        PartDefinition head_r4 = head.addOrReplaceChild("head_r4", CubeListBuilder.create().texOffs(12, 83).addBox(-0.5F, -2.0F, -3.0F, 1.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.1139F, -0.0761F, -0.58F, 0.0F, 0.0F, -0.3403F));

        PartDefinition head_r5 = head.addOrReplaceChild("head_r5", CubeListBuilder.create().texOffs(84, 46).addBox(-0.5F, -1.5F, -3.0F, 1.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.9593F, 2.8491F, -0.58F, 0.0F, 0.0F, 0.576F));

        PartDefinition head_r6 = head.addOrReplaceChild("head_r6", CubeListBuilder.create().texOffs(82, 59).addBox(-0.5F, -2.5F, -3.0F, 1.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.3714F, 3.5162F, -0.58F, 0.0F, 0.0F, -0.6894F));

        PartDefinition head_r7 = head.addOrReplaceChild("head_r7", CubeListBuilder.create().texOffs(26, 83).addBox(-0.5F, -2.0F, -3.0F, 1.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.8896F, -0.2661F, -0.58F, 0.0F, 0.0F, 0.4451F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(22, 0).addBox(-5.5047F, -8.3281F, -2.0861F, 11.0F, 7.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(0, 32).addBox(-3.5047F, -1.3281F, -2.0861F, 7.0F, 7.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(0, 26).addBox(-4.5047F, 0.6719F, -3.0861F, 8.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(18, 58).addBox(-1.5047F, 0.1719F, -4.0861F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 29).addBox(-3.5047F, 0.6719F, 2.9139F, 7.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(86, 0).addBox(-4.2547F, 0.6719F, -2.0861F, 1.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(44, 78).addBox(3.4953F, 0.6719F, -3.0861F, 1.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.4953F, 2.2281F, 1.8361F));

        PartDefinition body_r1 = body.addOrReplaceChild("body_r1", CubeListBuilder.create().texOffs(72, 35).addBox(-3.5F, -5.5F, 0.0F, 7.0F, 11.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.697F, 8.1708F, 0.3192F, -3.0606F, -1.498F, -3.1273F));

        PartDefinition body_r2 = body.addOrReplaceChild("body_r2", CubeListBuilder.create().texOffs(40, 87).addBox(-2.5F, -5.5F, 0.0F, 5.0F, 11.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.0742F, 8.1635F, -3.7139F, 3.1343F, -0.0142F, -3.1332F));

        PartDefinition body_r3 = body.addOrReplaceChild("body_r3", CubeListBuilder.create().texOffs(72, 24).addBox(-3.5F, -5.5F, 0.0F, 7.0F, 11.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.0742F, 8.1635F, 4.0861F, 3.1343F, -0.0142F, -3.1332F));

        PartDefinition body_r4 = body.addOrReplaceChild("body_r4", CubeListBuilder.create().texOffs(72, 0).addBox(-3.5F, -5.5F, 0.0F, 7.0F, 11.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.553F, 8.1708F, 0.3192F, 3.0568F, -1.498F, -3.1273F));

        PartDefinition body_r5 = body.addOrReplaceChild("body_r5", CubeListBuilder.create().texOffs(40, 12).addBox(-2.5F, -1.0F, -3.5F, 5.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.8425F, -7.4184F, -0.2361F, 0.0F, 0.0F, -0.5934F));

        PartDefinition body_r6 = body.addOrReplaceChild("body_r6", CubeListBuilder.create().texOffs(40, 21).addBox(-2.5F, -1.0F, -3.5F, 5.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5586F, -7.5768F, -0.2361F, 0.0F, 0.0F, -2.4958F));

        PartDefinition body_r7 = body.addOrReplaceChild("body_r7", CubeListBuilder.create().texOffs(23, 31).addBox(-2.5F, -1.0F, -4.5F, 5.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.1914F, -6.5768F, 0.6639F, 0.0F, 0.0F, -3.1416F));

        PartDefinition cape = partdefinition.addOrReplaceChild("cape", CubeListBuilder.create(), PartPose.offset(-0.5F, 5.721F, 7.3331F));

        PartDefinition cape_r1 = cape.addOrReplaceChild("cape_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-5.5F, -13.0F, 0.0F, 11.0F, 26.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.2F, 0.7F, 0.1571F, 0.0F, 0.0F));

        PartDefinition r_arm = partdefinition.addOrReplaceChild("r_arm", CubeListBuilder.create().texOffs(24, 41).addBox(-3.3962F, -6.8385F, -3.5962F, 5.0F, 2.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(68, 48).addBox(1.5038F, -5.8385F, -3.5962F, 1.0F, 4.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(0, 44).addBox(-3.3962F, -4.8385F, -3.5962F, 5.0F, 2.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(0, 58).addBox(-2.3962F, -2.8385F, -2.5962F, 4.0F, 7.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(54, 0).addBox(-2.3962F, 4.1615F, -2.5962F, 4.0F, 7.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(78, 68).addBox(-2.3962F, 11.1615F, -2.5962F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(90, 75).addBox(-2.3962F, 4.1615F, -3.5962F, 4.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(92, 8).addBox(-2.3962F, 4.1615F, 2.2038F, 4.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(78, 75).addBox(1.3538F, 4.1615F, -2.5962F, 1.0F, 7.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(0, 81).addBox(-3.3462F, 4.1615F, -2.5962F, 1.0F, 7.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(8.3962F, 0.7385F, 2.5962F));

        PartDefinition r_arm_r1 = r_arm.addOrReplaceChild("r_arm_r1", CubeListBuilder.create().texOffs(72, 80).addBox(-0.5F, -1.5F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.7538F, -6.3385F, 0.6538F, 0.0F, 0.0F, 0.6109F));

        PartDefinition r_arm_r2 = r_arm.addOrReplaceChild("r_arm_r2", CubeListBuilder.create().texOffs(18, 26).addBox(-0.5F, -1.5F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0538F, -7.8385F, 0.6538F, 0.0F, 0.0F, 0.2182F));

        PartDefinition r_arm_r3 = r_arm.addOrReplaceChild("r_arm_r3", CubeListBuilder.create().texOffs(38, 62).addBox(-0.5F, -1.5F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.2462F, -7.8385F, 0.6538F, 0.0F, 0.0F, 0.0087F));

        PartDefinition l_arm = partdefinition.addOrReplaceChild("l_arm", CubeListBuilder.create().texOffs(48, 30).addBox(-1.5321F, -6.9548F, -4.3571F, 5.0F, 2.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(0, 70).addBox(-2.5321F, -5.9548F, -4.3571F, 1.0F, 4.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(48, 39).addBox(-1.5321F, -4.9548F, -4.3571F, 5.0F, 2.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(44, 59).addBox(-1.4071F, -3.3714F, -3.1571F, 4.0F, 7.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(26, 50).addBox(-1.4071F, 3.6286F, -3.1571F, 4.0F, 7.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(44, 71).addBox(-1.4071F, 10.6786F, -3.2571F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(50, 87).addBox(-1.4071F, 3.6286F, -4.1571F, 4.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(86, 87).addBox(-1.4071F, 3.6286F, 1.8429F, 4.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(80, 11).addBox(-2.4071F, 3.6286F, -3.1571F, 1.0F, 7.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(60, 80).addBox(2.5929F, 3.6286F, -3.1571F, 1.0F, 7.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-9.4679F, 1.1048F, 4.1071F));

        PartDefinition l_arm_r1 = l_arm.addOrReplaceChild("l_arm_r1", CubeListBuilder.create().texOffs(44, 50).addBox(-0.5F, -1.5F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.4179F, -8.4548F, -0.8571F, -3.1359F, 0.1308F, -3.0976F));

        PartDefinition l_arm_r2 = l_arm.addOrReplaceChild("l_arm_r2", CubeListBuilder.create().texOffs(44, 54).addBox(-0.5F, -1.5F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.7321F, -8.2048F, -0.8571F, 3.1244F, 0.1298F, 3.0096F));

        PartDefinition l_arm_r3 = l_arm.addOrReplaceChild("l_arm_r3", CubeListBuilder.create().texOffs(38, 66).addBox(-0.5F, -1.5F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.8321F, -6.8548F, -0.8571F, 3.0691F, 0.1091F, 2.5529F));

        PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(64, 12).addBox(-1.5417F, -9.1333F, -2.125F, 3.0F, 7.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(86, 23).addBox(-1.7917F, -2.1333F, -1.125F, 3.0F, 7.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(64, 24).addBox(-1.7917F, -0.1333F, 1.375F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(96, 55).addBox(-1.7917F, -0.1333F, -1.875F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(72, 94).addBox(0.7083F, -0.1333F, -1.125F, 1.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(18, 62).addBox(-1.7917F, 4.8667F, -5.125F, 3.0F, 2.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(88, 95).addBox(-2.1417F, -0.1333F, -1.125F, 1.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(2.1417F, 17.0333F, 1.825F));

        PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(62, 68).addBox(-2.1438F, -9.3F, -2.2187F, 3.0F, 7.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(86, 33).addBox(-1.8938F, -2.3F, -1.2187F, 3.0F, 7.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(96, 65).addBox(-1.8938F, -0.3F, -1.9687F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(96, 60).addBox(-1.8938F, -0.3F, 1.2813F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(62, 59).addBox(-1.8938F, 4.7F, -5.2188F, 3.0F, 2.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(50, 95).addBox(0.6062F, -0.3F, -1.2187F, 1.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(80, 95).addBox(-2.3938F, -0.3F, -1.2187F, 1.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.5062F, 17.2F, 1.9187F));

        PartDefinition sculk_edge = partdefinition.addOrReplaceChild("sculk_edge", CubeListBuilder.create(), PartPose.offsetAndRotation(-8.525F, 12.9667F, 1.1F, 1.5708F, 0.0436F, -1.5708F));

        PartDefinition blade = sculk_edge.addOrReplaceChild("blade", CubeListBuilder.create().texOffs(0, 93).addBox(1.5F, -23.0F, -2.3F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(10, 93).addBox(-0.3F, -23.0F, -2.3F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(20, 93).addBox(-2.1F, -23.0F, -2.3F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.175F, 3.0333F, 0.2F));

        PartDefinition body2 = sculk_edge.addOrReplaceChild("body2", CubeListBuilder.create().texOffs(0, 53).addBox(-4.475F, 3.65F, -2.0F, 8.9F, 1.2F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(22, 12).addBox(-2.275F, -12.15F, -2.0F, 4.6F, 15.8F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.8167F, -0.1F));

        PartDefinition handle = sculk_edge.addOrReplaceChild("handle", CubeListBuilder.create().texOffs(30, 93).addBox(-0.85F, -3.75F, -0.8F, 1.7F, 7.5F, 1.6F, new CubeDeformation(0.0F)), PartPose.offset(0.175F, 2.7833F, -0.1F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(SalvatoreEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.applyHeadRotation(netHeadYaw, headPitch);

        this.animateWalk(SalvatoreAnimations.walking, limbSwing, limbSwingAmount, 1f, 1f);
        this.animate(entity.idleAnimationState, SalvatoreAnimations.idle, ageInTicks, 1f);
        this.animate(entity.attackAnimationState, SalvatoreAnimations.attack, ageInTicks, 1f);
    }

    private void applyHeadRotation(float pNetHeadYaw, float pHeadPitch) {
        pNetHeadYaw = Mth.clamp(pNetHeadYaw, -30.0F, 30.0F);
        pHeadPitch = Mth.clamp(pHeadPitch, -25.0F, 45.0F);

        this.head.yRot = pNetHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot = pHeadPitch * ((float)Math.PI / 180F);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        cape.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        r_arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        l_arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        right_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        left_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        sculk_edge.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}
