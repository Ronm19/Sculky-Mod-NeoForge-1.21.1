package net.ronm19.sculky.event;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.ronm19.sculky.SculkyMod;
import net.ronm19.sculky.item.ModItems;

@EventBusSubscriber(modid = SculkyMod.MOD_ID)
public class SculkBowEvents {

    private static final String SCULK_BOW_ARROW_TAG = "SculkySculkBowArrow";

    private static final double SCULK_BOW_VELOCITY_MULTIPLIER = 1.45D;

    @SubscribeEvent
    public static void onArrowSpawn(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof AbstractArrow arrow)) {
            return;
        }

        if (!(arrow.getOwner() instanceof Player player)) {
            return;
        }

        if (!isHoldingSculkBow(player)) {
            return;
        }

        // Mark the arrow so effects still work after the player switches items.
        arrow.getPersistentData().putBoolean(SCULK_BOW_ARROW_TAG, true);

        // Buff: stronger than vanilla bow, but not ridiculous.
        arrow.setBaseDamage(arrow.getBaseDamage() + 1.75D);

        // Velocity buff.
        arrow.setDeltaMovement(arrow.getDeltaMovement().scale(SCULK_BOW_VELOCITY_MULTIPLIER));
    }

    @SubscribeEvent
    public static void onSculkBowArrowImpact(ProjectileImpactEvent event) {
        if (!(event.getProjectile() instanceof AbstractArrow arrow)) {
            return;
        }

        if (!arrow.getPersistentData().getBoolean(SCULK_BOW_ARROW_TAG)) {
            return;
        }

        if (!(event.getRayTraceResult() instanceof EntityHitResult entityHitResult)) {
            return;
        }

        if (!(entityHitResult.getEntity() instanceof LivingEntity target)) {
            return;
        }

        if (target.level().isClientSide) {
            return;
        }

        target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 80, 0));
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 0));

        // Custom sculk knockback.
        if (arrow.getOwner() instanceof LivingEntity shooter) {
            Vec3 away = target.position().subtract(shooter.position());

            if (away.lengthSqr() > 0.0001D) {
                away = away.normalize();

                target.push(
                        away.x * 0.45D,
                        0.18D,
                        away.z * 0.45D
                );

                target.hurtMarked = true;
            }
        }

        if (target.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.SCULK_SOUL,
                    target.getX(),
                    target.getY() + target.getBbHeight() * 0.55D,
                    target.getZ(),
                    10,
                    0.25D,
                    0.35D,
                    0.25D,
                    0.025D
            );
        }
    }

    private static boolean isHoldingSculkBow(Player player) {
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();

        return mainHand.is(ModItems.SCULK_BOW.get()) || offHand.is(ModItems.SCULK_BOW.get());
    }
}