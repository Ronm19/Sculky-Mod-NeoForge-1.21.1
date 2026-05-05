package net.ronm19.sculky.sounds;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.JukeboxSong;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.ronm19.sculky.SculkyMod;

import java.util.function.Supplier;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, SculkyMod.MOD_ID);

    public static final Supplier<SoundEvent> SHADOW_PANTHER_THEME = registerSoundEvent("shadow_panther_theme");
    public static final Supplier<SoundEvent> ECHOES_OF_THE_CROWN = registerSoundEvent("echoes_of_the_crown");

    public static final Supplier<SoundEvent> SHADOW_PANTHER_AMBIENT = registerSoundEvent("shadow_panther_ambient");
    public static final Supplier<SoundEvent> SHADOW_PANTHER_HURT = registerSoundEvent("shadow_panther_hurt");
    public static final Supplier<SoundEvent> SHADOW_PANTHER_DEATH = registerSoundEvent("shadow_panther_death");
    public static final Supplier<SoundEvent> SHADOW_PANTHER_STALK = registerSoundEvent("shadow_panther_stalk");
    public static final Supplier<SoundEvent> SHADOW_PANTHER_PRESSURE = registerSoundEvent("shadow_panther_pressure");
    public static final Supplier<SoundEvent> SHADOW_PANTHER_ATTACK_STINGER = registerSoundEvent("shadow_panther_attack_stinger");

    public static final ResourceKey<JukeboxSong> SHADOW_PANTHER_THEME_KEY = createSong("shadow_panther_theme");
    public static final ResourceKey<JukeboxSong> ECHOES_OF_THE_CROWN_KEY = createSong("echoes_of_the_crown");

    private static Supplier<SoundEvent> registerSoundEvent( String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    private static ResourceKey<JukeboxSong> createSong( String name) {
        return ResourceKey.create(Registries.JUKEBOX_SONG, ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, name));
    }

    public static void register( IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
