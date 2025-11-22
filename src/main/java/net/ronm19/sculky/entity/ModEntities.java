package net.ronm19.sculky.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.*;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, SculkyMod.MOD_ID);


    public static final Supplier<EntityType<SculkParasiteEntity>> SCULK_PARASITE =
            ENTITY_TYPES.register("sculk_parasite", () -> EntityType.Builder.of(SculkParasiteEntity::new, MobCategory.MONSTER)
                    .sized(0.4F, 0.25F).build("sculk_parasite"));

    public static final Supplier<EntityType<SculkSentinelEntity>> SCULK_SENTINEL =
            ENTITY_TYPES.register("sculk_sentinel", () -> EntityType.Builder.of(SculkSentinelEntity::new, MobCategory.MONSTER)
                    .sized(1.2F, 3.2F).eyeHeight(2.75F).clientTrackingRange(10).build("sculk_sentinel"));

    public static final Supplier<EntityType<SculkStalkerEntity>> SCULK_STALKER =
            ENTITY_TYPES.register("sculk_stalker", () -> EntityType.Builder.of(SculkStalkerEntity::new, MobCategory.MONSTER)
                    .sized(1.4F, 0.9F).build("sculk_stalker"));

    public static final Supplier<EntityType<SculkShadeEntity>> SCULK_SHADE =
            ENTITY_TYPES.register("sculk_shade", () -> EntityType.Builder.of(SculkShadeEntity ::new, MobCategory.MONSTER)
                            .sized(0.5f, 0.6f).eyeHeight(0.9F).build("sculk_shade"));

    public static final Supplier<EntityType<SculkHorrorEntity>> SCULK_HORROR =
            ENTITY_TYPES.register("sculk_horror", () -> EntityType.Builder.of(SculkHorrorEntity ::new, MobCategory.MONSTER)
                    .sized(0.2F, 0.5F).eyeHeight(0.9F).build("sculk_horror"));




    public static final Supplier<EntityType<SculkWolfEntity>> SCULK_WOLF =
            ENTITY_TYPES.register("sculk_wolf", () -> EntityType.Builder.of(SculkWolfEntity ::new, MobCategory.CREATURE)
                    .sized(0.8f, 0.9f).eyeHeight(0.9F).build("sculk_wolf"));

    public static final Supplier<EntityType<SculkWolfAlphaEntity>> SCULK_WOLF_ALPHA =
            ENTITY_TYPES.register("sculk_wolf_alpha", () -> EntityType.Builder.of(SculkWolfAlphaEntity ::new, MobCategory.CREATURE)
                    .sized(0.8f, 0.9f).eyeHeight(0.9F).build("sculk_wolf_alpha"));

    public static final Supplier<EntityType<SculkHorseEntity>> SCULK_HORSE =
            ENTITY_TYPES.register("sculk_horse", () -> EntityType.Builder.of(SculkHorseEntity::new, MobCategory.CREATURE)
                    .sized(0.3F, 0.6F).build("sculk_horse"));

    public static final Supplier<EntityType<SculkFoxEntity>> SCULK_FOX =
            ENTITY_TYPES.register("sculk_fox", () -> EntityType.Builder.of(SculkFoxEntity::new, MobCategory.CREATURE)
                    .sized(0.3F, 0.6F).eyeHeight(0.9F).build("sculk_fox"));

    public static final Supplier<EntityType<SculkBatEntity>> SCULK_BAT =
            ENTITY_TYPES.register("sculk_bat", () -> EntityType.Builder.of(SculkBatEntity::new, MobCategory.AMBIENT)
                    .sized(0.5f, 0.1f).eyeHeight(0.9F).build("sculk_bat"));

    public static void register( IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
