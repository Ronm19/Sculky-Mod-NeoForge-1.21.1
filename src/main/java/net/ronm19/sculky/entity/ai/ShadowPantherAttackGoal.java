package net.ronm19.sculky.entity.ai;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.ronm19.sculky.api.interfaces.CheckAndPerform;
import net.ronm19.sculky.entity.custom.ShadowPantherEntity;

public class ShadowPantherAttackGoal extends MeleeAttackGoal implements CheckAndPerform {

    private final ShadowPantherEntity entity;

    private static final int ATTACK_DELAY = 16;
    private static final int WINDUP_DURATION = 6;

    private int ticksUntilNextAttack = 0;
    private boolean windingUp = false;
    private int windupTicks = 0;

    public ShadowPantherAttackGoal(PathfinderMob mob, double speed, boolean longMemory) {
        super(mob, speed, longMemory);
        this.entity = (ShadowPantherEntity) mob;
    }

    @Override
    public void checkAndPerformAttack(LivingEntity enemy, double distSqr) {
        if (enemy == null || !enemy.isAlive()) {
            resetAttackState();
            return;
        }

        if (!isEnemyWithinAttackDistance(enemy, distSqr)) {
            resetAttackState();
            return;
        }

        if (this.ticksUntilNextAttack > 0) {
            return;
        }

        if (!this.windingUp) {
            this.windingUp = true;
            this.windupTicks = WINDUP_DURATION;

            entity.startAttackAnimation();
            return;
        }

        entity.getLookControl().setLookAt(enemy, 30.0F, 30.0F);

        if (this.windupTicks > 0) {
            this.windupTicks--;
            return;
        }

        performAttack(enemy);
        this.ticksUntilNextAttack = ATTACK_DELAY;
        this.windingUp = false;
        this.windupTicks = 0;
    }

    private boolean isEnemyWithinAttackDistance(LivingEntity enemy, double distSqr) {
        return distSqr <= this.getAttackReachSqr(enemy);
    }

    private double getAttackReachSqr(LivingEntity enemy) {
        return this.mob.getBbWidth() * this.mob.getBbWidth() + enemy.getBbWidth();
    }

    protected void performAttack(LivingEntity enemy) {
        this.mob.swing(InteractionHand.MAIN_HAND);
        this.mob.doHurtTarget(enemy);
    }

    private void resetAttackState() {
        this.windingUp = false;
        this.windupTicks = 0;
        this.entity.setAttacking(false);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.ticksUntilNextAttack > 0) {
            this.ticksUntilNextAttack--;
        }

        LivingEntity target = this.mob.getTarget();
        if (this.windingUp && target != null && target.isAlive()) {
            this.entity.getLookControl().setLookAt(target, 30.0F, 30.0F);
        }

        if (!this.windingUp && this.ticksUntilNextAttack < ATTACK_DELAY - 5) {
            this.entity.setAttacking(false);
        }
    }

    @Override
    public void stop() {
        resetAttackState();
        super.stop();
    }
}