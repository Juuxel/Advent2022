package juuxel.advent2022;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

public final class Day15 {
    private static final Pattern PATTERN = Pattern.compile("^Sensor at x=(.+), y=(.+): closest beacon is at x=(.+), y=(.+)$");
    private static final ConcurrentLinkedQueue<Progress> progressQueue = new ConcurrentLinkedQueue<>();

    public static void main(String[] args) throws Exception {
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

    private static void part2(String[] args) throws Exception {
        record SensorData(Point sensor, int closestDistance) {}
        List<SensorData> sensors = new ArrayList<>();
        for (String line : args) {
            readLine(line, (sensor, beacon) -> {
                sensors.add(new SensorData(sensor, manhattan(sensor.x, sensor.y, beacon.x, beacon.y)));
            });
        }
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 3);
        Thread progressThread = new Thread(() -> {
            float[] allProgress = new float[16];

            while (true) {
                Progress progress = progressQueue.poll();
                if (progress != null) {
                    allProgress[progress.index] = progress.x / 1_000_000f;

                    float progressSum = 0;
                    for (float v : allProgress) {
                        progressSum += v;
                    }
                    progressSum *= 100f / 16f;
                    System.out.printf("%.2f%%%n", progressSum);
                }
            }
        });
        progressThread.setName("Progress Reporter");
        progressThread.setDaemon(true);
        progressThread.start();
        int[] endpoints = new int[] { 0, 1_000_000, 2_000_000, 3_000_000, 4_000_001 };
        int threadIndex = 0;
        for (int xIndex = 0; xIndex < endpoints.length - 1; xIndex++) {
            for (int yIndex = 0; yIndex < endpoints.length - 1; yIndex++) {
                final int myIndex = threadIndex++;
                int startX = endpoints[xIndex];
                int endX = endpoints[xIndex + 1];
                int startY = endpoints[yIndex];
                int endY = endpoints[yIndex + 1];
                CompletableFuture.runAsync(() -> {
                    System.out.printf("Thread %s checking (%d, %d) -> (%d, %d)%n", Thread.currentThread().getName(), startX, startY, endX, endY);
                    for (int x = startX; x < endX; x++) {
                        progressQueue.add(new Progress(myIndex, x));
                        point: for (int y = startY; y < endY; y++) {
                            for (SensorData sensorData : sensors) {
                                if (manhattan(sensorData.sensor.x, sensorData.sensor.y, x, y) <= sensorData.closestDistance) {
                                    continue point;
                                }
                            }

                            long score = (long) x * 4_000_000 + (long) y;
                            System.out.println(score);
                        }
                    }
                }, executor);
            }
        }
        if (!executor.awaitTermination(10L, TimeUnit.HOURS)) {
            throw new IllegalStateException("Could not find answer in 10 hours");
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

    private record Progress(int index, int x) {
    }

    private record Point(int x, int y) {
    }

    private enum Tile {
        SENSOR, BEACON, OBSTRUCTED
    }
}
