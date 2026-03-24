package net.ronm19.sculky.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.ronm19.sculky.entity.custom.ShadowPantherEntity;

import java.util.EnumSet;

public class ShadowPantherCircleGoal extends Goal {

    private final ShadowPantherEntity panther;
    private LivingEntity target;

    private double angle;
    private int circleTime;

    private double radius;
    private double speed;

    public ShadowPantherCircleGoal(ShadowPantherEntity panther) {
        this.panther = panther;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        this.target = panther.getTarget();

        if (this.target == null || !this.target.isAlive()) return false;
        if (panther.isOrderedToSit()) return false;
        if (panther.isStalking()) return false;
        if (panther.isAttacking()) return false;

        double dist = panther.distanceTo(this.target);
        return dist > 3.0D && dist < 12.0D;
    }

    @Override
    public void start() {
        this.angle = panther.getRandom().nextDouble() * Math.PI * 2.0D;
        this.circleTime = 0;

        this.radius = 3.5D + panther.getRandom().nextDouble() * 2.5D;
        this.speed = 0.85D + panther.getRandom().nextDouble() * 0.25D;

        panther.setCircling(true);
        panther.setStalking(false);
    }

    @Override
    public void tick() {
        if (this.target == null || !this.target.isAlive()) return;

        this.circleTime++;

        this.angle += 0.04D + panther.getRandom().nextDouble() * 0.025D;

        double x = this.target.getX() + this.radius * Math.cos(this.angle);
        double z = this.target.getZ() + this.radius * Math.sin(this.angle);
        double y = this.target.getY();

        panther.getNavigation().moveTo(x, y, z, this.speed);
        panther.getLookControl().setLookAt(this.target, 40.0F, 40.0F);

        if (this.circleTime % 40 == 0 && this.radius > 3.0D) {
            this.radius -= 0.35D;
        }

        // Let the entity handle sound cooldowns.
        if (this.target instanceof Player) {
            panther.tryPlayPressureSound();
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (this.target == null || !this.target.isAlive()) return false;
        if (panther.isOrderedToSit()) return false;
        if (panther.isStalking()) return false;
        if (panther.isAttacking()) return false;

        double dist = panther.distanceTo(this.target);
        return dist < 14.0D && this.circleTime < 120;
    }

    @Override
    public void stop() {
        panther.setCircling(false);
        this.target = null;
    }
}