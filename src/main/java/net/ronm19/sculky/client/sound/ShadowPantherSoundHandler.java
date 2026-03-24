package net.ronm19.sculky.client.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.ronm19.sculky.entity.custom.ShadowPantherEntity;
import net.ronm19.sculky.sounds.ModSounds;

import java.util.Comparator;
import java.util.List;

public class ShadowPantherSoundHandler {

    private static int stalkCooldown = 0;
    private static int circleCooldown = 0;
    private static int attackCooldown = 0;

    private static final double SEARCH_RADIUS = 24.0D;
    private static final double STALK_RANGE = 18.0D;
    private static final double CIRCLE_RANGE = 14.0D;
    private static final double ATTACK_RANGE = 12.0D;

    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (player == null || player.level() == null || player.isSpectator()) return;

        if (stalkCooldown > 0) stalkCooldown--;
        if (circleCooldown > 0) circleCooldown--;
        if (attackCooldown > 0) attackCooldown--;

        List<ShadowPantherEntity> panthers = player.level().getEntitiesOfClass(
                ShadowPantherEntity.class,
                player.getBoundingBox().inflate(SEARCH_RADIUS),
                panther -> panther.isAlive() && !panther.isTame()
        );

        if (panthers.isEmpty()) return;

        ShadowPantherEntity panther = panthers.stream()
                .filter(p -> p.getTarget() == player || p.distanceTo(player) <= STALK_RANGE)
                .min(Comparator.comparingDouble(p -> p.distanceToSqr(player)))
                .orElse(null);

        if (panther == null) return;

        double distance = Math.sqrt(panther.distanceToSqr(player));
        boolean targetingPlayer = panther.getTarget() == player;

        // Highest priority: attack
        if (panther.isAttacking() && targetingPlayer && distance <= ATTACK_RANGE) {
            if (attackCooldown <= 0) {
                float volume = Mth.clamp(1.15F - (float)(distance / ATTACK_RANGE), 0.45F, 1.0F);

                player.level().playLocalSound(
                        panther.getX(),
                        panther.getY(),
                        panther.getZ(),
                        ModSounds.SHADOW_PANTHER_ATTACK_STINGER.get(),
                        SoundSource.HOSTILE,
                        volume,
                        1.0F,
                        false
                );

                attackCooldown = 30;
            }
            return;
        }

        // Medium priority: stalking
        if (panther.isStalking() && distance <= STALK_RANGE) {
            if (stalkCooldown <= 0) {
                float volume = Mth.clamp(0.95F - (float)(distance / STALK_RANGE), 0.20F, 0.75F);

                player.level().playLocalSound(
                        panther.getX(),
                        panther.getY(),
                        panther.getZ(),
                        ModSounds.SHADOW_PANTHER_STALK.get(),
                        SoundSource.HOSTILE,
                        volume,
                        0.92F,
                        false
                );

                stalkCooldown = 90;
            }
            return;
        }

        // Lowest priority: circling pressure
        if (panther.isCircling() && targetingPlayer && distance <= CIRCLE_RANGE) {
            if (circleCooldown <= 0) {
                float volume = Mth.clamp(0.85F - (float)(distance / CIRCLE_RANGE), 0.18F, 0.60F);

                player.level().playLocalSound(
                        panther.getX(),
                        panther.getY(),
                        panther.getZ(),
                        ModSounds.SHADOW_PANTHER_PRESSURE.get(),
                        SoundSource.HOSTILE,
                        volume,
                        0.96F,
                        false
                );

                circleCooldown = 70;
            }
        }
    }
}