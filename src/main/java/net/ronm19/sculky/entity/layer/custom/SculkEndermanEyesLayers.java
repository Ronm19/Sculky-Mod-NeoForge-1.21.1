package net.ronm19.sculky.entity.layer.custom;

import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.ronm19.sculky.SculkyMod;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class SculkEndermanEyesLayers<T extends LivingEntity> extends EyesLayer<T, EndermanModel<T>> {
    private static final RenderType ENDERMAN_EYES = RenderType.eyes(ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculk_enderman/sculk_enderman_eyes.png"));

    public SculkEndermanEyesLayers(RenderLayerParent<T, EndermanModel<T>> p_116964_) {
        super(p_116964_);
    }

    public @NotNull RenderType renderType() {
        return ENDERMAN_EYES;
    }
}
