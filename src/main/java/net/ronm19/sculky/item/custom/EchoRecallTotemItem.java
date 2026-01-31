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
import net.ronm19.sculky.entity.custom.SculkRatEntity;

import java.util.List;
import java.util.UUID;

public class EchoRecallTotemItem extends Item {

    public static final int RECALL_RADIUS = 160;

    public EchoRecallTotemItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);
        if (level.isClientSide) return InteractionResultHolder.success(stack);

        if (!(level instanceof ServerLevel sl)) return InteractionResultHolder.pass(stack);

        // Only recall if you actually have rats nearby (avoid spam)
        List<SculkRatEntity> rats = getOwnedRatsInRange(sl, user, RECALL_RADIUS);
        if (rats.isEmpty()) {
            user.displayClientMessage(Component.literal("No Sculk Rats nearby to recall."), true);
            return InteractionResultHolder.pass(stack);
        }

        SculkRatEntity.recallAllOwnedRats(sl, user);

        user.displayClientMessage(Component.literal("Totem: Echo Recall"), true);
        sl.playSound(null, user.blockPosition(), SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.PLAYERS, 0.9F, 1.15F);
        sl.sendParticles(ParticleTypes.SCULK_SOUL, user.getX(), user.getY() + 1.0, user.getZ(), 24, 0.45, 0.45, 0.45, 0.02);

        user.getCooldowns().addCooldown(this, 40); // 2 seconds
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
