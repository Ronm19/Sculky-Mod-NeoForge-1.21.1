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
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.ronm19.sculky.SculkyMod;
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
    public static void registerLayers( EntityRenderersEvent.RegisterLayerDefinitions event ) {
        event.registerLayerDefinition(ModModelLayers.SCULK_SENTINEL, SculkSentinelModel :: createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.SCULK_SHADE, SculkShadeModel :: createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.SCULK_HORROR, SculkHorrorModel :: createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.SCULK_SPIDER, SculkSpiderModel :: createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.SCULK_TAIL, SculkTailModel :: createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.SCULK_SANDSNARE, SculkSandsnareModel :: createBodyLayer);

        event.registerLayerDefinition(ModModelLayers.SCULK_WOLF, SculkWolfModel :: createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.SCULK_FOX, SculkFoxModel :: createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.SCULK_RAT, SculkRatModel :: createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.SCULK_WOLF_ALPHA, SculkWolfAlphaModel :: createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.SCULK_BAT, SculkBatModel :: createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.SCULK_BEETLE, SculkBeetleModel :: createBodyLayer);
    }

    @SubscribeEvent
    public static void registerAttributes( EntityAttributeCreationEvent event ) {
        event.put(ModEntities.SCULK_PARASITE.get(), SculkParasiteEntity.createSculkParasiteAttributes().build());
        event.put(ModEntities.SCULK_SENTINEL.get(), SculkSentinelEntity.createSculkSentinelAttributes().build());
        event.put(ModEntities.SCULK_STALKER.get(), SculkStalkerEntity.createSculkStalkerAttributes().build());
        event.put(ModEntities.SCULK_SHADE.get(), SculkShadeEntity.createSculkShadeAttributes().build());
        event.put(ModEntities.SCULK_HORROR.get(), SculkHorrorEntity.createSculkHorrorAttributes().build());
        event.put(ModEntities.SCULK_ZOMBIE.get(), SculkZombieEntity.createSculkZombieAttributes().build());
        event.put(ModEntities.SCULK_SKELETON.get(), SculkSkeletonEntity.createSculkSkeletonAttributes().build());
        event.put(ModEntities.SCULK_CREEPER.get(), SculkCreeperEntity.createSculkCreeperAttributes().build());
        event.put(ModEntities.SCULK_SPIDER.get(), SculkSpiderEntity.createSculkSpiderAttributes().build());
        event.put(ModEntities.SCULK_TAIL.get(), SculkTailEntity.createSculkTailAttributes().build());
        event.put(ModEntities.SCULK_ENDERMAN.get(), SculkEndermanEntity.createSculkEndermanAttributes().build());
        event.put(ModEntities.SCULK_SANDSNARE.get(), SculkSandsnareEntity.createSculkSandsnareAttributes().build());
        event.put(ModEntities.SCULKMITE.get(), SculkmiteEntity.createSculkmiteAttributes().build());

        event.put(ModEntities.SCULK_WOLF.get(), SculkWolfEntity.createsSculkWolfAttributes().build());
        event.put(ModEntities.SCULK_WOLF_ALPHA.get(), SculkWolfAlphaEntity.createSculkWolfAlphaAttributes().build());
        event.put(ModEntities.SCULK_HORSE.get(), SculkHorseEntity.createSculkHorseAttributes().build());
        event.put(ModEntities.SCULK_FOX.get(), SculkFoxEntity.createSculkFoxAttributes().build());
        event.put(ModEntities.SCULK_RAT.get(), SculkRatEntity.createSculkRatAttributes().build());
        event.put(ModEntities.SCULK_BAT.get(), SculkBatEntity.createSculkBatAttributes().build());
        event.put(ModEntities.SCULK_BEETLE.get(), SculkBeetleEntity.createSculkBeetleAttributes().build());

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

        event.register(ModEntities.SCULK_SANDSNARE.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMobSpawnRules,
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
    }
}
