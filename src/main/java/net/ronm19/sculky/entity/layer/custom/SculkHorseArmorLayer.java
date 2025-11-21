package net.ronm19.sculky.entity.layer.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.ronm19.sculky.entity.custom.SculkHorseEntity;

@OnlyIn(Dist.CLIENT)
public class SculkHorseArmorLayer extends RenderLayer<SculkHorseEntity, HorseModel<SculkHorseEntity>> {
    private final HorseModel<SculkHorseEntity> model;

    public SculkHorseArmorLayer( RenderLayerParent<SculkHorseEntity, HorseModel<SculkHorseEntity>> renderer, EntityModelSet modelSet) {
        super(renderer);
        this.model = new HorseModel<>(modelSet.bakeLayer(ModelLayers.HORSE_ARMOR));
    }

    public void render( PoseStack poseStack, MultiBufferSource buffer, int packedLight, SculkHorseEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack itemstack = livingEntity.getBodyArmorItem();
        Item var13 = itemstack.getItem();
        if (var13 instanceof AnimalArmorItem animalarmoritem) {
            if (animalarmoritem.getBodyType() == AnimalArmorItem.BodyType.EQUESTRIAN) {
                ((HorseModel)this.getParentModel()).copyPropertiesTo(this.model);
                this.model.prepareMobModel(livingEntity, limbSwing, limbSwingAmount, partialTicks);
                this.model.setupAnim(livingEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
                int i;
                if (itemstack.is(ItemTags.DYEABLE)) {
                    i = FastColor.ARGB32.opaque(DyedItemColor.getOrDefault(itemstack, -6265536));
                } else {
                    i = -1;
                }

                VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(animalarmoritem.getTexture()));
                this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, i);
                return;
            }
        }

    }
}