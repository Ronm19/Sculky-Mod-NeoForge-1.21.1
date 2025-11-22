package net.ronm19.sculky.entity.layer.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.Crackiness;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.ronm19.sculky.entity.client.SculkWolfModel;
import net.ronm19.sculky.entity.custom.SculkWolfEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class SculkWolfArmorLayer extends RenderLayer<SculkWolfEntity, SculkWolfModel<SculkWolfEntity>> {
    public final SculkWolfModel<SculkWolfEntity> model;
    public static final Map<Crackiness.Level, ResourceLocation> ARMOR_CRACK_LOCATIONS;

    public SculkWolfArmorLayer( RenderLayerParent<SculkWolfEntity, SculkWolfModel<SculkWolfEntity>> renderer, EntityModelSet models) {
        super(renderer);
        this.model = new SculkWolfModel<>(models.bakeLayer(ModelLayers.WOLF_ARMOR));
    }

    public void render( @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight, SculkWolfEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (livingEntity.hasArmor()) {
            ItemStack itemstack = livingEntity.getBodyArmorItem();
            Item var13 = itemstack.getItem();
            if (var13 instanceof AnimalArmorItem animalarmoritem) {
                if (animalarmoritem.getBodyType() == AnimalArmorItem.BodyType.CANINE) {
                    ((SculkWolfModel)this.getParentModel()).copyPropertiesTo(this.model);
                    this.model.prepareMobModel(livingEntity, limbSwing, limbSwingAmount, partialTick);
                    this.model.setupAnim(livingEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
                    VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(animalarmoritem.getTexture()));
                    this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
                    this.maybeRenderColoredLayer(poseStack, bufferSource, packedLight, itemstack, animalarmoritem);
                    this.maybeRenderCracks(poseStack, bufferSource, packedLight, itemstack);
                    return;
                }
            }
        }

    }

    public void maybeRenderColoredLayer(PoseStack poseStack, MultiBufferSource buffer, int packedLight, ItemStack armorStack, AnimalArmorItem armorItem) {
        if (armorStack.is(ItemTags.DYEABLE)) {
            int i = DyedItemColor.getOrDefault(armorStack, 0);
            if (FastColor.ARGB32.alpha(i) == 0) {
                return;
            }

            ResourceLocation resourcelocation = armorItem.getOverlayTexture();
            if (resourcelocation == null) {
                return;
            }

            this.model.renderToBuffer(poseStack, buffer.getBuffer(RenderType.entityCutoutNoCull(resourcelocation)), packedLight, OverlayTexture.NO_OVERLAY, FastColor.ARGB32.opaque(i));
        }

    }

    public void maybeRenderCracks(PoseStack poseStack, MultiBufferSource buffer, int packedLight, ItemStack armorStack) {
        Crackiness.Level crackiness$level = Crackiness.WOLF_ARMOR.byDamage(armorStack);
        if (crackiness$level != Crackiness.Level.NONE) {
            ResourceLocation resourcelocation = (ResourceLocation)ARMOR_CRACK_LOCATIONS.get(crackiness$level);
            VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.entityTranslucent(resourcelocation));
            this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
        }

    }

    static {
        ARMOR_CRACK_LOCATIONS = Map.of(Crackiness.Level.LOW, ResourceLocation.withDefaultNamespace("textures/entity/wolf/wolf_armor_crackiness_low.png"), Crackiness.Level.MEDIUM, ResourceLocation.withDefaultNamespace("textures/entity/wolf/wolf_armor_crackiness_medium.png"), Crackiness.Level.HIGH, ResourceLocation.withDefaultNamespace("textures/entity/wolf/wolf_armor_crackiness_high.png"));
    }
}
