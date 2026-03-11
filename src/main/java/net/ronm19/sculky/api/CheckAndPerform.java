package net.ronm19.sculky.api;

import net.minecraft.world.entity.LivingEntity;

public interface CheckAndPerform {
    void checkAndPerformAttack(LivingEntity pEnemy, double pDistToEnemySqr);
}
