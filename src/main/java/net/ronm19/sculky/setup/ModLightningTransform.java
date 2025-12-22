package net.ronm19.sculky.setup;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.api.LightningTransformRegistry;
import net.ronm19.sculky.api.interfaces.LightningTransform;

@EventBusSubscriber(modid = SculkyMod.MOD_ID)
public class ModLightningTransform {

    @SubscribeEvent
    public static void onStruck(EntityStruckByLightningEvent event) {
        Entity entity = event.getEntity();

        if (!(entity.level() instanceof ServerLevel level)) return;

        LightningTransform transform =
                LightningTransformRegistry.get(entity.getType());

        if (transform == null) return;
        if (!transform.canTransform(entity, level)) return;

        transform.transform(entity, level, (LightningBolt) event.getLightning());
    }
}
