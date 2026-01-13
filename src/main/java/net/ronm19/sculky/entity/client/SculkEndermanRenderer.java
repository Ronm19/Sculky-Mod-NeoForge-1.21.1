package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EnderEyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.SculkEndermanEntity;
import net.ronm19.sculky.entity.layer.custom.SculkEndermanCarriedBlockLayer;
import net.ronm19.sculky.entity.layer.custom.SculkEndermanEyesLayers;
import net.ronm19.sculky.entity.variant.CorruptedSculkEndermanVariant;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

public class SculkEndermanRenderer extends MobRenderer<SculkEndermanEntity, EndermanModel<SculkEndermanEntity>> {
    private final RandomSource random = RandomSource.create();

    private static final Map<CorruptedSculkEndermanVariant, ResourceLocation> TEXTURE_BY_VARIANT =
            Util.make(new EnumMap<>(CorruptedSculkEndermanVariant.class), map -> {
                map.put(CorruptedSculkEndermanVariant.NORMAL, ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculk_enderman/sculk_enderman.png"));

                map.put(CorruptedSculkEndermanVariant.CORRUPTED, ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculk_enderman/sculk_enderman_corrupted.png"));
            });


    public SculkEndermanRenderer(EntityRendererProvider.Context context) {
        super(context, new EndermanModel<>(context.bakeLayer(ModelLayers.ENDERMAN)), 0.5F);
        this.addLayer(new SculkEndermanEyesLayers<>(this));
        this.addLayer(new SculkEndermanCarriedBlockLayer(this, context.getBlockRenderDispatcher()));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(SculkEndermanEntity sculkEndermanEntity) {
        return TEXTURE_BY_VARIANT.getOrDefault(sculkEndermanEntity.getVariant(), TEXTURE_BY_VARIANT.get(CorruptedSculkEndermanVariant.NORMAL));
    }

    public void render(SculkEndermanEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        BlockState blockstate = entity.getCarriedBlock();
        EndermanModel<SculkEndermanEntity> endermanmodel = (EndermanModel)this.getModel();
        endermanmodel.carrying = blockstate != null;
        endermanmodel.creepy = entity.isCreepy();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    public @NotNull Vec3 getRenderOffset(SculkEndermanEntity entity, float partialTicks) {
        if (entity.isCreepy()) {
            double d0 = 0.02 * (double)entity.getScale();
            return new Vec3(this.random.nextGaussian() * d0, (double)0.0F, this.random.nextGaussian() * d0);
        } else {
            return super.getRenderOffset(entity, partialTicks);
        }
    }
}
