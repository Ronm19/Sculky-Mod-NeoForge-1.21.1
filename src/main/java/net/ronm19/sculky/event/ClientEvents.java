package net.ronm19.sculky.event;

import net.minecraft.world.level.portal.PortalForcer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;
import net.ronm19.sculky.client.sound.ShadowPantherSoundHandler;

@EventBusSubscriber(value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onClientTick( ClientTickEvent.Post event ) {
        ShadowPantherSoundHandler.tick();
    }
}