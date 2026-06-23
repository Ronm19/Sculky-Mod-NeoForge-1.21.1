package net.ronm19.sculky.event;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.block.ModBlocks;
import net.ronm19.sculky.entity.ModEntities;
import net.ronm19.sculky.entity.client.*;
import net.ronm19.sculky.entity.custom.*;
import net.ronm19.sculky.entity.layer.ModModelLayers;
import net.ronm19.sculky.item.ModItems;
import net.ronm19.sculky.item.custom.InfestedSculkHammerItem;
import net.ronm19.sculky.potion.ModPotions;

import java.util.HashSet;
import java.util.Set;

@EventBusSubscriber(modid = SculkyMod.MOD_ID)
public class ModEvents {
    // Done with the help of https://github.com/CoFH/CoFHCore/blob/1.19.x/src/main/java/cofh/core/event/AreaEffectEvents.java
    // Don't be a jerk License
    private static final Set<BlockPos> HARVESTED_BLOCKS = new HashSet<>();

    @SubscribeEvent
    public static void onHammerUsage( BlockEvent.BreakEvent event ) {
        Player player = event.getPlayer();
        ItemStack mainHandItem = player.getMainHandItem();

        if (mainHandItem.getItem() instanceof InfestedSculkHammerItem hammer && player instanceof ServerPlayer serverPlayer) {
            BlockPos initialBlockPos = event.getPos();
            if (HARVESTED_BLOCKS.contains(initialBlockPos)) {
                return;
            }

            for (BlockPos pos : InfestedSculkHammerItem.getBlocksToBeDestroyed(1, initialBlockPos, serverPlayer)) {
                if (pos == initialBlockPos || !hammer.isCorrectToolForDrops(mainHandItem, event.getLevel().getBlockState(pos))) {
                    continue;
                }

                HARVESTED_BLOCKS.add(pos);
                serverPlayer.gameMode.destroyBlock(pos);
                HARVESTED_BLOCKS.remove(pos);
            }
        }
    }

    @SubscribeEvent
    public static void onBrewingRecipeRegister( RegisterBrewingRecipesEvent event ) {
        PotionBrewing.Builder builder = event.getBuilder();

        builder.addMix(Potions.AWKWARD, ModItems.SCULK_HEARTFRUIT.asItem(), ModPotions.SCULK_INFECTION_POTION);
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, tintIndex) -> 0xFFFFFF, ModBlocks.INFESTED_SCULK_LEAVES.get());
        event.register((state, level, pos, tintIndex) -> 0xFFFFFF, ModBlocks.SCULK_JUNGLE_LEAVES.get());
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> 0xFFFFFF, ModBlocks.INFESTED_SCULK_LEAVES.get().asItem());
        event.register((stack, tintIndex) -> 0xFFFFFF, ModBlocks.SCULK_JUNGLE_LEAVES.get().asItem());


    }


    @SubscribeEvent
    public static void registerLayers( EntityRenderersEvent.RegisterLayerDefinitions event ) {
        event.registerLayerDefinition(ModModelLayers.SCULK_SENTINEL, SculkSentinelModel :: createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.SCULK_SHADE, SculkShadeModel :: createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.SCULK_HORROR, SculkHorrorModel :: createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.SCULK_SPIDER, SculkSpiderModel :: createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.SCULK_TAIL, SculkTailModel :: createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.SCULK_SANDSNARE, SculkSandsnareModel :: createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.SALVATORE, SalvatoreModel :: createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.SANCTUM_WATCHER, SanctumWatcherModel :: createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.ROYAL_SCULK_KNIGHT, RoyalSculkKnightModel :: createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.SCULK_BRUTE, SculkBruteModel :: createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.SCULK_BURROWER, SculkBurrowerModel :: createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.SCULK_HERALD, SculkHeraldModel :: createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.SCULK_SNAPPER, SculkSnapperModel :: createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.SCULK_EXECUTIONER, SculkExecutionerModel :: createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.SCULK_BULWARK, SculkBulwarkModel :: createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.SCULK_ORACLE, SculkOracleModel :: createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.THRONEBOUND_WRAITH, ThroneboundWraithModel :: createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.SCULK_KING, SculkKingModel :: createBodyLayer);

        event.registerLayerDefinition(ModModelLayers.SCULK_WOLF, SculkWolfModel :: createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.SCULK_FOX, SculkFoxModel :: createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.SCULK_RAT, SculkRatModel :: createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.SCULK_WOLF_ALPHA, SculkWolfAlphaModel :: createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.SCULK_BAT, SculkBatModel :: createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.SCULK_BEETLE, SculkBeetleModel :: createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.HOLLOW_HORN, HollowhornModel :: createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.INFESTED_EYE, InfestedEyeModel :: createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.SCULK_HUNTER, SculkHunterModel :: createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.SHADOW_PANTHER, ShadowPantherModel :: createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.SCULK_GOLEM, SculkGolemModel :: createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.SCULK_BEAR, SculkBearModel :: createBodyLayer);
    }

    @SubscribeEvent
    public static void registerAttributes( EntityAttributeCreationEvent event ) {
        event.put(ModEntities.SCULK_PARASITE.get(), SculkParasiteEntity.createSculkParasiteAttributes().build());
        event.put(ModEntities.SCULK_SENTINEL.get(), SculkSentinelEntity.createSculkSentinelAttributes().build());
        event.put(ModEntities.SCULK_STALKER.get(), SculkStalkerEntity.createSculkStalkerAttributes().build());
        event.put(ModEntities.SCULK_SHADE.get(), SculkShadeEntity.createSculkShadeAttributes().build());
        event.put(ModEntities.SCULK_HORROR.get(), SculkHorrorEntity.createSculkHorrorAttributes().build());
        event.put(ModEntities.SCULK_ZOMBIE.get(), SculkZombieEntity.createSculkZombieAttributes().build());
        event.put(ModEntities.SCULK_HUSK.get(), SculkHuskEntity.createSculkHuskAttributes().build());
        event.put(ModEntities.SCULK_SKELETON.get(), SculkSkeletonEntity.createSculkSkeletonAttributes().build());
        event.put(ModEntities.SCULK_CREEPER.get(), SculkCreeperEntity.createSculkCreeperAttributes().build());
        event.put(ModEntities.SCULK_SPIDER.get(), SculkSpiderEntity.createSculkSpiderAttributes().build());
        event.put(ModEntities.SCULK_TAIL.get(), SculkTailEntity.createSculkTailAttributes().build());
        event.put(ModEntities.SCULK_ENDERMAN.get(), SculkEndermanEntity.createSculkEndermanAttributes().build());
        event.put(ModEntities.SCULK_SANDSNARE.get(), SculkSandsnareEntity.createSculkSandsnareAttributes().build());
        event.put(ModEntities.SCULKMITE.get(), SculkmiteEntity.createSculkmiteAttributes().build());
        event.put(ModEntities.CROWNED_SCULKMITE.get(), CrownedSculkmiteEntity.createCrownedSculkmiteAttributes().build());
        event.put(ModEntities.SALVATORE.get(), SalvatoreEntity.createSalvatoreAttributes().build());
        event.put(ModEntities.SCULK_PHANTOM.get(), SculkPhantomEntity.createSculkPhantomAttributes().build());
        event.put(ModEntities.SANCTUM_WATCHER.get(), SanctumWatcherEntity.createSanctumWatcherAttributes().build());
        event.put(ModEntities.ROYAL_SCULK_KNIGHT.get(), RoyalSculkKnightEntity.createRoyalSculkKnightAttributes().build());
        event.put(ModEntities.SCULK_NECROMANCER.get(), SculkNecromancerEntity.createSculkNecromancerAttributes().build());
        event.put(ModEntities.SCULK_BRUTE.get(), SculkBruteEntity.createSculkBruteAttributes().build());
        event.put(ModEntities.SCULK_BURROWER.get(), SculkBurrowerEntity.createSculkBurrowerAttributes().build());
        event.put(ModEntities.SCULK_SLIME.get(), SculkSlimeEntity.createSculkSlimeAttributes().build());
        event.put(ModEntities.SCULK_HERALD.get(), SculkHeraldEntity.createSculkHeraldAttributes().build());
        event.put(ModEntities.SCULK_SNAPPER.get(), SculkSnapperEntity.createSculkSnapperAttributes().build());
        event.put(ModEntities.SCULK_VINDICATOR.get(), SculkVindicatorEntity.createSculkVindicatorAttributes().build());
        event.put(ModEntities.SCULK_SENTRY.get(), SculkSentryEntity.createSculkSentryAttributes().build());
        event.put(ModEntities.SCULK_SPIRIT.get(), SculkSpiritEntity.createSculkSpiritAttributes().build());
        event.put(ModEntities.SCULK_EVOKER.get(), SculkEvokerEntity.createSculkEvokerAttributes().build());
        event.put(ModEntities.SCULK_EXECUTIONER.get(), SculkExecutionerEntity.createSculkExecutionerAttributes().build());
        event.put(ModEntities.SCULK_BULWARK.get(), SculkBulwarkEntity.createSculkBulwarkAttributes().build());
        event.put(ModEntities.SCULK_ORACLE.get(), SculkOracleEntity.createSculkOracleAttributes().build());
        event.put(ModEntities.THRONEBOUND_WRAITH.get(), ThroneboundWraithEntity.createThroneboundWraithAttributes().build());
        event.put(ModEntities.SCULK_KING.get(), SculkKingEntity.createSculkKingAttributes().build());

        event.put(ModEntities.SCULK_WOLF.get(), SculkWolfEntity.createsSculkWolfAttributes().build());
        event.put(ModEntities.SCULK_WOLF_ALPHA.get(), SculkWolfAlphaEntity.createSculkWolfAlphaAttributes().build());
        event.put(ModEntities.SCULK_HORSE.get(), SculkHorseEntity.createSculkHorseAttributes().build());
        event.put(ModEntities.SCULK_FOX.get(), SculkFoxEntity.createSculkFoxAttributes().build());
        event.put(ModEntities.SCULK_RAT.get(), SculkRatEntity.createSculkRatAttributes().build());
        event.put(ModEntities.SCULK_BAT.get(), SculkBatEntity.createSculkBatAttributes().build());
        event.put(ModEntities.SCULK_BEETLE.get(), SculkBeetleEntity.createSculkBeetleAttributes().build());
        event.put(ModEntities.HOLLOW_HORN.get(), HollowhornEntity.createHollowhornAttributes().build());
        event.put(ModEntities.INFESTED_EYE.get(), InfestedEyeEntity.createInfestedEyeAttributes().build());
        event.put(ModEntities.SCULK_HUNTER.get(), SculkHunterEntity.createSculkHunterAttributes().build());
        event.put(ModEntities.SHADOW_PANTHER.get(), ShadowPantherEntity.createShadowPantherAttributes().build());
        event.put(ModEntities.SCULK_GOLEM.get(), SculkGolemEntity.createSculkGolemAttributes().build());
        event.put(ModEntities.SCULK_DOLPHIN.get(), SculkDolphinEntity.createSculkDolphinAttributes().build());
        event.put(ModEntities.SCULK_BEAR.get(), SculkBearEntity.createSculkBearAttributes().build());

    }

    @SubscribeEvent
    public static void registerSpawnPlacements( RegisterSpawnPlacementsEvent event ) {

        // ---------------------------------------------------------
        //                   HOSTILE / MONSTER ENTITIES
        // ---------------------------------------------------------
        event.register(ModEntities.SCULK_PARASITE.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster :: checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        event.register(ModEntities.SCULK_SENTINEL.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster :: checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        event.register(ModEntities.SCULK_STALKER.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster :: checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        event.register(ModEntities.SCULK_SHADE.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster :: checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        event.register(ModEntities.SCULK_HORROR.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster :: checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        event.register(ModEntities.SCULK_ZOMBIE.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster :: checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        event.register(ModEntities.SCULK_HUSK.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster :: checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        event.register(ModEntities.SCULK_SKELETON.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster :: checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        event.register(ModEntities.SCULK_CREEPER.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster :: checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        event.register(ModEntities.SCULK_SPIDER.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster :: checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        event.register(ModEntities.SCULK_ENDERMAN.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        event.register(ModEntities.SCULKMITE.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        event.register(
                ModEntities.CROWNED_SCULKMITE.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                CrownedSculkmiteEntity::checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE
        );

        event.register(ModEntities.SCULK_SANDSNARE.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        event.register(ModEntities.SALVATORE.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        event.register(ModEntities.SANCTUM_WATCHER.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        event.register(ModEntities.SCULK_HERALD.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        event.register(ModEntities.SCULK_VINDICATOR.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                SculkVindicatorEntity::checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);


        event.register(ModEntities.SCULK_SENTRY.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                SculkSentryEntity::checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        // ---------------------------------------------------------
        //                NEUTRAL / TAMABLE-TYPE ENTITIES
        // ---------------------------------------------------------

        event.register(ModEntities.SCULK_WOLF.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                TamableAnimal :: checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        event.register(ModEntities.SCULK_WOLF_ALPHA.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                TamableAnimal :: checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        event.register(ModEntities.SCULK_FOX.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                TamableAnimal :: checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);


        event.register(ModEntities.SCULK_BEETLE.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal :: checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        event.register(ModEntities.SCULK_RAT.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal :: checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        event.register(ModEntities.HOLLOW_HORN.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal :: checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);


        event.register(ModEntities.SHADOW_PANTHER.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal :: checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);


        // ---------------------------------------------------------
        //                PASSIVE / RIDEABLE ENTITIES
        // ---------------------------------------------------------
        event.register(ModEntities.SCULK_HORSE.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal :: checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        event.register(ModEntities.SCULK_TAIL.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal :: checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);


        // ---------------------------------------------------------
        //                   AMBIENT / FLYING ENTITIES
        // ---------------------------------------------------------

        event.register(ModEntities.SCULK_BAT.get(),
                SpawnPlacementTypes.NO_RESTRICTIONS, // correct for bats
                Heightmap.Types.MOTION_BLOCKING,
                AmbientCreature :: checkMobSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        // ---------------------------------------------------------
        //                WATER ENTITIES
        // ---------------------------------------------------------

        event.register(ModEntities.SCULK_DOLPHIN.get(),
                SpawnPlacementTypes.IN_WATER,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                SculkDolphinEntity::checkSurfaceWaterAnimalSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        event.register(ModEntities.SCULK_SNAPPER.get(),
                SpawnPlacementTypes.IN_WATER,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                SculkSnapperEntity::canSpawn,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
}
