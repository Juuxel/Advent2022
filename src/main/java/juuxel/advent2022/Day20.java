package juuxel.advent2022;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class Day20 {
    public static void main(String[] args) {
        // Set up
        List<Integer> numbers = read(args);
        List<Integer> indices = new ArrayList<>(numbers.size());
        for (int i = 0; i < numbers.size(); i++) {
            indices.add(i);
        }

        //System.out.println("Initial: " + numbers);

        // Start mixing
        for (int i = 0; i < numbers.size(); i++) {
            int value = numbers.get(i);
            System.out.printf("Applying %d (%d/%d)%n", value, i + 1, numbers.size());
            boolean negative = value < 0;
            int count = Math.abs(value);
            for (int j = 0; j < count; j++) {
                mixSingle(indices, i, indices.indexOf(i), negative);
            }
            /*int fromIndex = indices.indexOf(i);
            int toIndex = fromIndex + value;
            if (toIndex < 0) toIndex++;
            toIndex = wrap(toIndex, numbers.size());
            indices.remove(fromIndex);
            System.out.println("  Removed: " + indices.stream().map(numbers::get).toList());
            System.out.println("  Adding back at " + toIndex + " (raw value: " + (fromIndex + value) + ")");
            indices.add(toIndex, i);*/

            //System.out.println("  Mixed: " + indices.stream().map(numbers::get).toList());
        }

        // Get values
        int zeroIndex = indices.indexOf(numbers.indexOf(0));
        List<Integer> mixed = indices.stream().map(numbers::get).toList();
        int x = mixed.get((1000 + zeroIndex) % mixed.size());
        int y = mixed.get((2000 + zeroIndex) % mixed.size());
        int z = mixed.get((3000 + zeroIndex) % mixed.size());

        System.out.println(x + y + z);
    }

    private static <E> void mixSingle(List<E> list, E value, int fromIndex, boolean negative) {
        int size = list.size();
        list.remove(fromIndex);
        int toIndex;
        if (fromIndex == 0 && negative) {
            toIndex = size - 2;
        } else if (fromIndex == 1 && negative) {
            toIndex = size - 1;
        } else if (fromIndex == size - 1 && !negative) {
            toIndex = 1;
        } else {
            toIndex = fromIndex + (negative ? -1 : 1);
        }
        list.add(toIndex, value);
    }

    private static int wrap(int a, int b) {
        while (a < 0) {
            a += b;
        }
        return a % b;
    }

    private static List<Integer> read(String[] args) {
        return Stream.of(args).map(Integer::parseInt).toList();
    }
}
