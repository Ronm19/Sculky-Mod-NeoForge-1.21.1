package net.ronm19.sculky.entity.variant;

import java.util.Arrays;
import java.util.Comparator;

public enum CorruptedSculkEndermanVariant {
    NORMAL(0),
   CORRUPTED(1);

    private static final CorruptedSculkEndermanVariant[] BY_ID = Arrays.stream(values()).sorted(
            Comparator.comparingInt(CorruptedSculkEndermanVariant::getId)).toArray(CorruptedSculkEndermanVariant[]::new);
    private final int id;

    CorruptedSculkEndermanVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static CorruptedSculkEndermanVariant byId(int id) {
        return BY_ID[id % BY_ID.length];
    }
}

