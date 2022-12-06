package juuxel.advent2022;

import java.util.HashSet;
import java.util.Set;

public final class Day6 {
    public static void part1(String data) {
        System.out.println(findMarker(data, 4));
    }

    public static void part2(String data) {
        System.out.println(findMarker(data, 14));
    }

    private static int findMarker(String data, int length) {
        Set<Character> chars = new HashSet<>(4);
        for (int i = length - 1; i < data.length(); i++) {
            for (int j = 0; j < length; j++) {
                chars.add(data.charAt(i - j));
            }

            if (chars.size() == length) {
                return i + 1;
            }

            chars.clear();
        }
        throw new IllegalStateException("Could not find marker of length " + length);
    }
}
