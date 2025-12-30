package net.ronm19.sculky.entity.variant;

import java.util.Arrays;
import java.util.Comparator;

public enum CorruptedSculkSkeletonVariant {
    NORMAL(0),
   CORRUPTED(1);

    private static final CorruptedSculkSkeletonVariant[] BY_ID = Arrays.stream(values()).sorted(
            Comparator.comparingInt(CorruptedSculkSkeletonVariant ::getId)).toArray(CorruptedSculkSkeletonVariant[]::new);
    private final int id;

    CorruptedSculkSkeletonVariant( int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static CorruptedSculkSkeletonVariant byId( int id) {
        return BY_ID[id % BY_ID.length];
    }
}

