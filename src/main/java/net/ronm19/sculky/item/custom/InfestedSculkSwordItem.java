package net.ronm19.sculky.item.custom;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class InfestedSculkSwordItem extends SwordItem {

    public InfestedSculkSwordItem(Tier tier, Properties properties) {
        super(tier, properties);
    }

    @Override
    public boolean hurtEnemy( @NotNull ItemStack stack, @NotNull LivingEntity target, LivingEntity attacker) {
        // Add Sculk-like debuff effect on hit
        if (!attacker.level().isClientSide()) {
            target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 100, 0)); // 5 seconds
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1)); // brief slow
        }

        return super.hurtEnemy(stack, target, attacker);
    }
}
