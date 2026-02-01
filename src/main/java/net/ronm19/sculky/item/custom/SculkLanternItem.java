package net.ronm19.sculky.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.ronm19.sculky.effect.ModEffects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SculkLanternItem extends Item {

    // --- NBT keys ---
    private static final String NBT_MODE = "Mode";
    private static final String NBT_WARNING = "WarningEnabled";

    // --- Modes ---
    // 0 = PING, 1 = CLEANSE, 2 = WARNING (toggle)
    private static final int MODE_PING = 0;
    private static final int MODE_CLEANSE = 1;
    private static final int MODE_WARNING = 2;

    // --- Tuning ---
    private static final int PING_RADIUS = 18;
    private static final int PING_GLOW_DURATION_TICKS = 60;  // 3s
    private static final int CLEANSE_COOLDOWN_TICKS = 20 * 18; // 18s
    private static final int PING_COOLDOWN_TICKS = 20 * 10;    // 10s
    private static final int WARNING_RADIUS = 14;

    public SculkLanternItem(Properties props) {
        super(props);
    }

    // ---- Mode & NBT helpers (using CustomData / DataComponents like your staff) ----

    private static CompoundTag getOrCreateTag(ItemStack stack) {
        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = data.copyTag();
        return tag;
    }

    private static void saveTag(ItemStack stack, CompoundTag tag) {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    private static int getMode(ItemStack stack) {
        CompoundTag tag = getOrCreateTag(stack);
        return tag.getInt(NBT_MODE);
    }

    private static void setMode(ItemStack stack, int mode) {
        CompoundTag tag = getOrCreateTag(stack);
        tag.putInt(NBT_MODE, mode);
        saveTag(stack, tag);
    }

    private static boolean isWarningEnabled(ItemStack stack) {
        CompoundTag tag = getOrCreateTag(stack);
        // default true
        return !tag.contains(NBT_WARNING) || tag.getBoolean(NBT_WARNING);
    }

    private static void setWarningEnabled(ItemStack stack, boolean enabled) {
        CompoundTag tag = getOrCreateTag(stack);
        tag.putBoolean(NBT_WARNING, enabled);
        saveTag(stack, tag);
    }

    private static Component modeName(int mode, boolean warningEnabled) {
        return switch (mode) {
            case MODE_PING -> Component.literal("Mode: Echo Ping").withStyle(ChatFormatting.AQUA);
            case MODE_CLEANSE -> Component.literal("Mode: Cleanse").withStyle(ChatFormatting.LIGHT_PURPLE);
            case MODE_WARNING -> Component.literal("Mode: Warden Warning (" + (warningEnabled ? "ON" : "OFF") + ")")
                    .withStyle(ChatFormatting.DARK_AQUA);
            default -> Component.literal("Mode: Unknown").withStyle(ChatFormatting.GRAY);
        };
    }

    // ---- Right-click: shift = use mode, no shift = cycle mode ----

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) {
            // USE current mode
            if (!level.isClientSide) {
                int mode = getMode(stack);

                if (mode == MODE_PING) {
                    if (player.getCooldowns().isOnCooldown(this)) {
                        return InteractionResultHolder.pass(stack);
                    }
                    doPing((ServerLevel) level, player);
                    player.getCooldowns().addCooldown(this, PING_COOLDOWN_TICKS);
                }
                else if (mode == MODE_CLEANSE) {
                    if (player.getCooldowns().isOnCooldown(this)) {
                        return InteractionResultHolder.pass(stack);
                    }
                    doCleanse((ServerLevel) level, player);
                    player.getCooldowns().addCooldown(this, CLEANSE_COOLDOWN_TICKS);
                }
                else if (mode == MODE_WARNING) {
                    // Toggle warning on/off
                    boolean enabled = isWarningEnabled(stack);
                    setWarningEnabled(stack, !enabled);
                    player.displayClientMessage(modeName(MODE_WARNING, !enabled), true);
                    level.playSound(null, player.blockPosition(), SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.PLAYERS, 0.6F, enabled ? 0.9F : 1.1F);
                }
            }

            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        } else {
            // CYCLE mode
            if (!level.isClientSide) {
                int mode = getMode(stack);
                mode = (mode + 1) % 3;
                setMode(stack, mode);

                player.displayClientMessage(modeName(mode, isWarningEnabled(stack)), true);
                level.playSound(null, player.blockPosition(), SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.PLAYERS, 0.6F, 1.2F);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }
    }

    // ---- Mode 1: Echo Ping ----
    private void doPing(ServerLevel level, Player player) {
        AABB box = player.getBoundingBox().inflate(PING_RADIUS);

        // Glow sculky mobs (you can narrow this to your entities later)
        List<LivingEntity> nearby = level.getEntitiesOfClass(LivingEntity.class, box, e -> e != player);

        int glowed = 0;
        for (LivingEntity e : nearby) {
            // Optional: only glow sculk-themed mobs by tag or by namespace check.
            // For now, glow anything that is not a player for debugging usefulness.
            if (!(e instanceof Player)) {
                e.addEffect(new MobEffectInstance(MobEffects.GLOWING, PING_GLOW_DURATION_TICKS, 0, true, false, true));
                glowed++;
            }
        }

        // Small “ping” feedback: brief night vision so the player feels it even if nothing glows
        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 40, 0, true, false, false));

        level.playSound(null, player.blockPosition(), SoundEvents.SCULK_BLOCK_CHARGE, SoundSource.PLAYERS, 1.0F, 1.0F);
        player.displayClientMessage(Component.literal("Echo Ping: detected " + glowed + " entities.")
                .withStyle(ChatFormatting.AQUA), true);
    }

    // ---- Mode 2: Cleanse ----
    private void doCleanse(ServerLevel level, Player player) {
        // Clears Darkness instantly
        player.removeEffect(MobEffects.DARKNESS);
        player.removeEffect(ModEffects.SCULK_INFECTION_EFFECT);


        // Small consolation buff so it feels meaningful even if player had no Darkness
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 0, true, true, true)); // 3s regen

        level.playSound(null, player.blockPosition(), SoundEvents.WARDEN_HEARTBEAT, SoundSource.PLAYERS, 0.9F, 1.2F);
        player.displayClientMessage(Component.literal("Cleanse: Darkness removed.").withStyle(ChatFormatting.LIGHT_PURPLE), true);
    }

    // ---- Mode 3: Passive Warden Warning (offhand) ----
    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot, boolean selected) {
        if (level.isClientSide) return;
        if (!(entity instanceof Player player)) return;

        // Only when in OFFHAND
        ItemStack offhand = player.getOffhandItem();
        if (offhand != stack) return;

        if (!isWarningEnabled(stack)) return;

        // Don’t spam: check every 20 ticks
        if (player.tickCount % 20 != 0) return;

        // Detect nearby shrieker/sensor blocks (simple and vanilla-friendly)
        boolean found = isSculkWarningNearby((ServerLevel) level, player.blockPosition(), WARNING_RADIUS);

        if (found) {
            // Subtle click + message (message can be removed if you want purely sound)
            level.playSound(null, player.blockPosition(), SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.PLAYERS, 0.45F, 0.7F);
            player.displayClientMessage(Component.literal("⚠ Sculk activity nearby").withStyle(ChatFormatting.DARK_AQUA), true);
        }
    }

    private boolean isSculkWarningNearby(ServerLevel level, BlockPos origin, int radius) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        // Scan a small cube; radius 14 is fine (29^3 = 24k checks) once per second.
        // If you want it lighter, reduce to 10 or scan only a few rings.
        for (int y = -3; y <= 3; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    pos.set(origin.getX() + x, origin.getY() + y, origin.getZ() + z);
                    BlockState state = level.getBlockState(pos);

                    if (state.is(Blocks.SCULK_SHRIEKER) || state.is(Blocks.SCULK_SENSOR)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        int mode = getMode(stack);
        boolean warning = isWarningEnabled(stack);

        tooltip.add(modeName(mode, warning));
        tooltip.add(Component.literal("Right-click: cycle modes").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Shift + right-click: use mode / toggle warning").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Offhand: warns near sensors/shriekers").withStyle(ChatFormatting.DARK_GRAY));
    }
}
