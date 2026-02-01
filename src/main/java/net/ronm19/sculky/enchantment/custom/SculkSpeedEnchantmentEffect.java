package net.ronm19.sculky.enchantment.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.ronm19.sculky.util.ModTags;
import org.jetbrains.annotations.NotNull;

public record SculkSpeedEnchantmentEffect(int level) implements EnchantmentEntityEffect {

    public static final MapCodec<SculkSpeedEnchantmentEffect> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.INT.fieldOf("level").forGetter(SculkSpeedEnchantmentEffect::level)
            ).apply(instance, SculkSpeedEnchantmentEffect::new)
    );

    @Override
    public void apply(ServerLevel level, int enchantmentLevel, @NotNull EnchantedItemInUse item,
                      @NotNull Entity target, @NotNull Vec3 hitPos) {

        if (!(target instanceof LivingEntity living)) return;

        // Boots check
        ItemStack boots = living.getItemBySlot(EquipmentSlot.FEET);
        if (boots.isEmpty()) return;

        // Optional: only while on ground
        if (!living.onGround()) return;

        // Block under feet
        BlockPos below = living.blockPosition().below();
        BlockState state = level.getBlockState(below);
        if (!state.is(ModTags.Blocks.SCULK_SPEED_BLOCKS)) return;

        // Speed effect: refreshed every tick by the enchantment TICK component
        int amplifier = Mth.clamp(enchantmentLevel - 1, 0, 2); // I->0, II->1, III->2
        living.addEffect(new MobEffectInstance(
                MobEffects.MOVEMENT_SPEED,
                25,
                amplifier,
                true,   // ambient
                false,  // show particles
                true    // show icon
        ));

        // Durability cost (Soul Speed-like): once per second while moving
        if (living.tickCount % 20 == 0 && living.getDeltaMovement().horizontalDistanceSqr() > 0.001) {
            float chance = 0.08F + 0.04F * (enchantmentLevel - 1); // 8%, 12%, 16%
            if (level.random.nextFloat() < chance) {
                boots.hurtAndBreak(1, living, EquipmentSlot.FEET);
            }
        }
    }

    @Override
    public @NotNull MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
