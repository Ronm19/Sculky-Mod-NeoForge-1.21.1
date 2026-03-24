package net.ronm19.sculky.datagen;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.ronm19.sculky.SculkyMod;

import java.nio.file.Path;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ModJukeboxSongProvider implements DataProvider {

    private final PackOutput packOutput;
    private final Map<ResourceLocation, JsonObject> songs = new LinkedHashMap<>();

    public ModJukeboxSongProvider(PackOutput packOutput) {
        this.packOutput = packOutput;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        /* Inside this list below you can add your music disc.
           By typing new SongDef( ) and give it the song String name, int comparator, float length
        */
        List<SongDef> entries = List.of(new SongDef("shadow_panther_theme", 15, 270.0f)
        );

        entries.forEach(this::addSong);
        PackOutput.PathProvider pathProvider = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, "jukebox_song");
        return CompletableFuture.allOf(songs.entrySet().stream()
                .map(entry -> {
                    Path path = pathProvider.json(entry.getKey());
                    return DataProvider.saveStable(cache, entry.getValue(), path);
                }).toArray(CompletableFuture[]::new)
        );
    }

    private void addSong(SongDef def) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(SculkyMod.MOD_ID, def.name());
        JsonObject desc = new JsonObject();
        JsonObject root = new JsonObject();

        root.add("comparator_output", new JsonPrimitive(def.comparator()));
        desc.add("translate", new JsonPrimitive("item." + SculkyMod.MOD_ID + ".music_disc_" + def.name() + ".desc"));
        root.add("description", desc);
        root.add("length_in_seconds", new JsonPrimitive(def.length()));
        root.add("sound_event", new JsonPrimitive(id.toString()));

        if (songs.put(id, root) != null) {
            throw new IllegalStateException("Duplicate jukebox song: " + id);
        }
    }

    @Override
    public String getName() {
        return "Jukebox Songs: " + SculkyMod.MOD_ID;
    }

    private record SongDef(String name, int comparator, float length) {
    }
}