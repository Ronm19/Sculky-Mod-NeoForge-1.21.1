package net.ronm19.sculky.entity.variant;

import java.util.Arrays;
import java.util.Comparator;

public enum SculkmiteVariant {
    DEFAULT(0),
    KING(1);

    private static final SculkmiteVariant[] BY_ID = Arrays.stream(values()).sorted(
            Comparator.comparingInt(SculkmiteVariant::getId)).toArray(SculkmiteVariant[]::new);
    private final int id;

    SculkmiteVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static SculkmiteVariant byId(int id) {
        return BY_ID[id % BY_ID.length];
    }
}

