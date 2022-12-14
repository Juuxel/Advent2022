package juuxel.advent2022;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class Day14 {
    public static void main(String[] args) {
        Map<Point, Tile> tiles = new HashMap<>();
        for (String line : args) {
            readLine(line, tiles);
        }

        int maxY = tiles.keySet().stream().mapToInt(Point::y).max().orElseThrow();
        System.out.println(simulate(new HashMap<>(tiles), point -> Tile.AIR, maxY, false));
        System.out.println(simulate(new HashMap<>(tiles), point -> point.y == maxY + 2 ? Tile.ROCK : Tile.AIR, maxY, true));
    }

    private static int simulate(Map<Point, Tile> tiles, Function<Point, Tile> defaultValue, int maxY, boolean part2) {
        int sands = 0;
        top: while (true) {
            int sandX = 500, sandY = 0;
            while (part2 ? tiles.computeIfAbsent(new Point(sandX, sandY), defaultValue) == Tile.AIR : sandY <= maxY) {
                Point below = new Point(sandX, sandY + 1);
                Point left = new Point(sandX - 1, sandY + 1);
                Point right = new Point(sandX + 1, sandY + 1);

                if (tiles.computeIfAbsent(below, defaultValue) == Tile.AIR) {
                    sandY++;
                } else if (tiles.computeIfAbsent(left, defaultValue) == Tile.AIR) {
                    sandX--;
                    sandY++;
                } else if (tiles.computeIfAbsent(right, defaultValue) == Tile.AIR) {
                    sandX++;
                    sandY++;
                } else {
                    tiles.put(new Point(sandX, sandY), Tile.SAND);
                    sands++;
                    continue top;
                }
            }

            break;
        }

        return sands;
    }

    private static void readLine(String line, Map<Point, Tile> tiles) {
        var parts = line.split(" -> ");
        for (int i = 0; i < parts.length - 1; i++) {
            var point1 = parts[i].split(",");
            var point2 = parts[i + 1].split(",");
            int x1 = Integer.parseInt(point1[0]), y1 = Integer.parseInt(point1[1]);
            int x2 = Integer.parseInt(point2[0]), y2 = Integer.parseInt(point2[1]);
            if (x1 != x2) {
                for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
                    tiles.put(new Point(x, y1), Tile.ROCK);
                }
            } else {
                for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
                    tiles.put(new Point(x1, y), Tile.ROCK);
                }
            }
        }
    }

    private record Point(int x, int y) {}
    private enum Tile {
        AIR, ROCK, SAND;
    }
}
