package net.ronm19.sculky.item.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ExecutionerCleaverItem extends AxeItem {
    private static final int SLAM_COOLDOWN_TICKS = 20 * 16;

    private static final float BONUS_HIT_DAMAGE = 4.0F;
    private static final float EXECUTION_BONUS_DAMAGE = 5.0F;
    private static final float SLAM_DAMAGE = 7.0F;

    private static final double HIT_KNOCKBACK = 1.45D;
    private static final double SLAM_KNOCKBACK = 1.75D;
    private static final double SLAM_RADIUS = 4.25D;

    public ExecutionerCleaverItem(Tier tier, Properties properties) {
        super(tier, properties);
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack,
                             @NotNull LivingEntity target,
                             @NotNull LivingEntity attacker) {
        if (!attacker.level().isClientSide) {
            double dx = target.getX() - attacker.getX();
            double dz = target.getZ() - attacker.getZ();

            target.knockback(HIT_KNOCKBACK, -dx, -dz);

            DamageSource damageSource = getExecutionDamageSource(attacker);

            // Extra brutal cleaver damage.
            target.hurt(damageSource, BONUS_HIT_DAMAGE);

            // Execution identity: hits harder when the target is already weakened.
            if (target.getHealth() <= target.getMaxHealth() * 0.35F) {
                target.hurt(damageSource, EXECUTION_BONUS_DAMAGE);
            }

            if (attacker.level() instanceof ServerLevel serverLevel) {
                serverLevel.playSound(
                        null,
                        target.blockPosition(),
                        SoundEvents.WARDEN_ATTACK_IMPACT,
                        SoundSource.PLAYERS,
                        0.9F,
                        0.72F
                );

                serverLevel.sendParticles(
                        ParticleTypes.SCULK_SOUL,
                        target.getX(),
                        target.getY() + 0.9D,
                        target.getZ(),
                        18,
                        0.35D,
                        0.35D,
                        0.35D,
                        0.035D
                );
            }
        }

        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level,
                                                           @NotNull Player player,
                                                           @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResultHolder.success(stack);
        }

        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }

        executionSlam(serverLevel, player);

        player.getCooldowns().addCooldown(this, SLAM_COOLDOWN_TICKS);

        return InteractionResultHolder.success(stack);
    }

    private void executionSlam(ServerLevel level, Player player) {
        AABB area = player.getBoundingBox().inflate(SLAM_RADIUS);

        List<LivingEntity> targets = level.getEntitiesOfClass(
                LivingEntity.class,
                area,
                entity -> entity.isAlive()
                        && entity != player
                        && !entity.isAlliedTo(player)
                        && !(entity instanceof TamableAnimal tamable && tamable.isOwnedBy(player))
        );

        for (LivingEntity target : targets) {
            target.hurt(player.damageSources().playerAttack(player), SLAM_DAMAGE);

            target.knockback(
                    SLAM_KNOCKBACK,
                    player.getX() - target.getX(),
                    player.getZ() - target.getZ()
            );

            level.sendParticles(
                    ParticleTypes.SCULK_SOUL,
                    target.getX(),
                    target.getY() + 0.8D,
                    target.getZ(),
                    10,
                    0.25D,
                    0.25D,
                    0.25D,
                    0.03D
            );
        }

        level.playSound(
                null,
                player.blockPosition(),
                SoundEvents.WARDEN_ATTACK_IMPACT,
                SoundSource.PLAYERS,
                1.05F,
                0.62F
        );

        level.playSound(
                null,
                player.blockPosition(),
                SoundEvents.SCULK_SHRIEKER_SHRIEK,
                SoundSource.PLAYERS,
                0.55F,
                0.85F
        );

        level.sendParticles(
                ParticleTypes.SONIC_BOOM,
                player.getX(),
                player.getY() + 1.0D,
                player.getZ(),
                1,
                0.0D,
                0.0D,
                0.0D,
                0.0D
        );

        level.sendParticles(
                ParticleTypes.SCULK_SOUL,
                player.getX(),
                player.getY() + 1.0D,
                player.getZ(),
                55,
                0.75D,
                0.45D,
                0.75D,
                0.055D
        );
    }

    private DamageSource getExecutionDamageSource(LivingEntity attacker) {
        if (attacker instanceof Player player) {
            return player.damageSources().playerAttack(player);
        }

        return attacker.damageSources().mobAttack(attacker);
    }
}