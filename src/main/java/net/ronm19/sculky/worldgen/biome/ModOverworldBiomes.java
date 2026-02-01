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
        
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_PARASITE.get(), 30, 1, 3));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_SENTINEL.get(), 30, 2, 3));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_STALKER.get(), 30, 2, 5));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_SHADE.get(), 30, 2, 4));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_HORROR.get(), 30, 2, 4));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_ZOMBIE.get(), 30, 2, 5));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_SKELETON.get(), 30, 2, 4));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_CREEPER.get(), 30, 2, 4));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_SPIDER.get(), 30, 2, 3));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_ENDERMAN.get(), 30, 2, 3));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULKMITE.get(), 30, 1, 3));

        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_WOLF.get(), 30, 2, 5));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_WOLF_ALPHA.get(), 30, 1, 2));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_HORSE.get(), 30, 2, 4));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_FOX.get(), 30, 1, 3));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_TAIL.get(), 30, 2, 4));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_BEETLE.get(), 3, 1, 2));

        spawnBuilder.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_BAT.get(), 30, 2, 5));



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
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_ZOMBIE.get(), 12, 1, 3));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_SKELETON.get(), 10, 1, 3));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_CREEPER.get(), 8, 1, 2));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_SPIDER.get(), 8, 1, 2));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_BEETLE.get(), 10, 1, 3));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_SANDSNARE.get(), 13, 2, 5));
        spawnBuilder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(ModEntities.SCULK_RAT.get(), 10, 2, 3));

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



    protected static int calculateSkyColor(float temperature) {
        float temp = temperature / 3.0F;
        temp = Mth.clamp(temp, -1.0F, 1.0F);
        return Mth.hsvToRgb(0.62222224F - temp * 0.05F, 0.5F + temp * 0.1F, 1.0F);
    }
}