package net.ronm19.sculky.entity.ai;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class RelentlessMeleeHuntGoal extends Goal {
    private final PathfinderMob hunter;
    private final double speedModifier;

    private int repathCooldown = 0;
    private int attackCooldown = 0;
    private int unseenTicks = 0;

    private Vec3 lastKnownTargetPos = null;

    public RelentlessMeleeHuntGoal(PathfinderMob hunter, double speedModifier) {
        this.hunter = hunter;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = hunter.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = hunter.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public void start() {
        repathCooldown = 0;
        attackCooldown = 0;
        unseenTicks = 0;
    }

    @Override
    public void stop() {
        hunter.getNavigation().stop();
        lastKnownTargetPos = null;
        unseenTicks = 0;
    }

    @Override
    public void tick() {
        LivingEntity target = hunter.getTarget();
        if (target == null) return;

        hunter.getLookControl().setLookAt(target, 30.0F, 30.0F);

        if (attackCooldown > 0) attackCooldown--;

        boolean canSee = hunter.getSensing().hasLineOfSight(target);

        if (canSee) {
            unseenTicks = 0;
            lastKnownTargetPos = target.position();
        } else {
            unseenTicks++;
            if (lastKnownTargetPos == null) {
                lastKnownTargetPos = target.position();
            }
        }

        if (repathCooldown > 0) repathCooldown--;

        if (repathCooldown <= 0) {
            repathCooldown = 5;

            if (canSee) {
                hunter.getNavigation().moveTo(target, speedModifier);
            } else if (lastKnownTargetPos != null) {
                hunter.getNavigation().moveTo(
                        lastKnownTargetPos.x,
                        lastKnownTargetPos.y,
                        lastKnownTargetPos.z,
                        speedModifier
                );
            }
        }

        double distanceSqr = hunter.distanceToSqr(target);
        if (distanceSqr <= getAttackReachSqr(target) && attackCooldown <= 0) {
            attackCooldown = 15;
            hunter.swing(InteractionHand.MAIN_HAND);
            hunter.doHurtTarget(target);
        }

        // optional: if target has been unseen for too long, forget it
        if (unseenTicks > 200) {
            hunter.setTarget(null);
        }
    }

    private double getAttackReachSqr(LivingEntity target) {
        double hunterWidth = hunter.getBbWidth();
        double targetWidth = target.getBbWidth();
        return (hunterWidth * 2.0F) * (hunterWidth * 2.0F) + targetWidth;
    }
}