package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.ronm19.sculky.entity.custom.SculkBeetleEntity;
import org.jetbrains.annotations.NotNull;

public class SculkBeetleModel<T extends SculkBeetleEntity> extends HierarchicalModel<T> {

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;

    // 3 legs per side (keep your names)
    private final ModelPart leg1; // left-front
    private final ModelPart leg2; // left-mid
    private final ModelPart leg3; // left-back
    private final ModelPart leg4; // right-front
    private final ModelPart leg5; // right-mid
    private final ModelPart leg6; // right-back

    public SculkBeetleModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.leg1 = root.getChild("leg1");
        this.leg2 = root.getChild("leg2");
        this.leg3 = root.getChild("leg3");
        this.leg4 = root.getChild("leg4");
        this.leg5 = root.getChild("leg5");
        this.leg6 = root.getChild("leg6");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // --- HEAD (your original boxes kept) ---
        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(0, 17).addBox(-5.0F, -3.1F, -7.7F, 6.0F, 3.0F, 5.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 25).addBox(0.7F, -2.2F, -11.7F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(32, 21).addBox(-3.8F, -1.2F, -9.7F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(32, 24).addBox(-2.4F, -1.2F, -9.7F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(32, 27).addBox(-1.0F, -1.2F, -9.7F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(22, 23).addBox(1.7F, -2.2F, -14.2F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(10, 25).addBox(-6.6F, -2.1F, -14.2F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 24.0F, 0.0F)
        );

        head.addOrReplaceChild("head_r1",
                CubeListBuilder.create().texOffs(22, 17)
                        .addBox(0.0F, -2.0F, -3.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-4.6F, -2.1F, -8.7F, 0.0F, 0.0F, -3.1329F)
        );

        // --- BODY (your original kept) ---
        root.addOrReplaceChild("body",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-7.0F, -5.1F, -2.9F, 10.0F, 5.0F, 12.0F, new CubeDeformation(0.0F))
                        .texOffs(24, 33).addBox(-5.0F, -7.1F, -1.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(28, 33).addBox(-5.0F, -7.1F, 1.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(8, 33).addBox(-5.0F, -7.1F, 4.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(12, 33).addBox(-5.0F, -7.1F, 7.1F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(4, 33).addBox(0.2F, -7.1F, 7.1F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 33).addBox(0.2F, -7.1F, 4.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(20, 33).addBox(0.2F, -7.1F, 1.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(16, 33).addBox(0.2F, -7.1F, -1.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 24.0F, 0.0F)
        );

        // --- LEGS (FIXED: each leg gets its own geometry) ---
        // simple 8x2x2 “paddle leg”, angled in code for a beetle look
        CubeListBuilder legBoxLeft  = CubeListBuilder.create().texOffs(14, 29).addBox(0.0F, -1.0F, -1.0F, 8.0F, 2.0F, 2.0F);
        CubeListBuilder legBoxRight = CubeListBuilder.create().texOffs(14, 29).addBox(-8.0F, -1.0F, -1.0F, 8.0F, 2.0F, 2.0F);

        // these pivots decide where legs attach to the body
        root.addOrReplaceChild("leg1", legBoxLeft,  PartPose.offset(2.5F, 23.4F, -1.6F)); // left-front
        root.addOrReplaceChild("leg2", legBoxLeft,  PartPose.offset(2.5F, 23.4F,  1.0F)); // left-mid
        root.addOrReplaceChild("leg3", legBoxLeft,  PartPose.offset(2.5F, 23.4F,  3.6F)); // left-back

        root.addOrReplaceChild("leg4", legBoxRight, PartPose.offset(-6.5F, 23.4F, -1.6F)); // right-front
        root.addOrReplaceChild("leg5", legBoxRight, PartPose.offset(-6.5F, 23.4F,  1.0F)); // right-mid
        root.addOrReplaceChild("leg6", legBoxRight, PartPose.offset(-6.5F, 23.4F,  3.6F)); // right-back

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(SculkBeetleEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {

        // Reset exported pose every frame
        this.root().getAllParts().forEach(ModelPart::resetPose);

        // ----- HEAD -----
        float yaw = Mth.clamp(netHeadYaw, -30.0F, 30.0F) * Mth.DEG_TO_RAD;
        float pitch = Mth.clamp(headPitch, -20.0F, 25.0F) * Mth.DEG_TO_RAD;

        this.head.yRot = yaw * 0.75F;
        this.head.xRot = pitch * 0.75F;

        float idle = ageInTicks * 0.12F;
        this.head.xRot += Mth.sin(idle) * 0.04F;
        this.head.zRot += Mth.sin(idle * 0.7F) * 0.02F;

        // ----- WALK -----
        float amp = Mth.clamp(limbSwingAmount, 0.0F, 1.0F);

        // Vanilla spider-ish timing
        float speed = 0.6662F;
        float t2 = limbSwing * speed * 2.0F; // faster phase like spider
        float t1 = limbSwing * speed;

        // “stance” (this is what makes it feel spider-like)
        float baseZ = 0.55F;     // wider = more spider
        float baseYFront = 0.55F;
        float baseYBack  = 0.45F;

        // Mapping assumed:
        // leg1/2/3 = left front/mid/back
        // leg4/5/6 = right front/mid/back

        // Base spread (zRot)
        leg1.zRot += +baseZ;      leg4.zRot += -baseZ;
        leg2.zRot += +baseZ*0.9F; leg5.zRot += -baseZ*0.9F;
        leg3.zRot += +baseZ*0.8F; leg6.zRot += -baseZ*0.8F;

        // Base yaw so legs don’t look “flat”
        leg1.yRot += +baseYFront; leg4.yRot += -baseYFront;
        leg2.yRot += 0.0F;        leg5.yRot += 0.0F;
        leg3.yRot += -baseYBack;  leg6.yRot += +baseYBack;

        // Swing amounts
        float ampY = 0.70F * amp; // yaw skitter
        float ampZ = 0.55F * amp; // zRot “step lift” feel

        // 3 phases for 3 pairs
        float a0 = -(Mth.cos(t2 + 0.0F) * 0.4F) * ampY;
        float a1 = -(Mth.cos(t2 + Mth.PI) * 0.4F) * ampY;
        float a2 = -(Mth.cos(t2 + (Mth.PI / 2F)) * 0.4F) * ampY;

        float b0 = Math.abs(Mth.sin(t1 + 0.0F) * 0.4F) * ampZ;
        float b1 = Math.abs(Mth.sin(t1 + Mth.PI) * 0.4F) * ampZ;
        float b2 = Math.abs(Mth.sin(t1 + (Mth.PI / 2F)) * 0.4F) * ampZ;

        // Pair 1 (front)
        leg1.yRot += a0;  leg4.yRot -= a0;
        leg1.zRot += b0;  leg4.zRot -= b0;

        // Pair 2 (mid)
        leg2.yRot += a1;  leg5.yRot -= a1;
        leg2.zRot += b1;  leg5.zRot -= b1;

        // Pair 3 (back)
        leg3.yRot += a2;  leg6.yRot -= a2;
        leg3.zRot += b2;  leg6.zRot -= b2;

        // Tiny body bob (keep SMALL)
        this.body.y += Mth.sin(t1) * amp * 0.15F;
    }



    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay, int color) {
        // render the whole hierarchy (cleaner)
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public @NotNull ModelPart root() {
        return this.root;
    }
}
