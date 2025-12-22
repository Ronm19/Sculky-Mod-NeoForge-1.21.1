package net.ronm19.sculky.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.ModEntities;
import net.ronm19.sculky.entity.custom.SculkHorseEntity;
import net.ronm19.sculky.util.ModTags;

@EventBusSubscriber(modid = SculkyMod.MOD_ID)
public class LightningTransformHandler {

    @SubscribeEvent
    public static void onEntityStruck(EntityStruckByLightningEvent event) {

        Entity entity = event.getEntity();
        if (!(entity instanceof Horse horse)) return;

        if (!(horse.level() instanceof ServerLevel level)) return;

        BlockState below = level.getBlockState(horse.blockPosition().below());
        if (!below.is(ModTags.Blocks.SCULK_TRANSFORMABLE)) return;

        // Prevent double transform
        if (horse.isRemoved()) return;

        SculkHorseEntity sculkHorse = ModEntities.SCULK_HORSE.get().create(level);
        if (sculkHorse == null) return;

        sculkHorse.moveTo(
                horse.getX(),
                horse.getY(),
                horse.getZ(),
                horse.getYRot(),
                horse.getXRot()
        );

        if (horse.isTamed() && horse.getOwner() instanceof net.minecraft.world.entity.player.Player owner) {
            sculkHorse.tame(owner);
        }

        sculkHorse.setHealth(horse.getHealth());

        if (horse.hasCustomName()) {
            sculkHorse.setCustomName(horse.getCustomName());
            sculkHorse.setCustomNameVisible(horse.isCustomNameVisible());
        }

        level.addFreshEntity(sculkHorse);
        horse.discard();

        level.sendParticles(
                net.minecraft.core.particles.ParticleTypes.SCULK_SOUL,
                sculkHorse.getX(),
                sculkHorse.getY() + 1,
                sculkHorse.getZ(),
                40, 0.5, 0.5, 0.5, 0.1);



    }
}
