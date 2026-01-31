package net.ronm19.sculky.entity.command;

import net.minecraft.util.Mth;

public enum RatCommandMode {
    FOLLOW(0),
    STAY(1),
    WANDER(2),
    KILL_ON_SIGHT(3);

    public final int id;

    RatCommandMode(int id) {
        this.id = id;
    }

    public static RatCommandMode byId(int id) {
        int wrapped = Math.floorMod(id, values().length);
        return values()[wrapped];
    }

    public RatCommandMode next() {
        return byId(this.id + 1);
    }
}

