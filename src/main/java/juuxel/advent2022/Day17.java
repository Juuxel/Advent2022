package juuxel.advent2022;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public final class Day17 {
    private static final boolean DEBUG_PRINT = false;
    private static final int LEFT_WALL = 0;
    private static final int RIGHT_WALL = 8;
    private static final Shape[] SHAPES = new Shape[] {
        new Shape(4, 1, new boolean[][] {
            { true, true, true, true },
            { false, false, false, false },
            { false, false, false, false },
            { false, false, false, false },
        }),
        new Shape(3, 3, new boolean[][] {
            { false, true, false, false },
            { true, true, true, false },
            { false, true, false, false },
            { false, false, false, false },
        }),
        new Shape(3, 3, new boolean[][] {
            { false, false, true, false },
            { false, false, true, false },
            { true, true, true, false },
            { false, false, false, false },
        }),
        new Shape(1, 4, new boolean[][] {
            { true, false, false, false },
            { true, false, false, false },
            { true, false, false, false },
            { true, false, false, false },
        }),
        new Shape(2, 2, new boolean[][] {
            { true, true, false, false },
            { true, true, false, false },
            { false, false, false, false },
            { false, false, false, false },
        })
    };

    public static void main(String[] args) {
        JetPattern jetPattern = new JetPattern(args[0]);
        int maxY = 0;
        Set<Point> occupiedPoints = new HashSet<>();

        for (int i = 0; i < 7; i++) {
            occupiedPoints.add(new Point(i, 0));
        }

        for (int shapeIndex = 0; shapeIndex < 2022; shapeIndex++) {
            Shape shape = SHAPES[shapeIndex % SHAPES.length];
            int x = 3, y = maxY + 3 + shape.height;

            if (DEBUG_PRINT && shapeIndex < 10) {
                System.out.println("Round " + (shapeIndex + 1));
                printSnapshot(occupiedPoints, maxY, shape, x, y);
            }

            while (true) {
                Direction horizontal = jetPattern.next();
                int xd = horizontal == Direction.LEFT ? -1 : 1;
                if (shape.canPlaceAt(x + xd, y, occupiedPoints)) {
                    x += xd;
                }

                if (DEBUG_PRINT && shapeIndex < 3) {
                    System.out.printf("Pushed by jet to the %s: (%s)%n", horizontal, jetPattern.debugString(jetPattern.index - 1));
                    printSnapshot(occupiedPoints, maxY, shape, x, y);
                }

                if (shape.canPlaceAt(x, y - 1, occupiedPoints)) {
                    y--;

                    if (DEBUG_PRINT && shapeIndex < 3) {
                        System.out.println("Fell:");
                        printSnapshot(occupiedPoints, maxY, shape, x, y);
                    }
                } else {
                    // Stop the shape
                    for (int sx = 0; sx < shape.width; sx++) {
                        for (int sy = 0; sy < shape.height; sy++) {
                            if (shape.rows[sy][sx]) {
                                occupiedPoints.add(new Point(x + sx, y - sy));
                            }
                        }
                    }

                    maxY = Math.max(maxY, y);

                    if (DEBUG_PRINT && shapeIndex < 3) {
                        System.out.println("Solidified:");
                        printSnapshot(occupiedPoints, maxY, shape, x, y);
                    }

                    break;
                }
            }
        }

        System.out.println(maxY);
    }

    private static void printSnapshot(Set<Point> occupiedPoints, int maxY, Shape shape, int shapeX, int shapeY) {
        Point point = new Point(0, 0);
        for (int y = Math.max(maxY, shapeY); y > 0; y--) {
            System.out.print('|');
            for (int x = 1; x <= 7; x++) {
                point.x = x;
                point.y = y;
                char c;
                if (occupiedPoints.contains(point)) {
                    c = '#';
                } else if (shapeX <= x && x < shapeX + shape.width && shapeY >= y && y > shapeY - shape.height &&
                    shape.rows[shapeY - y][x - shapeX]) {
                    c = '@';
                } else {
                    c = '.';
                }
                System.out.print(c);
            }

            System.out.println('|');
        }
        System.out.println("+-------+");
    }

    private static final class Point {
        int x;
        int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Point point && x == point.x && y == point.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    private record Shape(int width, int height, boolean[][] rows) {
        Shape {
            if (rows.length != 4) throw new IllegalArgumentException("Shape must be exactly 4 units tall");
            for (boolean[] row : rows) {
                if (row.length != 4) throw new IllegalArgumentException("Shape must be exactly 4 units wide");
            }
        }

        boolean canPlaceAt(int x, int y, Set<Point> occupiedPoints) {
            if (x <= LEFT_WALL || x + width > RIGHT_WALL) return false;

            Point point = new Point(0, 0);
            for (int i = 0; i < width; i++) {
                point.x = x + i;
                for (int j = 0; j < height; j++) {
                    point.y = y - j;
                    if (rows[j][i] && occupiedPoints.contains(point)) return false;
                }
            }

            return true;
        }
    }

    private enum Direction {
        LEFT, RIGHT
    }

    private static final class JetPattern implements Iterator<Direction> {
        private final String input;
        private int index = 0;

        private JetPattern(String input) {
            this.input = input;
        }

        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        public Direction next() {
            index %= input.length();
            return input.charAt(index++) == '>' ? Direction.RIGHT : Direction.LEFT;
        }

        String debugString(int index) {
            String head = index > 0 ? input.substring(0, index) : "";
            String tail = index < input.length() - 1 ? input.substring(index + 1) : "";
            return head + ' ' + input.charAt(index) + ' ' + tail;
        }
    }
}
