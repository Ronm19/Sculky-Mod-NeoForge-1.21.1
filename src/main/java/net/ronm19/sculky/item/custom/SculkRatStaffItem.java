package net.ronm19.sculky.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.ronm19.sculky.entity.command.RatCommandMode;
import net.ronm19.sculky.entity.custom.SculkRatEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class SculkRatStaffItem extends Item {

    public static final int COMMAND_RADIUS = 96;
    private static final String STAFF_MODE_NBT = "RatStaffMode";

    public SculkRatStaffItem(Properties props) {
        super(props);
    }

    /* ===================== RIGHT CLICK AIR ===================== */

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);
        if (level.isClientSide) return InteractionResultHolder.pass(stack);
        if (!(level instanceof ServerLevel sl)) return InteractionResultHolder.pass(stack);

        // Recall
        if (user.isShiftKeyDown()) {
            return recallAndReport(sl, user, stack);
        }

        return cycleAndApply(sl, user, stack);
    }

    /* ===================== RIGHT CLICK BLOCK ===================== */

    @Override
    public @NotNull InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        Player user = ctx.getPlayer();
        if (user == null) return InteractionResult.PASS;
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!(level instanceof ServerLevel sl)) return InteractionResult.PASS;

        // Recall
        if (user.isShiftKeyDown()) {
            InteractionResultHolder<ItemStack> res = recallAndReport(sl, user, ctx.getItemInHand());
            return res.getResult() == InteractionResult.CONSUME ? InteractionResult.CONSUME : InteractionResult.PASS;
        }

        InteractionResultHolder<ItemStack> res = cycleAndApply(sl, user, ctx.getItemInHand());
        return res.getResult() == InteractionResult.CONSUME ? InteractionResult.CONSUME : InteractionResult.PASS;
    }

    /* ===================== RIGHT CLICK ENTITY ===================== */

    @Override
    public @NotNull InteractionResult interactLivingEntity(ItemStack stack, Player user, LivingEntity target, InteractionHand hand) {
        if (user.level().isClientSide) return InteractionResult.SUCCESS;
        if (!(user.level() instanceof ServerLevel sl)) return InteractionResult.PASS;

        commandOwnedRatsAttack(user, target, sl, COMMAND_RADIUS);
        user.getCooldowns().addCooldown(this, 10);
        return InteractionResult.CONSUME;
    }

    /* ===================== CYCLE + APPLY (WITH COUNTS) ===================== */

    private InteractionResultHolder<ItemStack> cycleAndApply(ServerLevel level, Player user, ItemStack staffStack) {
        List<SculkRatEntity> rats = getOwnedRatsInRange(level, user, COMMAND_RADIUS);
        if (rats.isEmpty()) {
            user.displayClientMessage(Component.literal("No Sculk Rats nearby to command!"), true);
            return InteractionResultHolder.pass(staffStack);
        }

        RatCommandMode current = getStaffMode(staffStack);
        RatCommandMode next = current.next();
        setStaffMode(staffStack, next);

        int acknowledged = rats.size();
        int changed = 0;

        for (SculkRatEntity r : rats) {
            if (r.getCommand() != next) changed++;
            r.applyCommand(next);

            // Make FOLLOW visibly happen immediately
            if (next == RatCommandMode.FOLLOW) {
                r.forceFollowNow();
            }
        }

        user.displayClientMessage(
                Component.literal("Sculk Rats: " + next.name() + " (" + acknowledged + " acknowledged, " + changed + " changed)"),
                true
        );

        level.playSound(null, user.blockPosition(), SoundEvents.SCULK_SHRIEKER_PLACE, SoundSource.PLAYERS, 0.9F, 1.2F);
        user.getCooldowns().addCooldown(this, 10);

        return InteractionResultHolder.consume(staffStack);
    }

    /* ===================== RECALL (WITH COUNTS) ===================== */

    private InteractionResultHolder<ItemStack> recallAndReport(ServerLevel level, Player user, ItemStack stack) {
        // Count how many are in range (acknowledged)
        List<SculkRatEntity> rats = getOwnedRatsInRange(level, user, COMMAND_RADIUS);
        if (rats.isEmpty()) {
            user.displayClientMessage(Component.literal("No Sculk Rats nearby to recall!"), true);
            return InteractionResultHolder.pass(stack);
        }

        // Teleport count = only those far enough (same rule used by recall method: <=16 dist^2 skip)
        int teleported = 0;
        for (SculkRatEntity r : rats) {
            if (r.distanceToSqr(user) > 16.0D) teleported++;
        }

        // Do the actual recall
        SculkRatEntity.recallAllOwnedRats(level, user);

        user.displayClientMessage(
                Component.literal("Sculk Rats: RECALL (" + rats.size() + " acknowledged, " + teleported + " teleported)"),
                true
        );
        level.playSound(null, user.blockPosition(), SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.PLAYERS, 0.9F, 1.1F);
        user.getCooldowns().addCooldown(this, 10);

        return InteractionResultHolder.consume(stack);
    }

    /* ===================== ATTACK ORDER (WITH COUNTS) ===================== */

    public static void commandOwnedRatsAttack(Player player, LivingEntity target, ServerLevel level, int radius) {
        List<SculkRatEntity> rats = getOwnedRatsInRange(level, player, radius);
        if (rats.isEmpty()) {
            player.displayClientMessage(Component.literal("No Sculk Rats nearby to command!"), true);
            return;
        }

        int acknowledged = rats.size();
        int ordered = 0;

        for (SculkRatEntity rat : rats) {
            if (rat == target) continue;
            if (!target.isAlive()) break;

            rat.orderAttack(target);
            ordered++;
        }

        player.displayClientMessage(
                Component.literal("Sculk Rats: ATTACK " + target.getName().getString() + " (" + acknowledged + " acknowledged, " + ordered + " ordered)"),
                true
        );
        level.playSound(null, player.blockPosition(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 0.35F, 1.6F);
    }

    /* ===================== HELPERS ===================== */

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

    private static RatCommandMode getStaffMode(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return RatCommandMode.FOLLOW;

        CompoundTag tag = data.copyTag();
        return RatCommandMode.byId(tag.getInt(STAFF_MODE_NBT));
    }

    private static void setStaffMode(ItemStack stack, RatCommandMode mode) {
        CustomData existing = stack.get(DataComponents.CUSTOM_DATA);
        CompoundTag tag = (existing != null) ? existing.copyTag() : new CompoundTag();

        tag.putInt(STAFF_MODE_NBT, mode.id);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
}
