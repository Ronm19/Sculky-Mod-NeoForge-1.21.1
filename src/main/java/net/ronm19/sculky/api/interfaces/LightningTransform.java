package net.ronm19.sculky.api.interfaces;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;

public interface LightningTransform {

    /** Should this entity transform right now? */
    boolean canTransform(Entity entity, ServerLevel level);

    /** Perform the transformation */
    void transform(Entity entity, ServerLevel level, LightningBolt lightning);
}
