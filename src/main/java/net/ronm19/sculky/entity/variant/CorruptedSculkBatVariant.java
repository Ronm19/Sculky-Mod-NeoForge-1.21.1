package net.ronm19.sculky.entity.variant;

import java.util.Arrays;
import java.util.Comparator;

public enum CorruptedSculkBatVariant {
    NORMAL(0),
   CORRUPTED(1);

    private static final CorruptedSculkBatVariant[] BY_ID = Arrays.stream(values()).sorted(
            Comparator.comparingInt(CorruptedSculkBatVariant ::getId)).toArray(CorruptedSculkBatVariant[]::new);
    private final int id;

    CorruptedSculkBatVariant( int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static CorruptedSculkBatVariant byId( int id) {
        return BY_ID[id % BY_ID.length];
    }
}

