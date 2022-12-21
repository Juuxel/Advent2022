package juuxel.advent2022;

import java.util.HashMap;
import java.util.Map;
import java.util.function.*;

public final class Day21 {
    public static void main(String[] args) {
        Map<String, Monkey> monkeys = new HashMap<>();
        for (String line : args) {
            read(line, monkeys::put);
        }

        part1(monkeys);
        part2(monkeys);
    }

    private static void part1(Map<String, Monkey> monkeys) {
        Map<String, Long> values = new HashMap<>();
        Context context = new Context() {
            @Override
            public long resolve(String monkey) {
                var result = values.get(monkey);
                if (result == null) {
                    values.put(monkey, result = monkeys.get(monkey).resolve(this));
                }
                return result;
            }
        };

        System.out.println(context.resolve("root"));
    }

    private static void part2(Map<String, Monkey> monkeys) {
        Map<String, LinearCombination> linearForms = new HashMap<>();
        linearForms.put("humn", new LinearCombination(1, 0));
        Function<String, LinearCombination> resolver = new Function<>() {
            @Override
            public LinearCombination apply(String s) {
                LinearCombination result = linearForms.get(s);

                if (result == null) {
                    linearForms.put(s, result = monkeys.get(s).toLinearCombination(this));
                }

                return result;
            }
        };
        var root = (MathMonkey) monkeys.get("root");
        LinearCombination lhs = resolver.apply(root.a);
        LinearCombination rhs = resolver.apply(root.b);
        System.out.println((rhs.constant - lhs.constant) / (lhs.coefficient - rhs.coefficient));
    }

    static void read(String line, BiConsumer<String, Monkey> consumer) {
        String name = line.substring(0, 4);
        String tail = line.substring(6);
        if (tail.contains(" ")) {
            var parts = tail.split(" ", 3);
            Operator operator = switch (parts[1]) {
                case "+" -> Operator.ADD;
                case "-" -> Operator.SUBTRACT;
                case "*" -> Operator.MULTIPLY;
                case "/" -> Operator.DIVIDE;
                default -> throw new IllegalArgumentException("Unknown operator " + parts[1]);
            };
            consumer.accept(name, new MathMonkey(parts[0], parts[2], operator));
        } else {
            consumer.accept(name, new ConstantMonkey(Long.parseLong(tail)));
        }
    }

    /**
     * Represents {@code coefficient * x + constant} for some variable x.
     */
    private record LinearCombination(double coefficient, double constant) {
    }

    @FunctionalInterface
    private interface Context {
        long resolve(String monkey);
    }

    sealed interface Monkey {
        long resolve(Context context);
        LinearCombination toLinearCombination(Function<String, LinearCombination> resolver);
    }

    record ConstantMonkey(long value) implements Monkey {
        @Override
        public long resolve(Context context) {
            return value;
        }

        @Override
        public LinearCombination toLinearCombination(Function<String, LinearCombination> resolver) {
            return new LinearCombination(0, value);
        }
    }

    record MathMonkey(String a, String b, Operator operator) implements Monkey {
        @Override
        public long resolve(Context context) {
            return operator.applyAsLong(context.resolve(a), context.resolve(b));
        }

        @Override
        public LinearCombination toLinearCombination(Function<String, LinearCombination> resolver) {
            LinearCombination first = resolver.apply(a);
            LinearCombination second = resolver.apply(b);

            return switch (operator) {
                case ADD -> new LinearCombination(first.coefficient + second.coefficient, first.constant + second.constant);
                case SUBTRACT -> new LinearCombination(first.coefficient - second.coefficient, first.constant - second.constant);
                case MULTIPLY -> {
                    if (first.coefficient == 0) {
                        yield new LinearCombination(first.constant * second.coefficient, first.constant * second.constant);
                    } else if (second.coefficient == 0) {
                        yield new LinearCombination(first.coefficient * second.constant, first.constant * second.constant);
                    } else {
                        throw new IllegalArgumentException("Cannot multiply two linear polynomials!");
                    }
                }
                case DIVIDE -> {
                    if (second.coefficient != 0) {
                        throw new IllegalArgumentException("Cannot divide by a linear polynomial!");
                    }

                    yield new LinearCombination(first.coefficient / second.constant, first.constant / second.constant);
                }
            };
        }
    }

    enum Operator implements LongBinaryOperator {
        ADD(Long::sum),
        SUBTRACT((a, b) -> a - b),
        MULTIPLY((a, b) -> a * b),
        DIVIDE((a, b) -> a / b);

        private final LongBinaryOperator longOperator;

        Operator(LongBinaryOperator longOperator) {
            this.longOperator = longOperator;
        }

        @Override
        public long applyAsLong(long left, long right) {
            return longOperator.applyAsLong(left, right);
        }
    }
}
