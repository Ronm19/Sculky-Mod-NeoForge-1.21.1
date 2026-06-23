package net.ronm19.sculky.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.ronm19.sculky.entity.ModEntities;
import net.ronm19.sculky.entity.custom.InfestedEyeEntity;
import net.ronm19.sculky.entity.custom.SculkFoxEntity;
import net.ronm19.sculky.entity.custom.SculkWolfAlphaEntity;
import net.ronm19.sculky.entity.custom.SculkWolfEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class KingsAxeItem extends InfestedSculkAxeItem {
    private static final int SUMMON_COOLDOWN_TICKS = 20 * 30;

    private static final float BONUS_HIT_DAMAGE = 7.0F;
    private static final float SHOCKWAVE_DAMAGE = 5.0F;

    private static final double DIRECT_KNOCKBACK = 2.35D;
    private static final double SHOCKWAVE_KNOCKBACK = 1.35D;

    private static final double HIT_SHOCKWAVE_RADIUS = 3.5D;
    private static final double COMMAND_RADIUS = 7.0D;

    public KingsAxeItem(Tier tier, Properties properties) {
        super(tier, properties);
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target,
                             @NotNull LivingEntity attacker) {
        if (!attacker.level().isClientSide) {
            double dx = target.getX() - attacker.getX();
            double dz = target.getZ() - attacker.getZ();

            target.knockback(DIRECT_KNOCKBACK, -dx, -dz);

            // Extra royal sculk damage on the main target.
            target.hurt(attacker.damageSources().mobAttack(attacker), BONUS_HIT_DAMAGE);

            if (attacker.level() instanceof ServerLevel serverLevel) {
                doHitShockwave(serverLevel, attacker, target);

                serverLevel.playSound(
                        null,
                        target.blockPosition(),
                        SoundEvents.WARDEN_ATTACK_IMPACT,
                        SoundSource.PLAYERS,
                        1.15F,
                        0.65F
                );

                serverLevel.playSound(
                        null,
                        target.blockPosition(),
                        SoundEvents.SCULK_SHRIEKER_SHRIEK,
                        SoundSource.PLAYERS,
                        0.35F,
                        1.25F
                );

                serverLevel.sendParticles(
                        ParticleTypes.SCULK_SOUL,
                        target.getX(),
                        target.getY() + 1.0D,
                        target.getZ(),
                        32,
                        0.5D,
                        0.45D,
                        0.5D,
                        0.055D
                );

                serverLevel.sendParticles(
                        ParticleTypes.SONIC_BOOM,
                        target.getX(),
                        target.getY() + 1.0D,
                        target.getZ(),
                        1,
                        0.0D,
                        0.0D,
                        0.0D,
                        0.0D
                );
            }
        }

        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player,
                                                           @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResultHolder.success(stack);
        }

        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }

        summonRoyalAllies(serverLevel, player);
        commandShockwave(serverLevel, player);

        player.getCooldowns().addCooldown(this, SUMMON_COOLDOWN_TICKS);

        serverLevel.playSound(
                null,
                player.blockPosition(),
                SoundEvents.SCULK_SHRIEKER_SHRIEK,
                SoundSource.PLAYERS,
                1.15F,
                0.55F
        );

        serverLevel.playSound(
                null,
                player.blockPosition(),
                SoundEvents.WARDEN_SONIC_CHARGE,
                SoundSource.PLAYERS,
                0.95F,
                1.05F
        );

        serverLevel.playSound(
                null,
                player.blockPosition(),
                SoundEvents.BEACON_ACTIVATE,
                SoundSource.PLAYERS,
                0.65F,
                0.65F
        );

        serverLevel.sendParticles(
                ParticleTypes.SCULK_SOUL,
                player.getX(),
                player.getY() + 1.0D,
                player.getZ(),
                90,
                1.1D,
                0.75D,
                1.1D,
                0.075D
        );

        serverLevel.sendParticles(
                ParticleTypes.SONIC_BOOM,
                player.getX(),
                player.getY() + 1.1D,
                player.getZ(),
                1,
                0.0D,
                0.0D,
                0.0D,
                0.0D
        );

        return InteractionResultHolder.success(stack);
    }

    private void doHitShockwave(ServerLevel level, LivingEntity attacker, LivingEntity mainTarget) {
        AABB area = mainTarget.getBoundingBox().inflate(HIT_SHOCKWAVE_RADIUS);

        List<LivingEntity> nearby = level.getEntitiesOfClass(LivingEntity.class, area,
                entity -> entity.isAlive()
                        && entity != attacker
                        && entity != mainTarget
                        && !entity.isAlliedTo(attacker));

        for (LivingEntity entity : nearby) {
            entity.hurt(attacker.damageSources().mobAttack(attacker), SHOCKWAVE_DAMAGE);

            entity.knockback(
                    SHOCKWAVE_KNOCKBACK,
                    attacker.getX() - entity.getX(),
                    attacker.getZ() - entity.getZ()
            );

            level.sendParticles(
                    ParticleTypes.SCULK_SOUL,
                    entity.getX(),
                    entity.getY() + 0.8D,
                    entity.getZ(),
                    8,
                    0.25D,
                    0.25D,
                    0.25D,
                    0.025D
            );
        }
    }

    private void commandShockwave(ServerLevel level, Player player) {
        AABB area = player.getBoundingBox().inflate(COMMAND_RADIUS);

        List<LivingEntity> nearby = level.getEntitiesOfClass(LivingEntity.class, area,
                entity -> entity.isAlive()
                        && entity != player
                        && !entity.isAlliedTo(player)
                        && !(entity instanceof TamableAnimal tamable && tamable.isOwnedBy(player)));

        for (LivingEntity entity : nearby) {
            entity.hurt(player.damageSources().playerAttack(player), 8.0F);

            entity.knockback(
                    1.85D,
                    player.getX() - entity.getX(),
                    player.getZ() - entity.getZ()
            );

            level.sendParticles(
                    ParticleTypes.SCULK_SOUL,
                    entity.getX(),
                    entity.getY() + 1.0D,
                    entity.getZ(),
                    12,
                    0.35D,
                    0.35D,
                    0.35D,
                    0.035D
            );
        }
    }

    private void summonRoyalAllies(ServerLevel level, Player player) {
        BlockPos basePos = player.blockPosition();

        spawnSculkWolf(level, player, basePos.offset(2, 0, 0));
        spawnSculkWolf(level, player, basePos.offset(-2, 0, 0));
        spawnSculkFox(level, player, basePos.offset(0, 0, -2));

        spawnInfestedEye(level, player, basePos.offset(2, 0, 2));
        spawnInfestedEye(level, player, basePos.offset(-2, 0, 2));
    }

    private void spawnSculkWolf(ServerLevel level, Player player, BlockPos pos) {
        SculkWolfEntity wolf = ModEntities.SCULK_WOLF.get().create(level);

        if (wolf == null) {
            return;
        }

        wolf.moveTo(pos.getX() + 0.5D, pos.getY() + 0.1D, pos.getZ() + 0.5D,
                player.getYRot(), 0.0F);

        if (wolf instanceof TamableAnimal tamable) {
            tamable.tame(player);
            tamable.setOrderedToSit(false);
        }

        wolf.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.MOB_SUMMONED, null);
        level.addFreshEntity(wolf);
    }

    private void spawnSculkFox(ServerLevel level, Player player, BlockPos pos) {
        SculkFoxEntity alpha = ModEntities.SCULK_FOX.get().create(level);

        if (alpha == null) {
            return;
        }

        alpha.moveTo(pos.getX() + 0.5D, pos.getY() + 0.1D, pos.getZ() + 0.5D,
                player.getYRot(), 0.0F);

        if (alpha instanceof TamableAnimal tamable) {
            tamable.tame(player);
            tamable.setOrderedToSit(false);
        }

        alpha.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.MOB_SUMMONED, null);
        level.addFreshEntity(alpha);
    }

    private void spawnInfestedEye(ServerLevel level, Player player, BlockPos pos) {
        InfestedEyeEntity eye = ModEntities.INFESTED_EYE.get().create(level);

        if (eye == null) {
            return;
        }

        eye.moveTo(pos.getX() + 0.5D, pos.getY() + 0.4D, pos.getZ() + 0.5D,
                player.getYRot(), 0.0F);

        eye.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.MOB_SUMMONED, null);
        level.addFreshEntity(eye);
    }
}