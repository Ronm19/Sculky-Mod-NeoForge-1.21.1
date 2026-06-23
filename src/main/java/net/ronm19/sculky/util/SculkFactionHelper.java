package net.ronm19.sculky.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.ronm19.sculky.entity.custom.SculkKingEntity;

public class SculkFactionHelper {
    public static boolean isWildSculkAlly(Entity entity) {
        if (!entity.getType().is(ModTags.Entities.SCULK_ALLIES)) {
            return false;
        }

        // Player-owned/tamed sculk mobs are allowed to fight the King.
        if (entity instanceof TamableAnimal tamable && tamable.isTame()) {
            return false;
        }

        return true;
    }

    public static boolean shouldIgnoreKing(Entity attacker, Entity target) {
        return target instanceof SculkKingEntity && isWildSculkAlly(attacker);
    }
}