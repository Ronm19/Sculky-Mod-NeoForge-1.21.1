package net.ronm19.sculky.event;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.SculkWolfEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.entity.custom.SculkWolfEntity;

import java.util.List;

@EventBusSubscriber(modid = SculkyMod.MOD_ID)
public class SculkWolfCallEvents {

    private static final int SEARCH_RADIUS = 48;

    @SubscribeEvent
    public static void onDarknessApplied( MobEffectEvent.Added event ) {

        if (!(event.getEntity() instanceof Player player)) return;
        if (player.isCreative() || player.isSpectator()) return;

        MobEffectInstance effect = event.getEffectInstance();
        if (effect.getEffect() != MobEffects.DARKNESS) return;

        Level lvl = player.level();
        if (!(lvl instanceof ServerLevel level)) return;

        BlockPos pos = player.blockPosition();
        AABB box = new AABB(pos).inflate(SEARCH_RADIUS);

        List<SculkWolfEntity> wolves =
                level.getEntitiesOfClass(SculkWolfEntity.class, box);

        for (SculkWolfEntity wolf : wolves) {
            if (!wolf.isAlive()) continue;

            // MUST be tamed
            if (!wolf.isTame()) continue;

            // MUST have an owner
            if (wolf.getOwner() == null) continue;

            // MUST belong to the player who triggered darkness
            if (!wolf.getOwner().getUUID().equals(player.getUUID())) continue;

            // Teleport if too far, otherwise path normally
            if (wolf.distanceTo(player) > 24) {
                wolf.teleportTo(
                        player.getX() + level.random.nextInt(-2, 3),
                        player.getY(),
                        player.getZ() + level.random.nextInt(-2, 3)
                );
            }

            wolf.setTarget(player);
            wolf.setAggressive(true);
        }
    }


    private static void teleportNearPlayer( Mob wolf, Player player ) {
        BlockPos pos = player.blockPosition().offset(
                wolf.getRandom().nextInt(5) - 2,
                0,
                wolf.getRandom().nextInt(5) - 2
        );

        wolf.teleportTo(
                pos.getX() + 0.5,
                pos.getY(),
                pos.getZ() + 0.5
        );
    }
}