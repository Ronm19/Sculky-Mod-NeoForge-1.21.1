package net.ronm19.sculky.entity.variant;

import java.util.Arrays;
import java.util.Comparator;

public enum CorruptedSculkCreeperVariant {
    NORMAL(0),
   CORRUPTED(1);

    private static final CorruptedSculkCreeperVariant[] BY_ID = Arrays.stream(values()).sorted(
            Comparator.comparingInt(CorruptedSculkCreeperVariant ::getId)).toArray(CorruptedSculkCreeperVariant[]::new);
    private final int id;

    CorruptedSculkCreeperVariant( int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static CorruptedSculkCreeperVariant byId( int id) {
        return BY_ID[id % BY_ID.length];
    }
}

