package net.ronm19.sculky.entity.layer.custom;

import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EnergySwirlLayer;
import net.minecraft.resources.ResourceLocation;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.SculkCreeperEntity;
import org.jetbrains.annotations.NotNull;

public class SculkCreeperPowerLayer extends EnergySwirlLayer<SculkCreeperEntity, CreeperModel<SculkCreeperEntity>> {
    private static final ResourceLocation POWER_LOCATION = ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculk_creeper/sculk_creeper_charge.png");
    private final CreeperModel<SculkCreeperEntity> model;

    public SculkCreeperPowerLayer( RenderLayerParent<SculkCreeperEntity, CreeperModel<SculkCreeperEntity>> renderer, EntityModelSet modelSet) {
        super(renderer);
        this.model = new CreeperModel<>(modelSet.bakeLayer(ModelLayers.CREEPER_ARMOR));
    }

    protected float xOffset(float tickCount) {
        return tickCount * 0.01F;
    }

    protected @NotNull ResourceLocation getTextureLocation() {
        return POWER_LOCATION;
    }

    protected @NotNull EntityModel<SculkCreeperEntity> model() {
        return this.model;
    }
}
