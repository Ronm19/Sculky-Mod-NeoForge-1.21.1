package net.ronm19.sculky.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.ronm19.sculky.api.interfaces.AbilityUser;
import net.ronm19.sculky.entity.custom.SculkDolphinEntity;

public class ModNetworking {

    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToServer(
                UseAbilityPayload.TYPE,
                UseAbilityPayload.STREAM_CODEC,
                ModNetworking::handleUseAbility
        );

        registrar.playToServer(
                SculkDolphinInputPayload.TYPE,
                SculkDolphinInputPayload.STREAM_CODEC,
                ModNetworking::handleSculkDolphinInput
        );
    }

    private static void handleUseAbility(UseAbilityPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            if (player.getVehicle() instanceof AbilityUser abilityUser) {
                abilityUser.useAbility(player);
            }
        });
    }

    private static void handleSculkDolphinInput(
            SculkDolphinInputPayload payload,
            IPayloadContext context
    ) {
        context.enqueueWork(() -> {
            Player player = context.player();
            Entity vehicle = player.getVehicle();

            if (vehicle instanceof SculkDolphinEntity dolphin
                    && dolphin.getId() == payload.entityId()
                    && dolphin.isOwnedBy(player)) {
                dolphin.applyRiderInput(
                        player,
                        payload.strafe(),
                        payload.forward(),
                        payload.jump()
                );
            }
        });
    }
}