package net.ronm19.sculky.sounds;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.ronm19.sculky.SculkyMod;

import java.util.function.Supplier;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, SculkyMod.MOD_ID);


    private static Supplier<SoundEvent> registerSoundEvent( String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register( IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
