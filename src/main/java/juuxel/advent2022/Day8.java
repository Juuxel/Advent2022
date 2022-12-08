package juuxel.advent2022;

public final class Day8 {
    public static void part1(String[] rows) {
        int height = rows.length;
        int width = rows[0].length();
        boolean[][] seenTrees = new boolean[width][height];

        for (int x = 0; x < width; x++) {
            {
                int max = -1;
                // Top-down
                for (int y = 0; y < height; y++) {
                    int current = Integer.parseInt("" + rows[y].charAt(x));
                    if (current > max) {
                        max = current;
                        seenTrees[x][y] = true;
                    }
                }
            }
            {
                int max = -1;
                // Bottom-up
                for (int y = height - 1; y >= 0; y--) {
                    int current = Integer.parseInt("" + rows[y].charAt(x));
                    if (current > max) {
                        max = current;
                        seenTrees[x][y] = true;
                    }
                }
            }
        }

        for (int y = 0; y < height; y++) {
            {
                int max = -1;
                // LTR
                for (int x = 0; x < width; x++) {
                    int current = Integer.parseInt("" + rows[y].charAt(x));
                    if (current > max) {
                        max = current;
                        seenTrees[x][y] = true;
                    }
                }
            }
            {
                int max = -1;
                // RTL
                for (int x = width - 1; x >= 0; x--) {
                    int current = Integer.parseInt("" + rows[y].charAt(x));
                    if (current > max) {
                        max = current;
                        seenTrees[x][y] = true;
                    }
                }
            }
        }

        int part1 = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.out.print(seenTrees[x][y] ? 'X' : '.');
                if (seenTrees[x][y]) {
                    part1++;
                }
            }
            System.out.println();
        }
        System.out.println(part1);
    }

    public static void part2(String[] rows) {
        int height = rows.length;
        int width = rows[0].length();
        int maxScenicScore = 0;

        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                maxScenicScore = Math.max(maxScenicScore, scenicScore(x, y, rows));
            }
        }

        System.out.println(maxScenicScore);
    }

    private static int scenicScore(int x, int y, String[] rows) {
        String row = rows[y];
        int targetHeight = Integer.parseInt("" + row.charAt(x));

        int left = 0;
        for (int x2 = x - 1; x2 >= 0; x2--) {
            left++;
            if (Integer.parseInt("" + row.charAt(x2)) >= targetHeight) {
                break;
            }
        }

        int right = 0;
        for (int x2 = x + 1; x2 < row.length(); x2++) {
            right++;
            if (Integer.parseInt("" + row.charAt(x2)) >= targetHeight) {
                break;
            }
        }

        int top = 0;
        for (int y2 = y - 1; y2 >= 0; y2--) {
            top++;
            if (Integer.parseInt("" + rows[y2].charAt(x)) >= targetHeight) {
                break;
            }
        }

        int bottom = 0;
        for (int y2 = y + 1; y2 < rows.length; y2++) {
            bottom++;
            if (Integer.parseInt("" + rows[y2].charAt(x)) >= targetHeight) {
                break;
            }
        }

        return left * right * top * bottom;
    }
}
