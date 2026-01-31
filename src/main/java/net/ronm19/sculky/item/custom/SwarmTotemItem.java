package net.ronm19.sculky.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.ronm19.sculky.entity.command.RatCommandMode;
import net.ronm19.sculky.entity.custom.SculkRatEntity;

import java.util.List;
import java.util.UUID;

public class SwarmTotemItem extends Item {

    public static final int COMMAND_RADIUS = 120;

    public SwarmTotemItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);
        if (level.isClientSide) return InteractionResultHolder.success(stack);
        if (!(level instanceof ServerLevel sl)) return InteractionResultHolder.pass(stack);

        List<SculkRatEntity> rats = getOwnedRatsInRange(sl, user, COMMAND_RADIUS);
        if (rats.isEmpty()) {
            user.displayClientMessage(Component.literal("No Sculk Rats nearby to command!"), true);
            return InteractionResultHolder.pass(stack);
        }

        for (SculkRatEntity r : rats) {
            r.setOrderedToSit(false);
            r.applyCommand(RatCommandMode.KILL_ON_SIGHT);
            r.setTarget(null);
            r.setAggressive(false);
        }

        user.displayClientMessage(Component.literal("Totem: Swarm Mode (KILL ON SIGHT)"), true);
        sl.playSound(null, user.blockPosition(), SoundEvents.SCULK_SHRIEKER_PLACE, SoundSource.PLAYERS, 0.9F, 1.2F);
        sl.sendParticles(ParticleTypes.SCULK_CHARGE_POP, user.getX(), user.getY() + 1.0, user.getZ(), 30, 0.55, 0.55, 0.55, 0.02);

        user.getCooldowns().addCooldown(this, 60); // 3 seconds
        return InteractionResultHolder.consume(stack);
    }

    private static List<SculkRatEntity> getOwnedRatsInRange(ServerLevel level, Player owner, int radius) {
        UUID id = owner.getUUID();
        BlockPos c = owner.blockPosition();
        AABB box = new AABB(
                c.getX() - radius, c.getY() - 64, c.getZ() - radius,
                c.getX() + radius, c.getY() + 64, c.getZ() + radius
        );

        return level.getEntitiesOfClass(
                SculkRatEntity.class,
                box,
                r -> r.isAlive() && r.isTame() && id.equals(r.getOwnerUUID())
        );
    }
}
