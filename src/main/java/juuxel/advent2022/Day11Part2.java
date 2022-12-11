package juuxel.advent2022;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// somehow broken, don't want to debug
@Deprecated
public final class Day11Part2 {
    public static void main(String[] args) {
        Map<Integer, Monkey> monkeys = partition(args, String::isEmpty).stream()
            .map(Day11Part2::readMonkey)
            .collect(Collectors.toMap(Monkey::id, Function.identity()));
        Map<Integer, Long> inspectionCounts = new TreeMap<>();

        for (int i = 0; i < 10_000; i++) {
            for (Monkey monkey : monkeys.values()) {
                for (FactorSet item : monkey.items) {
                    FactorSet newValue = monkey.operation.eval(item);
                    if (newValue.isDivisibleBy(monkey.divisibleBy)) {
                        monkeys.get(monkey.ifTrue).items.add(newValue);
                    } else {
                        monkeys.get(monkey.ifFalse).items.add(newValue);
                    }
                    inspectionCounts.put(monkey.id, 1L + inspectionCounts.getOrDefault(monkey.id, 0L));
                }

                monkey.items.clear();
            }

            System.out.println("end of round " + (i + 1) + ": " + inspectionCounts);
        }

        TreeSet<Long> inspectionCountsSorted = new TreeSet<>(Comparator.reverseOrder());
        inspectionCountsSorted.addAll(inspectionCounts.values());
        Iterator<Long> iter = inspectionCountsSorted.iterator();
        long monkeyBusiness = iter.next() * iter.next();
        System.out.println(monkeyBusiness);
    }

    private static Monkey readMonkey(List<String> lines) {
        int id = Integer.parseInt("" + lines.get(0).substring(7, lines.get(0).length() - 1));
        var startingItems =
            Stream.of(lines.get(1).substring("  Starting items: ".length()).split(", "))
                .map(Long::parseLong)
                .map(FactorSet::of)
                .toList();
        String[] operationParts = lines.get(2).substring("  Operation: new = ".length()).split(" ");
        Term term1 = readTerm(operationParts[0]);
        BiOp operation = operationParts[1].equals("+")
            ? new AddOp(term1, Long.parseLong(operationParts[2]))
            : new MulOp(term1, readTerm(operationParts[2]));
        int divisibleBy = Integer.parseInt(lines.get(3).substring("  Test: divisible by ".length()));
        int ifTrue = Integer.parseInt(lines.get(4).substring("    If true: throw to monkey ".length()));
        int ifFalse = Integer.parseInt(lines.get(5).substring("    If false: throw to monkey ".length()));
        return new Monkey(id, new ArrayList<>(startingItems), operation, divisibleBy, ifTrue, ifFalse);
    }

    private static Term readTerm(String s) {
        if (s.equals("old")) return OldTerm.INSTANCE;
        return new ConstantTerm(Long.parseLong(s));
    }

    private record Monkey(int id, List<FactorSet> items, BiOp operation, int divisibleBy, int ifTrue, int ifFalse) {
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

    private record FactorSet(Multiset<Long> factors) {
        static FactorSet of(long l) {
            if (l < 0) throw new IllegalArgumentException("Negative value for factor set: " + l);

            Multiset<Long> factors = HashMultiset.create();
            while (l > 1) {
                for (long divisor = 2; divisor <= l; divisor++) {
                    if (l % divisor == 0) {
                        factors.add(divisor);
                        l /= divisor;
                    }
                }
            }

            return new FactorSet(factors);
        }

        static FactorSet of(final BigInteger bi) {
            if (bi.signum() == -1) throw new IllegalArgumentException("Negative value for factor set: " + bi);

            Multiset<Long> factors = HashMultiset.create();
            BigInteger current = bi;
            while (current.compareTo(BigInteger.ONE) > 0) {
                for (BigInteger divisor = BigInteger.TWO; divisor.compareTo(current) <= 0; divisor = divisor.add(BigInteger.ONE)) {
                    var divRem = current.divideAndRemainder(divisor);
                    if (divRem[1].equals(BigInteger.ZERO)) {
                        factors.add(divisor.longValueExact());
                        current = divRem[0];
                    }
                }
            }

            FactorSet result = new FactorSet(factors);
            if (!result.toBigInteger().equals(bi)) {
                throw new AssertionError("FactorSet for " + bi + " doesn't equal itself");
            }
            return result;
        }

        boolean isDivisibleBy(long l) {
            return factors.contains(l);
        }

        FactorSet multiply(FactorSet other) {
            Multiset<Long> newFactors = HashMultiset.create();
            newFactors.addAll(this.factors);
            newFactors.addAll(other.factors);
            return new FactorSet(newFactors);
        }

        FactorSet add(long l) {
            for (long factor : factors) {
                if (l % factor == 0) {
                    // Discard already present factors
                    l /= factor;
                }
            }

            if (l == 1) {
                return this;
            }

            return of(toBigInteger().add(BigInteger.valueOf(l)));
        }

        private BigInteger toBigInteger() {
            BigInteger product = BigInteger.ONE;
            for (long factor : factors) {
                product = product.multiply(BigInteger.valueOf(factor));
            }
            return product;
        }
    }

    private sealed interface BiOp {
        FactorSet eval(FactorSet old);
    }

    private record AddOp(Term first, long second) implements BiOp {
        @Override
        public FactorSet eval(FactorSet old) {
            return first.getValue(old).add(second);
        }
    }

    private record MulOp(Term first, Term second) implements BiOp {
        @Override
        public FactorSet eval(FactorSet old) {
            var firstVal = first.getValue(old);
            var secondVal = second.getValue(old);
            return firstVal.multiply(secondVal);
        }
    }

    private sealed interface Term {
        FactorSet getValue(FactorSet old);
    }

    private static final class ConstantTerm implements Term {
        private final FactorSet factorSet;

        ConstantTerm(long value) {
            this.factorSet = FactorSet.of(value);
        }

        @Override
        public FactorSet getValue(FactorSet old) {
            return factorSet;
        }
    }

    private enum OldTerm implements Term {
        INSTANCE;

        @Override
        public FactorSet getValue(FactorSet old) {
            return old;
        }
    }
}
