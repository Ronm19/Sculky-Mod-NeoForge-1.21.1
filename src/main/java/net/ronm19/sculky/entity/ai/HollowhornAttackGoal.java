package net.ronm19.sculky.entity.ai;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.ronm19.sculky.api.interfaces.CheckAndPerform;
import net.ronm19.sculky.entity.custom.HollowhornEntity;

public class HollowhornAttackGoal extends MeleeAttackGoal implements CheckAndPerform {
    private final HollowhornEntity entity;
    private final int attackDelay = 12;
    private int ticksUntilNextAttack = 24;
    private boolean shouldCountTillNextAttack = false;

    public HollowhornAttackGoal( PathfinderMob pMob, double speedModifier, boolean followingTargetEvenIfNotSeen ) {
        super(pMob, speedModifier, followingTargetEvenIfNotSeen);
        entity = ((HollowhornEntity) pMob);
    }

    @Override
    public void checkAndPerformAttack(LivingEntity enemy, double distSqr) {

        if (isEnemyWithinAttackDistance(enemy, distSqr)) {

            if (this.ticksUntilNextAttack <= 0) {

                // START ANIMATION
                entity.setAttacking(true);

                // Deal damage immediately (or delay if you want wind-up)
                performAttack(enemy);

                // Cooldown
                this.ticksUntilNextAttack = attackDelay;
            }

        } else {
            entity.setAttacking(false);
            this.ticksUntilNextAttack = attackDelay;
        }
    }

    private boolean isEnemyWithinAttackDistance(LivingEntity pEnemy, double pDistToEnemySqr) {
        return pDistToEnemySqr <= this.getAttackReachSqr(pEnemy);
    }

    private double getAttackReachSqr(LivingEntity pEnemy) {
        return this.mob.getBbWidth() * this.mob.getBbWidth() + pEnemy.getBbWidth();
    }

    protected void resetAttackCooldown() {
        this.ticksUntilNextAttack = this.adjustedTickDelay(attackDelay * 2);
    }

    protected boolean isTimeToAttack() {
        return this.ticksUntilNextAttack <= 0;
    }

    protected boolean isTimeToStartAttackAnimation() {
        return this.ticksUntilNextAttack <= attackDelay;
    }

    protected int getTicksUntilNextAttack() {
        return this.ticksUntilNextAttack;
    }


    protected void performAttack( LivingEntity pEnemy) {
        this.resetAttackCooldown();
        this.mob.swing(InteractionHand.MAIN_HAND);
        this.mob.doHurtTarget(pEnemy);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.ticksUntilNextAttack > 0) {
            this.ticksUntilNextAttack--;
        }

        // Stop attack animation shortly after hit
        if (this.ticksUntilNextAttack < attackDelay - 5) {
            entity.setAttacking(false);
        }
    }

    @Override
    public void stop() {
        entity.setAttacking(false);
        super.stop();
    }
}