package net.ronm19.sculky.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.AbstractZombieRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.SculkZombieEntity;
import org.jetbrains.annotations.NotNull;

public class SculkZombieRenderer extends AbstractZombieRenderer<SculkZombieEntity, ZombieModel<SculkZombieEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, "textures/entity/sculk_zombie/sculk_zombie.png");


    public SculkZombieRenderer( EntityRendererProvider.Context context ) {
        this(context, ModelLayers.ZOMBIE, ModelLayers.ZOMBIE_INNER_ARMOR, ModelLayers.ZOMBIE_OUTER_ARMOR);
    }

    public SculkZombieRenderer( EntityRendererProvider.Context context, ModelLayerLocation zombieLayer, ModelLayerLocation innerArmor, ModelLayerLocation outerArmor ) {
        super(context, new ZombieModel<>(context.bakeLayer(zombieLayer)), new ZombieModel<>(context.bakeLayer(innerArmor)), new ZombieModel<>(context.bakeLayer(outerArmor)));
    }


    @Override
    public @NotNull ResourceLocation getTextureLocation( @NotNull SculkZombieEntity sculkZombieEntity ) {
        return TEXTURE;
    }

    @Override
    public void render( SculkZombieEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight ) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
