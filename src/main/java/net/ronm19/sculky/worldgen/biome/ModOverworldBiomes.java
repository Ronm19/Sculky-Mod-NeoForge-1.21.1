package net.ronm19.sculky.worldgen.biome;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.placement.AquaticPlacements;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.ronm19.sculky.entity.ModEntities;
import net.ronm19.sculky.worldgen.ModPlacedFeatures;

public class ModOverworldBiomes {
    private static void addFeature( BiomeGenerationSettings.Builder builder, GenerationStep.Decoration step, ResourceKey<PlacedFeature> feature ) {
        builder.addFeature(step, feature);
    }

    // 🌲 SCULK FOREST
    public static Biome sculkForest(HolderGetter<PlacedFeature> placedFeatureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();

        // 🌲 SCULK FOREST (BALANCED)

        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_PARASITE.get(), 18, 1, 2));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_SENTINEL.get(), 14, 1, 2));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_STALKER.get(), 20, 1, 3));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_SHADE.get(), 16, 1, 2));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_HORROR.get(), 12, 1, 2));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_ZOMBIE.get(), 12, 1, 3));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_SKELETON.get(), 10, 1, 2));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_CREEPER.get(), 8, 1, 2));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_SPIDER.get(), 8, 1, 2));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_ENDERMAN.get(), 6, 1, 1));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULKMITE.get(), 10, 1, 2));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_PHANTOM.get(), 6, 1, 2));

// 🐺 Forest wildlife (reduced heavily)
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_WOLF.get(), 10, 1, 3));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_WOLF_ALPHA.get(), 4, 1, 1));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_HORSE.get(), 6, 1, 2));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_FOX.get(), 8, 1, 2));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_TAIL.get(), 8, 1, 2));
        spawnBuilder.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_BAT.get(), 12, 1, 2));

// 🐞 Rare / special
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_BEETLE.get(), 2, 1, 1));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.HOLLOW_HORN.get(), 6, 1, 2));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.INFESTED_EYE.get(), 8, 1, 3));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_HUNTER.get(), 6, 1, 2));





        BiomeGenerationSettings.Builder biomeBuilder = new BiomeGenerationSettings.Builder(placedFeatureGetter, carverGetter);
        BiomeDefaultFeatures.addDefaultCarversAndLakes(biomeBuilder);
        BiomeDefaultFeatures.addDefaultCrystalFormations(biomeBuilder);
        BiomeDefaultFeatures.addDefaultMonsterRoom(biomeBuilder);
        BiomeDefaultFeatures.addDefaultUndergroundVariety(biomeBuilder);
        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);

        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ModPlacedFeatures.INFESTED_SCULK_ORE_PLACED_KEY);


        return new Biome.BiomeBuilder()
                .hasPrecipitation(false)
                .temperature(1.2F)
                .downfall(0.0F)
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(0x0a131f)
                        .waterFogColor(0x040a11)
                        .fogColor(0x0e1922)
                        .skyColor(calculateSkyColor(0.3F))
                        .grassColorOverride(0x0b2615)
                        .foliageColorOverride(0x0c1a10)
                        .ambientParticle(new AmbientParticleSettings(ParticleTypes.SCULK_SOUL, 0.0095f))
                        .ambientLoopSound(SoundEvents.AMBIENT_WARPED_FOREST_LOOP)
                        .ambientMoodSound(new AmbientMoodSettings(SoundEvents.AMBIENT_CAVE, 5000, 8, 2.0D))
                        .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_DEEP_DARK))
                        .build())
                .mobSpawnSettings(spawnBuilder.build())
                .generationSettings(biomeBuilder.build())
                .build();
    }

    // 🏜️ SCULK WASTES
    public static Biome sculkWastes(HolderGetter<PlacedFeature> placedFeatureGetter,
                                    HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {

        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();
        // Keep spawns lighter than forest (wastes = open, harsher, less packed)
        // 🏜️ SCULK WASTES (BALANCED)
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_ZOMBIE.get(), 10, 1, 2));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_SKELETON.get(), 8, 1, 2));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_CREEPER.get(), 6, 1, 1));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_SPIDER.get(), 6, 1, 1));
// 👻 Signature Wastes threat
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_PHANTOM.get(), 14, 1, 3));
// 🪤 Unique terrain mob
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_SANDSNARE.get(), 12, 1, 3));
// 🐞 Small life (keeps it from feeling dead)
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_BEETLE.get(), 6, 1, 2));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_RAT.get(), 6, 1, 2));
// 🧠 Hunter = rare but scary
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_HUNTER.get(), 5, 1, 2));



        BiomeGenerationSettings.Builder biomeBuilder = new BiomeGenerationSettings.Builder(placedFeatureGetter, carverGetter);

        // ✅ Vanilla-safe base gen (won’t break anything)
        BiomeDefaultFeatures.addDefaultCarversAndLakes(biomeBuilder);
        BiomeDefaultFeatures.addDefaultCrystalFormations(biomeBuilder);
        BiomeDefaultFeatures.addDefaultMonsterRoom(biomeBuilder);
        BiomeDefaultFeatures.addDefaultUndergroundVariety(biomeBuilder);
        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);

        // ✅ Add your ore safely (resolve from placedFeatureGetter to avoid key/type issues)
        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, placedFeatureGetter.getOrThrow(ModPlacedFeatures.INFESTED_SCULK_ORE_PLACED_KEY));

        return new Biome.BiomeBuilder()
                .hasPrecipitation(false)
                .temperature(2.0F)   // desert-hot
                .downfall(0.0F)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .waterColor(0x0a131f)
                        .waterFogColor(0x040a11)
                        .fogColor(0x0e1922)

                        // sky color based on temp - pass something hot
                        .skyColor(calculateSkyColor(2.0F))

                        // ✅ IMPORTANT: No foliage dependence in a desert biome.
                        // If you have NO grass/foliage, you can omit these entirely.
                        // If you keep them, keep them subtle so nothing becomes pitch black.
                        .grassColorOverride(0x2b5a4a)
                        .foliageColorOverride(0x2f6552)
                        .ambientParticle(new AmbientParticleSettings(ParticleTypes.SCULK_SOUL, 0.0065f))
                        .ambientLoopSound(SoundEvents.AMBIENT_WARPED_FOREST_LOOP)
                        .ambientMoodSound(new AmbientMoodSettings(SoundEvents.AMBIENT_CAVE, 5000, 8, 2.0D))
                        .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_DEEP_DARK))
                        .build())
                .mobSpawnSettings(spawnBuilder.build())
                .generationSettings(biomeBuilder.build())
                .build();
    }


    // 🌲 SCULK JUNGLE
    public static Biome sculkJungle(HolderGetter<PlacedFeature> placedFeatureGetter,
                                    HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {

        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();

        // Keep spawns lighter than forest (wastes = open, harsher, less packed)
        // 🌿 SCULK JUNGLE (BALANCED)

        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_ZOMBIE.get(), 10, 1, 3));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_SKELETON.get(), 8, 1, 2));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_CREEPER.get(), 6, 1, 2));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_SPIDER.get(), 10, 1, 2));

// 🌴 Jungle-specific pressure
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_HUSK.get(), 12, 1, 3));
// 👻 Less dominant than Wastes
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_PHANTOM.get(), 8, 1, 2));
// 🐆 MAIN JUNGLE STAR
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SHADOW_PANTHER.get(), 14, 1, 2));
// 🧠 Smart threats
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_HUNTER.get(), 8, 1, 2));
// 👁️ Atmosphere + pressure
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.INFESTED_EYE.get(), 12, 1, 3));
// 🐐 Rare encounter
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.HOLLOW_HORN.get(), 4, 1, 2));



        BiomeGenerationSettings.Builder biomeBuilder = new BiomeGenerationSettings.Builder(placedFeatureGetter, carverGetter);

        // ✅ Vanilla-safe base gen (won’t break anything)
        BiomeDefaultFeatures.addDefaultCarversAndLakes(biomeBuilder);
        BiomeDefaultFeatures.addDefaultCrystalFormations(biomeBuilder);
        BiomeDefaultFeatures.addDefaultMonsterRoom(biomeBuilder);
        BiomeDefaultFeatures.addDefaultUndergroundVariety(biomeBuilder);
        BiomeDefaultFeatures.addDefaultOres(biomeBuilder);

        // ✅ Add your ore safely (resolve from placedFeatureGetter to avoid key/type issues)
        biomeBuilder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, placedFeatureGetter.getOrThrow(ModPlacedFeatures.INFESTED_SCULK_ORE_PLACED_KEY));

        return new Biome.BiomeBuilder()
                // Jungle = humid + rainy
                .hasPrecipitation(true)
                .temperature(0.85F)   // warm but not desert
                .downfall(0.9F)       // heavy humidity

                .specialEffects(new BiomeSpecialEffects.Builder()
                        // 🌊 Water — deep infected tone
                        .waterColor(0x0b1e2b)
                        .waterFogColor(0x051017)
                        // 🌫️ Fog — bioluminescent jungle haze
                        .fogColor(0x10232c)
                        // Sky slightly dimmed but still natural
                        .skyColor(calculateSkyColor(0.85F))
                        // 🌿 Grass / foliage overrides
                        // Cyan-teal infected jungle palette
                        .grassColorOverride(0x2f7a67)
                        .foliageColorOverride(0x3aa17e)
                        // ✨ Ambient particles — increase slightly for jungle density
                        .ambientParticle(new AmbientParticleSettings(ParticleTypes.SCULK_SOUL, 0.0095f))
                        // 🔊 Soundscape
                        .ambientLoopSound(SoundEvents.AMBIENT_WARPED_FOREST_LOOP)
                        .ambientMoodSound(new AmbientMoodSettings(SoundEvents.AMBIENT_CAVE, 4000, 10, 2.0D))
                        // 🎵 Music — still deep dark themed
                        .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_DEEP_DARK))
                        .build())

                .mobSpawnSettings(spawnBuilder.build())
                .generationSettings(biomeBuilder.build())
                .build();

    }



    protected static int calculateSkyColor(float temperature) {
        float temp = temperature / 3.0F;
        temp = Mth.clamp(temp, -1.0F, 1.0F);
        return Mth.hsvToRgb(0.62222224F - temp * 0.05F, 0.5F + temp * 0.1F, 1.0F);
    }
}