package net.ronm19.sculky.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.AABB;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class HunterSenseTargetGoal extends Goal {
    private final Mob hunter;
    private final double range;
    private LivingEntity foundTarget;
    private int scanCooldown = 0;

    public HunterSenseTargetGoal(Mob hunter, double range) {
        this.hunter = hunter;
        this.range = range;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if (scanCooldown > 0) {
            scanCooldown--;
            return false;
        }

        scanCooldown = 10;
        foundTarget = findNearestMonster();
        return foundTarget != null;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = hunter.getTarget();
        return target != null
                && target.isAlive()
                && hunter.distanceToSqr(target) <= (range * range * 2.25D);
    }

    @Override
    public void start() {
        if (foundTarget != null) {
            hunter.setTarget(foundTarget);
        }
    }

    @Override
    public void stop() {
        foundTarget = null;
    }

    @Override
    public void tick() {
        LivingEntity target = hunter.getTarget();

        if (target == null || !target.isAlive()) {
            foundTarget = findNearestMonster();
            if (foundTarget != null) {
                hunter.setTarget(foundTarget);
            }
        }
    }

    private LivingEntity findNearestMonster() {
        AABB searchBox = hunter.getBoundingBox().inflate(range);

        List<LivingEntity> enemies = hunter.level().getEntitiesOfClass(
                LivingEntity.class,
                searchBox,
                entity -> entity != null
                        && entity.isAlive()
                        && entity != hunter
                        && entity instanceof Enemy
                        && !entity.isAlliedTo(hunter)
        );

        return enemies.stream()
                .min(Comparator.comparingDouble(hunter::distanceToSqr))
                .orElse(null);
    }
}