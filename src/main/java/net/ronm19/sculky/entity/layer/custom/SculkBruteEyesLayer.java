package net.ronm19.sculky.entity.layer.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.client.SculkBruteModel;
import net.ronm19.sculky.entity.custom.SculkBruteEntity;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class SculkBruteEyesLayer extends EyesLayer<SculkBruteEntity, SculkBruteModel<SculkBruteEntity>> {

    private static final RenderType EYES = RenderType.eyes(ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculk_brute/sculk_brute_eyes.png")
    );

    public SculkBruteEyesLayer(RenderLayerParent<SculkBruteEntity, SculkBruteModel<SculkBruteEntity>> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                       SculkBruteEntity entity, float limbSwing, float limbSwingAmount,
                       float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {

        if (!entity.shouldGlowEyes()) {
            return;
        }

        super.render(poseStack, buffer, packedLight, entity, limbSwing, limbSwingAmount,
                partialTick, ageInTicks, netHeadYaw, headPitch);
    }

    @Override
    public RenderType renderType() {
        return EYES;
    }
}
