package dev.anhcraft.schematicfixer.internal;

public record Position(int x, int y, int z) {
    public static Position of(int x, int y, int z) {
        return new Position(x, y, z);
    }

    public static Position of(int[] a) {
        return new Position(a[0], a[1], a[2]);
    }

    @Override
    public String toString() {
        return String.format("%d %d %d", x, y, z);
    }

    public Position add(Position offset) {
        return of(x + offset.x, y + offset.y, z + offset.z);
    }
}
