package net.ronm19.sculky.client;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.network.ModNetworking;
import net.ronm19.sculky.network.UseAbilityPayload;

@EventBusSubscriber(modid = SculkyMod.MOD_ID, value = Dist.CLIENT)
public class ClientInputHandler {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {

        if (ModKeyMappings.USE_ABILITY.consumeClick()) {

            if (Minecraft.getInstance().player == null) return;

            Minecraft.getInstance()
                    .getConnection()
                    .send(new UseAbilityPayload());
        }
    }
}
