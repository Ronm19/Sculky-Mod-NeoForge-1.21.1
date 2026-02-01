package net.ronm19.sculky.enchantment.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.ronm19.sculky.effect.ModEffects;
import org.jetbrains.annotations.NotNull;

public record InfectionEnchantmentEffect(int level) implements EnchantmentEntityEffect {
    public static final MapCodec<InfectionEnchantmentEffect> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(Codec.INT.fieldOf("level").forGetter(InfectionEnchantmentEffect::level))
                    .apply(instance, InfectionEnchantmentEffect::new));

    @Override
    public void apply(ServerLevel level, int enchantmentLevel, @NotNull EnchantedItemInUse item, @NotNull Entity target, @NotNull Vec3 hitPos) {
        // Only trigger on living entities
        if (!(target instanceof LivingEntity livingTarget)) return;

        // Infection chance based on enchantment level
        float chance = 0.15F + (0.10F * enchantmentLevel); // Level 1 = 15%, Level 3 = 35%
        if (level.random.nextFloat() < chance) {
            // Apply your custom Sculk Infection effect
            livingTarget.addEffect(new MobEffectInstance(ModEffects.SCULK_INFECTION_EFFECT, 200 + 40 * enchantmentLevel, enchantmentLevel - 1));

            // Optional: Add sound or particles here
            level.sendParticles(net.minecraft.core.particles.ParticleTypes.SCULK_SOUL,
                    livingTarget.getX(), livingTarget.getY() + 1, livingTarget.getZ(),
                    6, 0.2, 0.3, 0.2, 0.01);
        }
    }

    @Override
    public @NotNull MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
