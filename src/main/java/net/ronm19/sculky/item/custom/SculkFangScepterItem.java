package net.ronm19.sculky.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.ronm19.sculky.entity.projectile.SculkFangsEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SculkFangScepterItem extends Item {
    private static final int COOLDOWN_TICKS = 20 * 6;
    private static final int FANG_COUNT = 9;
    private static final double FANG_SPACING = 1.15D;
    private static final double FANG_START_DISTANCE = 1.25D;
    private static final double AIM_ASSIST_RANGE = 13.0D;
    private static final double AIM_ASSIST_DOT = 0.65D;

    public SculkFangScepterItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResultHolder.success(stack);
        }

        Vec3 look = player.getLookAngle();
        Vec3 direction = new Vec3(look.x, 0.0D, look.z);

        if (direction.lengthSqr() < 0.0001D) {
            return InteractionResultHolder.pass(stack);
        }

        direction = getAssistedDirection(serverLevel, player, direction);

        direction = direction.normalize();

        float fangRotation = (float) Mth.atan2(direction.z, direction.x);

        playCastPolish(serverLevel, player);

        for (int i = 0; i < FANG_COUNT; i++) {
            double distance = FANG_START_DISTANCE + i * FANG_SPACING;

            double x = player.getX() + direction.x * distance;
            double z = player.getZ() + direction.z * distance;
            double y = findFangY(serverLevel, x, z, player.getY());

            SculkFangsEntity fangs = new SculkFangsEntity(
                    serverLevel,
                    x,
                    y,
                    z,
                    fangRotation,
                    i * 2,
                    player
            );

            serverLevel.addFreshEntity(fangs);

            if (i % 2 == 0) {
                serverLevel.sendParticles(
                        ParticleTypes.SCULK_SOUL,
                        x,
                        y + 0.15D,
                        z,
                        6,
                        0.2D,
                        0.08D,
                        0.2D,
                        0.01D
                );
            }
        }

        knockbackEntitiesInPath(serverLevel, player, direction);

        serverLevel.playSound(
                null,
                player.blockPosition(),
                SoundEvents.EVOKER_CAST_SPELL,
                SoundSource.PLAYERS,
                1.0F,
                0.75F + player.getRandom().nextFloat() * 0.2F
        );

        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    private static double findFangY(ServerLevel level, double x, double z, double startY) {
        for (int i = 0; i < 8; i++) {
            BlockPos pos = BlockPos.containing(x, startY - i, z);
            BlockPos below = pos.below();

            if (level.getBlockState(below).isFaceSturdy(level, below, Direction.UP)) {
                return pos.getY();
            }
        }

        for (int i = 1; i <= 4; i++) {
            BlockPos pos = BlockPos.containing(x, startY + i, z);
            BlockPos below = pos.below();

            if (level.getBlockState(below).isFaceSturdy(level, below, Direction.UP)) {
                return pos.getY();
            }
        }

        return startY;
    }

    private static void knockbackEntitiesInPath(ServerLevel level, Player player, Vec3 direction) {
        AABB area = player.getBoundingBox()
                .expandTowards(direction.scale(FANG_START_DISTANCE + FANG_COUNT * FANG_SPACING + 1.0D))
                .inflate(1.25D, 0.75D, 1.25D);

        List<LivingEntity> targets = level.getEntitiesOfClass(
                LivingEntity.class,
                area,
                entity -> entity != player && entity.isAlive() && !entity.isAlliedTo(player)
        );

        for (LivingEntity target : targets) {
            Vec3 toTarget = target.position().subtract(player.position());
            Vec3 flatToTarget = new Vec3(toTarget.x, 0.0D, toTarget.z);

            if (flatToTarget.lengthSqr() < 0.0001D) {
                continue;
            }

            flatToTarget = flatToTarget.normalize();

            double dot = flatToTarget.dot(direction);

            // Only push entities mostly in front of the player.
            if (dot < 0.45D) {
                continue;
            }

            target.push(
                    direction.x * 0.65D,
                    0.28D,
                    direction.z * 0.65D
            );

            target.hurtMarked = true;
        }
    }

    private static Vec3 getAssistedDirection(ServerLevel level, Player player, Vec3 originalDirection) {
        AABB searchArea = player.getBoundingBox()
                .expandTowards(originalDirection.scale(AIM_ASSIST_RANGE))
                .inflate(4.0D, 2.5D, 4.0D);

        List<LivingEntity> targets = level.getEntitiesOfClass(
                LivingEntity.class,
                searchArea,
                entity -> entity != player
                        && entity.isAlive()
                        && !entity.isAlliedTo(player)
                        && player.hasLineOfSight(entity)
        );

        LivingEntity bestTarget = null;
        double bestScore = 0.0D;

        for (LivingEntity target : targets) {
            Vec3 toTarget = target.position().subtract(player.position());
            Vec3 flatToTarget = new Vec3(toTarget.x, 0.0D, toTarget.z);

            if (flatToTarget.lengthSqr() < 0.0001D) {
                continue;
            }

            flatToTarget = flatToTarget.normalize();

            double dot = flatToTarget.dot(originalDirection);

            // Lower dot = wider aim assist. 0.65 is forgiving but not insane.
            if (dot < AIM_ASSIST_DOT) {
                continue;
            }

            double distance = player.distanceTo(target);
            double score = dot / Math.max(distance, 1.0D);

            if (score > bestScore) {
                bestScore = score;
                bestTarget = target;
            }
        }

        if (bestTarget == null) {
            return originalDirection;
        }

        Vec3 assisted = bestTarget.position().subtract(player.position());
        assisted = new Vec3(assisted.x, 0.0D, assisted.z);

        if (assisted.lengthSqr() < 0.0001D) {
            return originalDirection;
        }

        return assisted.normalize();
    }

    private static void playCastPolish(ServerLevel level, Player player) {
        level.sendParticles(
                ParticleTypes.SCULK_SOUL,
                player.getX(),
                player.getY() + 1.1D,
                player.getZ(),
                24,
                0.35D,
                0.45D,
                0.35D,
                0.025D
        );

        level.sendParticles(
                ParticleTypes.SOUL_FIRE_FLAME,
                player.getX(),
                player.getY() + 0.8D,
                player.getZ(),
                10,
                0.25D,
                0.25D,
                0.25D,
                0.015D
        );

        level.playSound(
                null,
                player.blockPosition(),
                SoundEvents.WARDEN_SONIC_CHARGE,
                SoundSource.PLAYERS,
                0.45F,
                1.55F
        );
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.sculky.sculk_fang_scepter.line1")
                .withStyle(ChatFormatting.DARK_AQUA));
        tooltip.add(Component.translatable("tooltip.sculky.sculk_fang_scepter.line2")
                .withStyle(ChatFormatting.GRAY));

        super.appendHoverText(stack, context, tooltip, flag);
    }
}