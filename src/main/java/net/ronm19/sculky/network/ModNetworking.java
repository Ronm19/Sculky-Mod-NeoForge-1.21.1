package net.ronm19.sculky.network;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.api.interfaces.AbilityUser;
import net.ronm19.sculky.network.UseAbilityPayload;

@EventBusSubscriber(modid = SculkyMod.MOD_ID, value = Dist.CLIENT)
public class ModNetworking {

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {

        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToServer(
                UseAbilityPayload.TYPE,
                UseAbilityPayload.STREAM_CODEC,
                ModNetworking::handleUseAbility
        );
    }

    private static void handleUseAbility(
            UseAbilityPayload payload,
            IPayloadContext context
    ) {
        context.enqueueWork(() -> {

            if (!(context.player() instanceof ServerPlayer player)) return;

            if (player.getVehicle() instanceof AbilityUser abilityUser) {
                abilityUser.useAbility(player);
            }
        });
    }
}
