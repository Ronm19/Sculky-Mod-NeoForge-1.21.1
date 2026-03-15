package net.ronm19.sculky.item.custom;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.ronm19.sculky.entity.projectile.SculkBombProjectileEntity;

public class SculkBombItem extends Item {

    public SculkBombItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS,
                0.7F, 0.9F + level.random.nextFloat() * 0.2F);

        if (!level.isClientSide) {
            SculkBombProjectileEntity bomb = new SculkBombProjectileEntity(level, player);

            ItemStack single = stack.copy();
            single.setCount(1);
            bomb.setItem(single);

            bomb.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.25F, 1.0F);
            level.addFreshEntity(bomb);
        }

        player.getCooldowns().addCooldown(this, 20);

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}