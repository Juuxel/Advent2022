package juuxel.advent2022;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Day3 {
    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws Exception {
        int part1 = Loader.lines(3)
            .mapToInt(line -> {
                int length = line.length() / 2;
                String firstCompartment = line.substring(0, length);
                String secondCompartment = line.substring(length);
                char common = 0;

                for (int i = 0; i < length; i++) {
                    char c = firstCompartment.charAt(i);
                    if (secondCompartment.indexOf(c) >= 0) {
                        common = c;
                        break;
                    }
                }

                if (common == 0) throw new IllegalStateException("common == 0");

                return priority(common);
            })
            .sum();
        System.out.println(part1);
    }

    private static void part2() throws Exception {
        List<String> lines = Loader.lines(3).toList();
        int part2 = 0;
        for (int i = 0; i < lines.size(); i += 3) {
            Set<Character> chars = new HashSet<>();
            String first = lines.get(i), second = lines.get(i + 1), third = lines.get(i + 2);
            for (int j = 0; j < first.length(); j++) {
                chars.add(first.charAt(j));
            }
            chars.removeIf(c -> second.indexOf(c) < 0 || third.indexOf(c) < 0);
            char common = chars.iterator().next();
            part2 += priority(common);
        }
        System.out.println(part2);
    }

    private static int priority(char c) {
        return ('a' <= c && c <= 'z') ? c - 96 : c - 64 + 26;
    }
}
