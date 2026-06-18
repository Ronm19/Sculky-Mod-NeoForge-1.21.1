package net.ronm19.sculky.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.util.Mth;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.ronm19.sculky.client.sound.ShadowPantherSoundHandler;
import net.ronm19.sculky.entity.custom.SculkDolphinEntity;
import net.ronm19.sculky.item.ModItems;
import net.ronm19.sculky.item.SculkyItemProperties;
import net.ronm19.sculky.network.SculkDolphinInputPayload;

@EventBusSubscriber(value = Dist.CLIENT)
public class ModClientEvents {

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

    @SubscribeEvent
    public static void onComputeFovModifier( ComputeFovModifierEvent event) {
        if (event.getPlayer().isUsingItem()
                && event.getPlayer().getUseItem().is(ModItems.SCULK_BOW.get())) {

            float fovModifier = 1.0F;
            int ticksUsingItem = event.getPlayer().getTicksUsingItem();
            float scale = Math.min(ticksUsingItem / 20.0F, 1.0F);

            // Same vanilla-style pull-in feeling.
            fovModifier *= 1.0F - Mth.square(scale) * 0.15F;

            event.setNewFovModifier(Mth.lerp(
                    Minecraft.getInstance().options.fovEffectScale().get().floatValue(),
                    1.0F,
                    fovModifier
            ));
        }
    }

    @SubscribeEvent
    public static void onClientSetup( FMLClientSetupEvent event) {
        event.enqueueWork(SculkyItemProperties ::register);
    }
}