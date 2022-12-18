package juuxel.advent2022;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

public final class Day18 {
    public static void main(String[] args) {
        part1(args);
        part2(args);
    }

    private static void part1(String[] args) {
        Set<Pos> lavaTiles = readInput(args);

        Pos mutablePos = new Pos(0, 0, 0);
        int part1 = 0;
        for (Pos pos : lavaTiles) {
            mutablePos.copyFrom(pos);
            for (Direction offset : Direction.values()) {
                mutablePos.offset(offset, 1);
                if (!lavaTiles.contains(mutablePos)) {
                    part1++;
                }
                mutablePos.offset(offset, -1);
            }
        }
        System.out.println(part1);
    }

    private static void part2(String[] args) {
        // Setup
        Set<Pos> lavaTiles = readInput(args);
        Set<Pair<Pos, Direction>> shell = new HashSet<>();

        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;
        int minZ = Integer.MAX_VALUE, maxZ = Integer.MIN_VALUE;

        for (Pos lavaTile : lavaTiles) {
            minX = Math.min(lavaTile.x, minX);
            minY = Math.min(lavaTile.y, minY);
            minZ = Math.min(lavaTile.z, minZ);
            maxX = Math.max(lavaTile.x, maxX);
            maxY = Math.max(lavaTile.y, maxY);
            maxZ = Math.max(lavaTile.z, maxZ);
        }

        findShell(new Pos(minX - 1, minY - 1, minZ - 1), lavaTiles, shell, new Pos(minX - 1, minY - 1, minZ - 1), new Pos(maxX + 1, maxY + 1, maxZ + 1));
        System.out.println(shell.size());
    }

    private static Set<Pos> readInput(String[] args) {
        Set<Pos> result = new HashSet<>();
        for (String line : args) {
            String[] parts = line.split(",", 3);
            Pos pos = new Pos(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
            result.add(pos);
        }
        return result;
    }

    private static void findShell(Pos start, Set<Pos> lavaTiles, Set<Pair<Pos, Direction>> shell, Pos min, Pos max) {
        Set<Pair<Pos, Direction>> checked = new HashSet<>();
        for (Direction direction : Direction.values()) {
            checked.add(new Pair<>(start, direction));
        }
        Queue<Pair<Pos, Direction>> toCheck = new ArrayDeque<>();
        toCheck.add(new Pair<>(start, Direction.POSITIVE_X));

        while (!toCheck.isEmpty()) {
            Pair<Pos, Direction> current = toCheck.remove();
            Pos pos = current.first;

            if (lavaTiles.contains(pos)) {
                shell.add(current);
                continue;
            }

            Pos mutablePos = new Pos(0, 0, 0);
            for (Direction offset : Direction.values()) {
                mutablePos.copyFrom(pos);
                mutablePos.offset(offset, 1);

                if (min.x <= mutablePos.x && min.y <= mutablePos.y && min.z <= mutablePos.z &&
                    mutablePos.x <= max.x && mutablePos.y <= max.y && mutablePos.z <= max.z &&
                    !checked.contains(new Pair<>(mutablePos, offset))) {
                    Pair<Pos, Direction> next = new Pair<>(new Pos(0, 0, 0), offset);
                    next.first.copyFrom(mutablePos);
                    checked.add(next);
                    toCheck.add(next);
                }
            }
        }
    }

    private static final class Pos {
        int x, y, z;

        Pos(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        void copyFrom(Pos other) {
            x = other.x;
            y = other.y;
            z = other.z;
        }

        void offset(Direction offset, int scale) {
            x += offset.x * scale;
            y += offset.y * scale;
            z += offset.z * scale;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, z);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Pos pos && x == pos.x && y == pos.y && z == pos.z;
        }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ", " + z + ")";
        }
    }

    private record Pair<T, U>(T first, U second) {}

    private enum Direction {
        POSITIVE_X(1, 0, 0),
        NEGATIVE_X(-1, 0, 0),
        POSITIVE_Y(0, 1, 0),
        NEGATIVE_Y(0, -1, 0),
        POSITIVE_Z(0, 0, 1),
        NEGATIVE_Z(0, 0, -1);

        final int x;
        final int y;
        final int z;

        Direction(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}
