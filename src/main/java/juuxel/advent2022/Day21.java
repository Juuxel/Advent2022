package juuxel.advent2022;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.LongBinaryOperator;

public final class Day21 {
    private static final LongBinaryOperator ADD = Long::sum;
    private static final LongBinaryOperator SUBTRACT = (a, b) -> a - b;
    private static final LongBinaryOperator MULTIPLY = (a, b) -> a * b;
    private static final LongBinaryOperator DIVIDE = (a, b) -> a / b;

    public static void main(String[] args) {
        Map<String, Monkey> monkeys = new HashMap<>();
        for (String line : args) {
            read(line, monkeys::put);
        }

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

    private static void read(String line, BiConsumer<String, Monkey> consumer) {
        String name = line.substring(0, 4);
        String tail = line.substring(6);
        if (tail.contains(" ")) {
            var parts = tail.split(" ", 3);
            LongBinaryOperator operator = switch (parts[1]) {
                case "+" -> ADD;
                case "-" -> SUBTRACT;
                case "*" -> MULTIPLY;
                case "/" -> DIVIDE;
                default -> throw new IllegalArgumentException("Unknown operator " + parts[1]);
            };
            consumer.accept(name, new MathMonkey(parts[0], parts[2], operator));
        } else {
            consumer.accept(name, new ConstantMonkey(Long.parseLong(tail)));
        }
    }

    @FunctionalInterface
    private interface Context {
        long resolve(String monkey);
    }

    private sealed interface Monkey {
        long resolve(Context context);
    }

    private record ConstantMonkey(long value) implements Monkey {
        @Override
        public long resolve(Context context) {
            return value;
        }
    }

    private record MathMonkey(String a, String b, LongBinaryOperator operator) implements Monkey {
        @Override
        public long resolve(Context context) {
            return operator.applyAsLong(context.resolve(a), context.resolve(b));
        }
    }
}
