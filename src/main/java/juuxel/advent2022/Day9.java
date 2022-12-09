package juuxel.advent2022;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Day9 {
    public static void main(String[] lines) {
        List<Direction> directions = new ArrayList<>();

        for (String line : lines) {
            var split = line.split(" ");
            if (split.length != 2) {
                throw new IllegalArgumentException("Broken line? " + line);
            }
            int count = Integer.parseInt(split[1]);
            for (int i = 0; i < count; i++) {
                directions.add(switch (split[0]) {
                    case "U" -> Direction.UP;
                    case "D" -> Direction.DOWN;
                    case "L" -> Direction.LEFT;
                    case "R" -> Direction.RIGHT;
                    default -> throw new IllegalArgumentException("Unknown direction " + line);
                });
            }
        }

        Set<Point> pointsPart1 = new HashSet<>();
        pointsPart1.add(new Point(0, 0));
        Set<Point> pointsPart2 = new HashSet<>();
        pointsPart2.add(new Point(0, 0));
        PointMut head = new PointMut();
        PointMut tail = new PointMut();
        PointMut[] nodes = new PointMut[10];
        nodes[0] = head;
        for (int i = 1; i < 10; i++) {
            nodes[i] = new PointMut();
        }

        for (Direction direction : directions) {
            switch (direction) {
                case UP -> head.y++;
                case DOWN -> head.y--;
                case LEFT -> head.x--;
                case RIGHT -> head.x++;
            }

            simulate(head, tail);
            for (int i = 0; i < nodes.length - 1; i++) {
                simulate(nodes[i], nodes[i + 1]);
            }

            pointsPart1.add(new Point(tail.x, tail.y));
            PointMut part2Tail = nodes[nodes.length - 1];
            pointsPart2.add(new Point(part2Tail.x, part2Tail.y));
        }

        System.out.println(pointsPart1.size());
        System.out.println(pointsPart2.size());
    }

    private static void simulate(PointMut head, PointMut tail) {
        if (!isWithinDistance(tail.x, tail.y, head.x, head.y)) {
            if (tail.x == head.x) {
                if (tail.y < head.y) {
                    if (head.y - tail.y >= 2) {
                        tail.y++;
                    }
                } else if (tail.y > head.y) {
                    if (tail.y - head.y >= 2) {
                        tail.y--;
                    }
                }
            } else if (tail.y == head.y) {
                if (tail.x < head.x) {
                    if (head.x - tail.x >= 2) {
                        tail.x++;
                    }
                } else { // tail.x > head.x is implied since they're not equal and not LT
                    if (tail.x - head.x >= 2) {
                        tail.x--;
                    }
                }
            } else {
                if (tail.x < head.x && tail.y < head.y) {
                    tail.x++;
                    tail.y++;
                } else if (tail.x > head.x && tail.y > head.y) {
                    tail.x--;
                    tail.y--;
                } else if (tail.x < head.x) { // tail.y > head.y is implied
                    tail.x++;
                    tail.y--;
                } else { // tail.x > head.x && tail.y < head.y is implied
                    tail.x--;
                    tail.y++;
                }
            }
        }
    }

    private static boolean isWithinDistance(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) <= 1 && Math.abs(y1 - y2) <= 1;
    }

    private record Point(int x, int y) {
    }

    private static class PointMut {
        int x;
        int y;
    }

    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
}
