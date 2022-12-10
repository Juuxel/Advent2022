package juuxel.advent2022;

import java.util.ArrayList;
import java.util.List;

public final class Day10 {
    private static final int[] CYCLES_OF_INTEREST = { 20, 60, 100, 140, 180, 220 };

    public static void main(String[] lines) {
        int x = 1;
        List<Integer> signalStrengthsDuringCycles = new ArrayList<>();
        List<Insn> instructions = parse(lines);

        int cycle = 1;
        for (Insn insn : instructions) {
            // DURING phase
            // P1: Calculate signal strength
            signalStrengthsDuringCycles.add(cycle * x);

            // P2: Draw pixel
            int horizontalPos = (cycle - 1) % 40;
            System.out.print(Math.abs(horizontalPos - x) <= 1 ? '#' : '.');

            // P2: Switch to the next row
            if (cycle % 40 == 0) {
                System.out.println();
            }

            // AFTER phase

            // Apply current addx instruction
            if (insn instanceof AddX addx) {
                x += addx.value();
            }

            // Move to the next cycle
            cycle++;
        }

        int part1 = 0;
        for (int coi : CYCLES_OF_INTEREST) {
            part1 += signalStrengthsDuringCycles.get(coi - 1);
        }

        System.out.println(part1);
    }

    static List<Insn> parse(String[] lines) {
        List<Insn> instructions = new ArrayList<>();

        for (String line : lines) {
            if (line.startsWith("addx ")) {
                // addx is twice as long so we simulate it with a NoOp followed by AddX
                AddX addx = new AddX(Integer.parseInt(line.substring("addx ".length())));
                instructions.add(new NoOp(addx));
                instructions.add(addx);
            } else {
                instructions.add(new NoOp(null));
            }
        }

        return instructions;
    }

    sealed interface Insn {
    }

    // The source is for the visualisation in Swing.
    record NoOp(AddX source) implements Insn {
        @Override
        public String toString() {
            return source == null ? "noop" : "addx " + source.value + " 1/2";
        }
    }

    record AddX(int value) implements Insn {
        @Override
        public String toString() {
            return "addx " + value + " 2/2";
        }
    }
}
