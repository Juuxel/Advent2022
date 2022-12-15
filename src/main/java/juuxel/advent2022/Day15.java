package juuxel.advent2022;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

public final class Day15 {
    private static final Pattern PATTERN = Pattern.compile("^Sensor at x=(.+), y=(.+): closest beacon is at x=(.+), y=(.+)$");

    public static void main(String[] args) {
        part1(args);
        part2(args);
    }

    private static void part1(String[] args) {
        Map<Point, Tile> tiles = new HashMap<>();
        for (String line : args) {
            readLine(line, (sensor, beacon) -> {
                tiles.put(sensor, Tile.SENSOR);
                tiles.put(beacon, Tile.BEACON);
                int sensorToBeacon = manhattan(sensor.x, sensor.y, beacon.x, beacon.y);
                for (int y = -sensorToBeacon; y <= sensorToBeacon; y++) {
                    if (sensor.y + y != 2_000_000) continue;
                    for (int x = -sensorToBeacon; x <= sensorToBeacon; x++) {
                        if (Math.abs(x) + Math.abs(y) <= sensorToBeacon) {
                            tiles.putIfAbsent(new Point(sensor.x + x, sensor.y + y), Tile.OBSTRUCTED);
                        }
                    }
                }
            });
        }
        var score = tiles.entrySet().stream()
            .filter(entry -> entry.getValue() == Tile.OBSTRUCTED)
            .filter(entry -> entry.getKey().y == 2_000_000)
            .count();
        System.out.println(score);
    }

    // Note: very memory-intensive! Requires at least 2G, probably 3G.
    private static void part2(String[] args) {
        List<SensorData> sensors = new ArrayList<>();
        Set<Point> perimeter = new HashSet<>();
        for (String line : args) {
            readLine(line, sensors, perimeter);
        }
        Point result = findPart2Point(sensors, perimeter);
        long score = 4_000_000L * (long) result.x + (long) result.y;
        System.out.println(score);
    }

    private static Point findPart2Point(List<SensorData> sensors, Set<Point> perimeter) {
        pointLoop:
        for (Point point : perimeter) {
            for (SensorData data : sensors) {
                if (manhattan(data.sensor.x, data.sensor.y, point.x, point.y) <= data.beaconDist) {
                    continue pointLoop;
                }
            }
            return point;
        }
        throw new RuntimeException("Could not find answer!");
    }

    private static void readLine(String line, Collection<SensorData> sensors, Collection<Point> perimeter) {
        readLine(line, (sensor, beacon) -> {
            int manhattan = manhattan(sensor.x, sensor.y, beacon.x, beacon.y);
            sensors.add(new SensorData(sensor, manhattan));
            int x = sensor.x, y = sensor.y - (manhattan + 1);
            putPoint(perimeter, x, y);
            for (int i = 0; i < manhattan + 1; i++) {
                x++;
                y++;
                putPoint(perimeter, x, y);
            }
            for (int i = 0; i < manhattan + 1; i++) {
                x--;
                y++;
                putPoint(perimeter, x, y);
            }
            for (int i = 0; i < manhattan + 1; i++) {
                x--;
                y--;
                putPoint(perimeter, x, y);
            }
            for (int i = 0; i < manhattan; i++) {
                x++;
                y--;
                putPoint(perimeter, x, y);
            }
        });
    }

    private static void putPoint(Collection<Point> points, int x, int y) {
        if (0 <= x && x <= 4_000_000 && 0 <= y && y <= 4_000_000) {
            points.add(new Point(x, y));
        }
    }

    private static int manhattan(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    private static void readLine(String line, BiConsumer<Point, Point> consumer) {
        var matcher = PATTERN.matcher(line);
        if (matcher.matches()) {
            var sensor = new Point(
                Integer.parseInt(matcher.group(1)),
                Integer.parseInt(matcher.group(2))
            );
            var beacon = new Point(
                Integer.parseInt(matcher.group(3)),
                Integer.parseInt(matcher.group(4))
            );
            consumer.accept(sensor, beacon);
        } else {
            throw new IllegalArgumentException(line);
        }
    }

    private record Point(int x, int y) {
    }

    private record SensorData(Point sensor, int beaconDist) {
    }

    private enum Tile {
        SENSOR, BEACON, OBSTRUCTED
    }
}
