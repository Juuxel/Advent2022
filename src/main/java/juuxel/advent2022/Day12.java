package juuxel.advent2022;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import java.util.*;

public final class Day12 {
    public static void main(String[] args) {
        HeightmapData data = readHeightmap(args);
        int part1 = aStar(data, data.start);
        System.out.println(part1);

        TreeSet<Integer> pathLengths = new TreeSet<>();
        for (int x = 0; x < data.width; x++) {
            for (int y = 0; y < data.height; y++) {
                int height = data.heightmap[x][y];

                if (height == 0) {
                    var pathLength = aStar(data, new Point(x, y));
                    if (pathLength != null) pathLengths.add(pathLength);
                }
            }
        }
        System.out.println(pathLengths.first());
    }

    private static Integer aStar(HeightmapData data, Point start) {
        Multimap<Integer, Point> openSet = MultimapBuilder.treeKeys().hashSetValues().build();
        openSet.put(0, start);
        Map<Point, Integer> pathLengths = new HashMap<>();
        pathLengths.put(start, 0);

        while (!openSet.isEmpty()) {
            var iter = openSet.values().iterator();
            Point current = iter.next();
            iter.remove();

            if (current.equals(data.end)) {
                return pathLengths.get(current);
            }

            List<Point> neighbors = new ArrayList<>(4);
            if (current.x > 0) neighbors.add(new Point(current.x - 1, current.y));
            if (current.x < data.width - 1) neighbors.add(new Point(current.x + 1, current.y));
            if (current.y > 0) neighbors.add(new Point(current.x, current.y - 1));
            if (current.y < data.height - 1) neighbors.add(new Point(current.x, current.y + 1));

            for (Point neighbor : neighbors) {
                int heightDifference = data.heightmap[neighbor.x][neighbor.y] - data.heightmap[current.x][current.y];
                if (heightDifference > 1) continue;

                int currentLength = pathLengths.get(current) + 1;
                if (currentLength < pathLengths.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    pathLengths.put(neighbor, currentLength);
                    if (!openSet.containsValue(neighbor)) {
                        // f-score: current length + estimated minimum distance to neighbor
                        openSet.put(currentLength + distance(neighbor, data.end), neighbor);
                    }
                }
            }
        }

        return null;
    }

    private static int distance(Point start, Point end) {
        return Math.abs(end.x - start.x) + Math.abs(end.y - start.y);
    }

    private static HeightmapData readHeightmap(String[] args) {
        int width = args[0].length();
        int height = args.length;
        int[][] heightmap = new int[width][height];
        Point start = null, end = null;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                char c = args[y].charAt(x);
                int z = switch (c) {
                    case 'S' -> {
                        start = new Point(x, y);
                        yield 0;
                    }
                    case 'E' -> {
                        end = new Point(x, y);
                        yield 25;
                    }
                    default -> c - 'a';
                };
                heightmap[x][y] = z;
            }
        }
        return new HeightmapData(width, height, heightmap, start, end);
    }

    private record Point(int x, int y) {}
    private record HeightmapData(int width, int height, int[][] heightmap, Point start, Point end) {}
}
