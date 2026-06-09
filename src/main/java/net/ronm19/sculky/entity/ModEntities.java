package net.ronm19.sculky.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.*;
import net.ronm19.sculky.entity.projectile.SculkBombProjectileEntity;
import net.ronm19.sculky.entity.projectile.SculkJarProjectileEntity;
import net.ronm19.sculky.entity.projectile.ShadowBoltEntity;
import net.ronm19.sculky.entity.projectile.SonicBoomEntity;

import java.util.function.Supplier;

import static javax.swing.text.html.parser.DTDConstants.ENTITIES;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, SculkyMod.MOD_ID);

    // * ------------------------------------ MONSTER ------------------------------------- * //

    public static final Supplier<EntityType<SculkParasiteEntity>> SCULK_PARASITE =
            ENTITY_TYPES.register("sculk_parasite", () -> EntityType.Builder.of(SculkParasiteEntity::new, MobCategory.MONSTER)
                    .sized(0.4F, 0.3F).eyeHeight(0.15F).build("sculk_parasite"));

    public static final Supplier<EntityType<SculkmiteEntity>> SCULKMITE =
            ENTITY_TYPES.register("sculkmite", () -> EntityType.Builder.of(SculkmiteEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 0.4F).eyeHeight(0.25F).build("sculkmite"));

    public static final Supplier<EntityType<CrownedSculkmiteEntity>> CROWNED_SCULKMITE =
            ENTITY_TYPES.register("crowned_sculkmite", () -> EntityType.Builder.of(CrownedSculkmiteEntity::new, MobCategory.MONSTER)
                    .sized(0.45F, 0.35F).eyeHeight(0.25F).build("crowned_sculkmite"));

    public static final Supplier<EntityType<SculkSentinelEntity>> SCULK_SENTINEL =
            ENTITY_TYPES.register("sculk_sentinel", () -> EntityType.Builder.of(SculkSentinelEntity::new, MobCategory.MONSTER)
                    .sized(1.2F, 3.0F).eyeHeight(2.6F).clientTrackingRange(10).build("sculk_sentinel"));

    public static final Supplier<EntityType<SculkStalkerEntity>> SCULK_STALKER =
            ENTITY_TYPES.register("sculk_stalker", () -> EntityType.Builder.of(SculkStalkerEntity::new, MobCategory.MONSTER)
                    .sized(1.3F, 1.1F).eyeHeight(0.9F).build("sculk_stalker"));

    public static final Supplier<EntityType<SculkShadeEntity>> SCULK_SHADE =
            ENTITY_TYPES.register("sculk_shade", () -> EntityType.Builder.of(SculkShadeEntity ::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.8F).eyeHeight(1.6F).build("sculk_shade"));

    public static final Supplier<EntityType<SculkHorrorEntity>> SCULK_HORROR =
            ENTITY_TYPES.register("sculk_horror", () -> EntityType.Builder.of(SculkHorrorEntity ::new, MobCategory.MONSTER)
                    .sized(0.9F, 1.4F).eyeHeight(1.2F).build("sculk_horror"));

    public static final Supplier<EntityType<SculkZombieEntity>> SCULK_ZOMBIE =
            ENTITY_TYPES.register("sculk_zombie", () -> EntityType.Builder.of(SculkZombieEntity ::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F).eyeHeight(1.74F).build("sculk_zombie"));

    public static final Supplier<EntityType<SculkHuskEntity>> SCULK_HUSK =
            ENTITY_TYPES.register("sculk_husk", () -> EntityType.Builder.of(SculkHuskEntity ::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F).eyeHeight(1.74F).build("sculk_husk"));

    public static final Supplier<EntityType<SculkSkeletonEntity>> SCULK_SKELETON =
            ENTITY_TYPES.register("sculk_skeleton", () -> EntityType.Builder.of(SculkSkeletonEntity ::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.99F).eyeHeight(1.74F).build("sculk_skeleton"));

    public static final Supplier<EntityType<SculkCreeperEntity>> SCULK_CREEPER =
            ENTITY_TYPES.register("sculk_creeper", () -> EntityType.Builder.of(SculkCreeperEntity ::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.7F).eyeHeight(1.47F).build("sculk_creeper"));

    public static final Supplier<EntityType<SculkSpiderEntity>> SCULK_SPIDER =
            ENTITY_TYPES.register("sculk_spider", () -> EntityType.Builder.of(SculkSpiderEntity::new, MobCategory.MONSTER)
                            .sized(1.6F, 1.0F).eyeHeight(0.75F).build("sculk_spider"));

    public static final Supplier<EntityType<SculkSandsnareEntity>> SCULK_SANDSNARE =
            ENTITY_TYPES.register("sculk_sandsnare", () -> EntityType.Builder.of(SculkSandsnareEntity::new, MobCategory.MONSTER)
                            .sized(1.4F, 0.6F).eyeHeight(0.35F).build("sculk_sandsnare"));

    public static final Supplier<EntityType<SculkEndermanEntity>> SCULK_ENDERMAN =
            ENTITY_TYPES.register("sculk_enderman", () -> EntityType.Builder.of(SculkEndermanEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 2.9F).eyeHeight(2.55F).build("sculk_enderman"));

    public static final Supplier<EntityType<SanctumWatcherEntity>> SANCTUM_WATCHER =
            ENTITY_TYPES.register("sanctum_watcher", () -> EntityType.Builder.of(SanctumWatcherEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 2.9F).eyeHeight(2.45F).build("sanctum_watcher"));

    public static final Supplier<EntityType<RoyalSculkKnightEntity>> ROYAL_SCULK_KNIGHT =
            ENTITY_TYPES.register("royal_sculk_knight", () -> EntityType.Builder.of(RoyalSculkKnightEntity::new, MobCategory.MONSTER)
                    .sized(0.5F, 1.2F).eyeHeight(1.2F).clientTrackingRange(12).build("royal_sculk_knight"));

    public static final Supplier<EntityType<SculkPhantomEntity>> SCULK_PHANTOM =
            ENTITY_TYPES.register("sculk_phantom", () -> EntityType.Builder.of(SculkPhantomEntity::new, MobCategory.MONSTER)
                    .sized(0.9F, 0.5F).eyeHeight(0.25F).build("sculk_phantom"));

    public static final Supplier<EntityType<SalvatoreEntity>> SALVATORE =
            ENTITY_TYPES.register("salvatore", () -> EntityType.Builder.of(SalvatoreEntity::new, MobCategory.MONSTER)
                    .sized(0.8F, 2.6F).eyeHeight(2.0F).build("salvatore"));

    public static final Supplier<EntityType<SculkNecromancerEntity>> SCULK_NECROMANCER =
            ENTITY_TYPES.register("sculk_necromancer", () -> EntityType.Builder.of(SculkNecromancerEntity ::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.99F).eyeHeight(1.74F).build("sculk_necromancer"));

    public static final Supplier<EntityType<SculkBruteEntity>> SCULK_BRUTE =
            ENTITY_TYPES.register("sculk_brute", () -> EntityType.Builder.of(SculkBruteEntity::new, MobCategory.MONSTER)
                    .sized(1.35F, 2.1F).eyeHeight(2.4F).clientTrackingRange(10).updateInterval(3).build("sculk_brute"));

    public static final Supplier<EntityType<SculkBurrowerEntity>> SCULK_BURROWER =
            ENTITY_TYPES.register("sculk_burrower", () -> EntityType.Builder.of(SculkBurrowerEntity::new, MobCategory.MONSTER)
                    .sized(0.9F, 0.6F).eyeHeight(0.29F).build("sculk_burrower"));

    public static final Supplier<EntityType<SculkSlimeEntity>> SCULK_SLIME =
            ENTITY_TYPES.register("sculk_slime", () -> EntityType.Builder.of(SculkSlimeEntity::new, MobCategory.MONSTER)
                            .sized(0.9F, 0.8F).eyeHeight(0.14F).clientTrackingRange(10).build("sculk_slime"));

    public static final Supplier<EntityType<SculkHeraldEntity>> SCULK_HERALD =
            ENTITY_TYPES.register("sculk_herald", () -> EntityType.Builder.of(SculkHeraldEntity::new, MobCategory.MONSTER)
                    .sized(0.9F, 2.7F).eyeHeight(0.18F).clientTrackingRange(10).build("sculk_herald"));

    // ----------------------------- NATURAL & PASSIVE ---------------------------- //


    public static final Supplier<EntityType<SculkWolfEntity>> SCULK_WOLF =
            ENTITY_TYPES.register("sculk_wolf", () -> EntityType.Builder.of(SculkWolfEntity ::new, MobCategory.CREATURE)
                    .sized(0.8F, 0.9F).eyeHeight(0.68F).build("sculk_wolf"));

    public static final Supplier<EntityType<SculkWolfAlphaEntity>> SCULK_WOLF_ALPHA =
            ENTITY_TYPES.register("sculk_wolf_alpha", () -> EntityType.Builder.of(SculkWolfAlphaEntity ::new, MobCategory.CREATURE)
                    .sized(1.0F, 1.1F).eyeHeight(0.9F).build("sculk_wolf_alpha"));

    public static final Supplier<EntityType<SculkHorseEntity>> SCULK_HORSE =
            ENTITY_TYPES.register("sculk_horse", () -> EntityType.Builder.of(SculkHorseEntity::new, MobCategory.CREATURE)
                    .sized(1.4F, 1.6F).eyeHeight(1.5F).build("sculk_horse"));

    public static final Supplier<EntityType<SculkFoxEntity>> SCULK_FOX =
            ENTITY_TYPES.register("sculk_fox", () -> EntityType.Builder.of(SculkFoxEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 0.7F).eyeHeight(0.45F).build("sculk_fox"));

    public static final Supplier<EntityType<SculkRatEntity>> SCULK_RAT =
            ENTITY_TYPES.register("sculk_rat", () -> EntityType.Builder.of(SculkRatEntity::new, MobCategory.CREATURE)
                            .sized(0.6F, 0.4F).eyeHeight(0.25F).build("sculk_rat"));

    public static final Supplier<EntityType<HollowhornEntity>> HOLLOW_HORN =
            ENTITY_TYPES.register("hollow_horn", () -> EntityType.Builder.of(HollowhornEntity::new, MobCategory.CREATURE)
                    .sized(1.0F, 1.45F).eyeHeight(1.15F).build("hollow_horn"));

    public static final Supplier<EntityType<InfestedEyeEntity>> INFESTED_EYE =
            ENTITY_TYPES.register("infested_eye", () -> EntityType.Builder.of(InfestedEyeEntity ::new, MobCategory.CREATURE)
                    .sized(0.8F, 0.9F).eyeHeight(0.68F).build("infested_eye"));

    public static final Supplier<EntityType<SculkHunterEntity>> SCULK_HUNTER =
            ENTITY_TYPES.register("sculk_hunter", () -> EntityType.Builder.of(SculkHunterEntity ::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.95F).eyeHeight(1.74F).build("sculk_hunter"));

    public static final Supplier<EntityType<SculkBeetleEntity>> SCULK_BEETLE =
            ENTITY_TYPES.register("sculk_beetle", () -> EntityType.Builder.of(SculkBeetleEntity::new, MobCategory.CREATURE)
                    .sized(0.9F, 0.5F).eyeHeight(0.28F).build("sculk_beetle"));

    public static final Supplier<EntityType<ShadowPantherEntity>> SHADOW_PANTHER =
            ENTITY_TYPES.register("shadow_panther", () -> EntityType.Builder.of(ShadowPantherEntity ::new, MobCategory.CREATURE)
                    .sized(1.0F, 1.1F).eyeHeight(0.9F).build("shadow_panther"));

    public static final Supplier<EntityType<SculkGolemEntity>> SCULK_GOLEM =
            ENTITY_TYPES.register("sculk_golem", () -> EntityType.Builder.of(SculkGolemEntity::new, MobCategory.CREATURE)
                            .sized(1.45F, 3.0F).eyeHeight(2.7F).clientTrackingRange(10).updateInterval(3).build("sculk_golem"));


    // -------------------- WATER CREATURES -------------------------- //

    public static final Supplier<EntityType<SculkSnapperEntity>> SCULK_SNAPPER =
            ENTITY_TYPES.register("sculk_snapper", () -> EntityType.Builder.of(SculkSnapperEntity::new, MobCategory.WATER_CREATURE)
                    .sized(0.65F, 0.45F).eyeHeight(0.25F).clientTrackingRange(10).build("sculk_snapper"));

    public static final Supplier<EntityType<SculkDolphinEntity>> SCULK_DOLPHIN =
            ENTITY_TYPES.register("sculk_dolphin", () -> EntityType.Builder.of(SculkDolphinEntity::new, MobCategory.WATER_CREATURE)
                    .sized(0.9F, 0.6F).clientTrackingRange(10).updateInterval(3).build("sculk_dolphin"));


    // -----------------------------  PASSIVE ---------------------------- //


    public static final Supplier<EntityType<SculkTailEntity>> SCULK_TAIL =
            ENTITY_TYPES.register("sculk_tail", () -> EntityType.Builder.of(SculkTailEntity::new, MobCategory.CREATURE)
                            .sized(0.9F, 0.45F).eyeHeight(0.25F).build("sculk_tail"));

    public static final Supplier<EntityType<SculkBatEntity>> SCULK_BAT =
            ENTITY_TYPES.register("sculk_bat", () -> EntityType.Builder.of(SculkBatEntity::new, MobCategory.AMBIENT)
                    .sized(0.5F, 0.9F).eyeHeight(0.45F).build("sculk_bat"));


    // * ----------------------------- MISC ------------------------------- * //

    public static final Supplier<EntityType<SonicBoomEntity>> SONIC_BOOM =
            ENTITY_TYPES.register("sonic_boom", () -> EntityType.Builder.<SonicBoomEntity>of(SonicBoomEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(1).build("sonic_boom"));

    public static final Supplier<EntityType<SculkJarProjectileEntity>> SCULK_JAR_PROJECTILE =
            ENTITY_TYPES.register("sculk_jar_projectile", () -> EntityType.Builder.<SculkJarProjectileEntity>of(SculkJarProjectileEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build("sculky:sculk_jar_projectile"));

    public static final Supplier<EntityType<SculkBombProjectileEntity>> SCULK_BOMB_PROJECTILE =
            ENTITY_TYPES.register("sculk_bomb_projectile", () -> EntityType.Builder.<SculkBombProjectileEntity>of(SculkBombProjectileEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build("sculky:sculk_bomb_projectile"));

    public static final Supplier<EntityType<ShadowBoltEntity>> SHADOW_BOLT =
            ENTITY_TYPES.register("shadow_bolt", () -> EntityType.Builder.<ShadowBoltEntity>of(ShadowBoltEntity::new, MobCategory.MISC)
                            .sized(0.3F, 0.3F).clientTrackingRange(4).updateInterval(10).build("shadow_bolt"));





    public static void register( IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
