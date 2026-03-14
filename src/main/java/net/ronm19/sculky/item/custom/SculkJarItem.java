package net.ronm19.sculky.item.custom;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.ronm19.sculky.entity.ModEntities;
import net.ronm19.sculky.entity.projectile.SculkJarProjectileEntity;

public class SculkJarItem extends Item {

    public SculkJarItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.SPLASH_POTION_THROW,
                SoundSource.PLAYERS,
                0.8F,
                0.9F + level.random.nextFloat() * 0.2F);

        player.swing(hand);

        if (!level.isClientSide()) {
            SculkJarProjectileEntity jar = new SculkJarProjectileEntity(ModEntities.SCULK_JAR_PROJECTILE.get(), level);
            jar.setOwner(player);
            jar.setPos(player.getX(), player.getEyeY() - 0.1D, player.getZ());

            ItemStack singleJar = stack.copy();
            singleJar.setCount(1);
            jar.setItem(singleJar);

            jar.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.0F, 3.0F);
            level.addFreshEntity(jar);

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}