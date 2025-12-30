package net.ronm19.sculky.entity.variant;

import java.util.Arrays;
import java.util.Comparator;

public enum CorruptedSculkStalkerVariant {
    NORMAL(0),
   CORRUPTED(1);

    private static final CorruptedSculkStalkerVariant[] BY_ID = Arrays.stream(values()).sorted(
            Comparator.comparingInt(CorruptedSculkStalkerVariant ::getId)).toArray(CorruptedSculkStalkerVariant[]::new);
    private final int id;

    CorruptedSculkStalkerVariant( int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static CorruptedSculkStalkerVariant byId( int id) {
        return BY_ID[id % BY_ID.length];
    }
}

