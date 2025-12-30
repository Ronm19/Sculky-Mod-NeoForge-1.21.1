package net.ronm19.sculky.item.custom;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;

public class EchoDaggerItem extends SwordItem {
    public EchoDaggerItem( Tier tier, Properties properties ) {
        super(tier, properties);
    }

    @Override
    public boolean hurtEnemy( ItemStack stack, LivingEntity target, LivingEntity attacker) {

        // Apply effect only on server
        if (!target.level().isClientSide) {
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 0));
        }

        return super.hurtEnemy(stack, target, attacker);
    }
}
