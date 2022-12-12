package juuxel.advent2022;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Day11 {
    public static void part1(String[] args) {
        main(args, false);
    }

    public static void part2(String[] args) {
        main(args, true);
    }

    private static void main(String[] args, boolean part2) {
        Map<Integer, Monkey> monkeys = partition(args, String::isEmpty).stream()
            .map(Day11::readMonkey)
            .collect(Collectors.toMap(Monkey::id, Function.identity()));
        int mod = monkeys.values().stream().mapToInt(Monkey::divisibleBy).reduce(1, (a, b) -> a * b);
        Map<Integer, Long> inspectionCounts = new TreeMap<>();

        for (int i = 0; i < (part2 ? 10_000 : 20); i++) {
            for (Monkey monkey : monkeys.values()) {
                for (long item : monkey.items) {
                    long newValue = monkey.operation.eval(item);
                    if (part2) {
                        newValue %= mod;
                    } else {
                        newValue /= 3;
                    }
                    if (newValue % monkey.divisibleBy == 0) {
                        monkeys.get(monkey.ifTrue).items.add(newValue);
                    } else {
                        monkeys.get(monkey.ifFalse).items.add(newValue);
                    }
                    inspectionCounts.put(monkey.id, 1L + inspectionCounts.getOrDefault(monkey.id, 0L));
                }

                monkey.items.clear();
            }
        }

        TreeSet<Long> inspectionCountsSorted = new TreeSet<>(Comparator.reverseOrder());
        inspectionCountsSorted.addAll(inspectionCounts.values());
        Iterator<Long> iter = inspectionCountsSorted.iterator();
        long monkeyBusiness = iter.next() * iter.next();
        System.out.println(monkeyBusiness);
    }

    private static Monkey readMonkey(List<String> lines) {
        int id = Integer.parseInt("" + lines.get(0).substring(7, lines.get(0).length() - 1));
        List<Long> startingItems =
            Stream.of(lines.get(1).substring("  Starting items: ".length()).split(", "))
                .map(Long::parseLong)
                .toList();
        String[] operationParts = lines.get(2).substring("  Operation: new = ".length()).split(" ");
        Term term1 = readTerm(operationParts[0]), term2 = readTerm(operationParts[2]);
        BiOp operation = operationParts[1].equals("+") ? new AddOp(term1, term2) : new MulOp(term1, term2);
        int divisibleBy = Integer.parseInt(lines.get(3).substring("  Test: divisible by ".length()));
        int ifTrue = Integer.parseInt(lines.get(4).substring("    If true: throw to monkey ".length()));
        int ifFalse = Integer.parseInt(lines.get(5).substring("    If false: throw to monkey ".length()));
        return new Monkey(id, new ArrayList<>(startingItems), operation, divisibleBy, ifTrue, ifFalse);
    }

    private static Term readTerm(String s) {
        if (s.equals("old")) return OldTerm.INSTANCE;
        return new ConstantTerm(Integer.parseInt(s));
    }

    private record Monkey(int id, List<Long> items, BiOp operation, int divisibleBy, int ifTrue, int ifFalse) {
    }

    private static List<List<String>> partition(String[] input, Predicate<String> filter) {
        List<List<String>> result = new ArrayList<>();
        List<String> buffer = new ArrayList<>();

        for (String s : input) {
            if (filter.test(s)) {
                result.add(List.copyOf(buffer));
                buffer.clear();
            } else {
                buffer.add(s);
            }
        }

        if (!buffer.isEmpty()) {
            result.add(buffer);
        }

        return result;
    }

    private sealed interface BiOp {
        long eval(long old);
    }

    private record AddOp(Term first, Term second) implements BiOp {
        @Override
        public long eval(long old) {
            return first.getValue(old) + second.getValue(old);
        }
    }

    private record MulOp(Term first, Term second) implements BiOp {
        @Override
        public long eval(long old) {
            return first.getValue(old) * second.getValue(old);
        }
    }

    private sealed interface Term {
        long getValue(long old);
    }

    private record ConstantTerm(long value) implements Term {
        @Override
        public long getValue(long old) {
            return value;
        }
    }

    private enum OldTerm implements Term {
        INSTANCE;

        @Override
        public long getValue(long old) {
            return old;
        }
    }
}
