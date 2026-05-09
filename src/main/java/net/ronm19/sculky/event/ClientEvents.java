package net.ronm19.sculky.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.world.level.portal.PortalForcer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.network.PacketDistributor;
import net.ronm19.sculky.client.sound.ShadowPantherSoundHandler;
import net.ronm19.sculky.entity.custom.SculkDolphinEntity;
import net.ronm19.sculky.network.SculkDolphinInputPayload;

@EventBusSubscriber(value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onClientTick( ClientTickEvent.Post event ) {
        ShadowPantherSoundHandler.tick();

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || minecraft.level == null) {
            return;
        }

        if (!(minecraft.player.getVehicle() instanceof SculkDolphinEntity dolphin)) {
            return;
        }

        Options options = minecraft.options;

        float forward = 0.0F;
        float strafe = 0.0F;

        if (options.keyUp.isDown()) {
            forward += 1.0F;
        }

        if (options.keyDown.isDown()) {
            forward -= 1.0F;
        }

        if (options.keyLeft.isDown()) {
            strafe += 1.0F;
        }

        if (options.keyRight.isDown()) {
            strafe -= 1.0F;
        }

        boolean jump = options.keyJump.isDown();

        PacketDistributor.sendToServer(
                new SculkDolphinInputPayload(
                        dolphin.getId(),
                        strafe,
                        forward,
                        jump
                )
        );
    }
}