package net.ronm19.sculky.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

public class ModKeyMappings {

    public static final KeyMapping USE_ABILITY = new KeyMapping(
            "key.sculky.use_ability",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "key.categories.sculky"
    );

    @SubscribeEvent
    public static void register( RegisterKeyMappingsEvent event) {
        event.register(USE_ABILITY);
    }
}