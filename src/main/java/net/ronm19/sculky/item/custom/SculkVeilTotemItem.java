package net.ronm19.sculky.item.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SculkVeilTotemItem extends Item {

    public SculkVeilTotemItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);
        if (level.isClientSide) return InteractionResultHolder.success(stack);
        if (!(level instanceof ServerLevel sl)) return InteractionResultHolder.pass(stack);

        // 10 seconds invis + resistance (tweak freely)
        user.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 20 * 10, 0, false, false, true));
        user.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 10, 0, false, true, true));
        user.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 10, 0, false, true, true));

        user.displayClientMessage(Component.literal("Totem: Sculk Veil"), true);
        sl.playSound(null, user.blockPosition(), SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.PLAYERS, 0.55F, 1.6F);
        sl.sendParticles(ParticleTypes.SCULK_SOUL, user.getX(), user.getY() + 1.0, user.getZ(), 18, 0.35, 0.5, 0.35, 0.01);

        user.getCooldowns().addCooldown(this, 20 * 25); // 25 sec cooldown
        return InteractionResultHolder.consume(stack);
    }
}
