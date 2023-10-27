package dev.anhcraft.schematicfixer.internal;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public record Position(int x, int y, int z) {
    public static Position of(int[] a) {
        return new Position(a[0], a[1], a[2]);
    }

    @Override
    public String toString() {
        return String.format("%d %d %d", x, y, z);
    }
}
