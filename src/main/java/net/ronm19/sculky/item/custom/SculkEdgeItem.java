package net.ronm19.sculky.item.custom;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;

public class SculkEdgeItem extends SwordItem {

    public SculkEdgeItem(Tier tier, Properties properties) {
        super(tier, properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        Level level = attacker.level();

        // --- Infection and debuffs ---
        target.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 2));
        target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0));
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 1));

        // --- Sculk particles ---
        if (level.isClientSide()) {
            for (int i = 0; i < 10; i++) {
                double offsetX = (level.random.nextDouble() - 0.5D) * 0.5D;
                double offsetY = (level.random.nextDouble()) * 1.0D;
                double offsetZ = (level.random.nextDouble() - 0.5D) * 0.5D;
                level.addParticle(ParticleTypes.SCULK_SOUL,
                        target.getX() + offsetX,
                        target.getY() + offsetY,
                        target.getZ() + offsetZ,
                        0, 0.01, 0);
            }
        }

        // --- Audio feedback ---
        level.playSound(null, target.blockPosition(),
                SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.PLAYERS,
                1.0F, 0.8F + level.random.nextFloat() * 0.2F);

        // --- Self corruption ---
        if (attacker instanceof Player player && level.random.nextFloat() < 0.15F) {
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 40, 0));
        }

        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        ItemCooldowns cooldowns = player.getCooldowns();

        if (!cooldowns.isOnCooldown(this)) {
            if (!level.isClientSide()) {
                echoBurst(level, player);
            }
            cooldowns.addCooldown(this, 120); // 6 seconds cooldown
        }

        return InteractionResultHolder.success(itemstack);
    }

    private void echoBurst(Level level, Player player) {
        double radius = 5.0D;

        // --- Infect and damage enemies in range ---
        level.getEntities(player, player.getBoundingBox().inflate(radius)).forEach(entity -> {
            if (entity instanceof LivingEntity target && entity != player) {
                target.hurt(player.damageSources().magic(), 6.0F);
                target.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 1));
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2));
            }
        });

        // --- Particle ring ---
        if (level.isClientSide()) {
            for (int i = 0; i < 60; i++) {
                double angle = i * (Math.PI / 15);
                double x = player.getX() + Math.cos(angle) * radius * (0.7 + level.random.nextDouble() * 0.3);
                double z = player.getZ() + Math.sin(angle) * radius * (0.7 + level.random.nextDouble() * 0.3);
                double y = player.getY() + 0.2 + level.random.nextDouble() * 1.5;
                level.addParticle(ParticleTypes.SCULK_SOUL, x, y, z, 0, 0.03, 0);
            }
        }

        // --- Sculk sounds ---
        BlockPos pos = player.blockPosition();
        level.playSound(null, pos, SoundEvents.SCULK_BLOCK_BREAK, SoundSource.PLAYERS, 2.0F, 0.5F);
        level.playSound(null, pos, SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.PLAYERS, 1.0F, 0.6F);

        // --- Buffs for player ---
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, 1));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 1));
    }
}
