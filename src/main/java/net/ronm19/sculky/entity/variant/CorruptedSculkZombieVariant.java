package net.ronm19.sculky.entity.variant;

import java.util.Arrays;
import java.util.Comparator;

public enum CorruptedSculkZombieVariant {
    NORMAL(0),
   CORRUPTED(1);

    private static final CorruptedSculkZombieVariant[] BY_ID = Arrays.stream(values()).sorted(
            Comparator.comparingInt(CorruptedSculkZombieVariant::getId)).toArray(CorruptedSculkZombieVariant[]::new);
    private final int id;

    CorruptedSculkZombieVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static CorruptedSculkZombieVariant byId(int id) {
        return BY_ID[id % BY_ID.length];
    }
}

