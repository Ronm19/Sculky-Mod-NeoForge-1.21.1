package net.ronm19.sculky.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Wolf;
import net.ronm19.sculky.entity.custom.SculkWolfAlphaEntity;
import net.ronm19.sculky.entity.custom.SculkWolfEntity;

import java.util.EnumSet;

public class FollowAlphaGoal extends Goal {

    private final SculkWolfEntity wolf;
    private SculkWolfAlphaEntity alpha;
    private final double speed;
    private final double followDist = 12.0;
    private final double stopDist = 3.0;

    public FollowAlphaGoal(SculkWolfEntity wolf, double speed) {
        this.wolf = wolf;
        this.speed = speed;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (wolf.isTame()) return false;

        alpha = wolf.level().getNearestEntity(
                SculkWolfAlphaEntity.class,
                TargetingConditions.forNonCombat(),
                wolf,
                wolf.getX(), wolf.getY(), wolf.getZ(),
                wolf.getBoundingBox().inflate(followDist)
        );

        return alpha != null && alpha.isAlive();
    }

    @Override
    public boolean canContinueToUse() {
        if (wolf.isTame()) return false;
        return alpha != null && alpha.isAlive() &&
                wolf.distanceTo(alpha) > stopDist &&
                wolf.distanceTo(alpha) < followDist + 4;
    }

    @Override
    public void start() {
        wolf.getNavigation().moveTo(alpha, speed);
    }

    @Override
    public void stop() {
        wolf.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (alpha != null) {
            double dist = wolf.distanceTo(alpha);

            // Follow Alpha
            if (dist > stopDist) {
                wolf.getNavigation().moveTo(alpha, speed);
            }

            // Defend Alpha if he's attacked
            LivingEntity attacker = alpha.getLastHurtByMob();
            if (attacker != null) {
                wolf.setTarget(attacker);
            }

            wolf.getLookControl().setLookAt(alpha, 30.0F, 30.0F);
        }
    }
}