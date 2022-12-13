package juuxel.advent2022;

import java.io.EOFException;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public final class Day13 {
    public static void main(String[] input) throws Exception {
        List<Integer> correctIndices = new ArrayList<>();
        int pairIndex = 0;
        for (int i = 0; i < input.length; i += 3) {
            pairIndex++;
            Element first = parse(new PushbackReader(new StringReader(input[i])));
            Element second = parse(new PushbackReader(new StringReader(input[i + 1])));
            if (compare(first, second) == TriState.TRUE) correctIndices.add(pairIndex);
        }
        System.out.println(correctIndices.stream().mapToInt(Integer::intValue).sum());

        List<Element> elements = new ArrayList<>(input.length + 2);
        for (String line : input) {
            if (line.isEmpty()) continue;
            elements.add(parse(new PushbackReader(new StringReader(line))));
        }
        Element marker1 = new Arr(List.of(new Arr(List.of(new Num(2)))));
        Element marker2 = new Arr(List.of(new Arr(List.of(new Num(6)))));
        elements.add(marker1);
        elements.add(marker2);
        elements.sort((a, b) -> compare(a, b).value);
        int decoderKey = (elements.indexOf(marker1) + 1) * (elements.indexOf(marker2) + 1);
        System.out.println(decoderKey);
    }

    private static Element parse(PushbackReader reader) throws IOException {
        int start = reader.read();
        if (start == -1) throw new EOFException();

        if (start == '[') {
            List<Element> elements = new ArrayList<>();
            int read;
            while ((read = reader.read()) != -1) {
                if (read == ',') continue;
                if (read == ']') break;
                reader.unread(read);
                elements.add(parse(reader));
            }
            return new Arr(elements);
        } else {
            StringBuilder buffer = new StringBuilder().append((char) start);
            int read;
            while (isDigit(read = reader.read())) {
                buffer.append((char) read);
            }
            if (read != -1) {
                reader.unread(read);
            }
            return new Num(Integer.parseInt(buffer.toString()));
        }
    }

    private static boolean isDigit(int c) {
        return c >= 0 && "0123456789".indexOf(c) >= 0;
    }

    private static TriState compare(Element a, Element b) {
        if (a instanceof Num aNum && b instanceof Num bNum) {
            if (aNum.value < bNum.value) {
                return TriState.TRUE;
            } else if (aNum.value > bNum.value) {
                return TriState.FALSE;
            } else {
                return TriState.DEFAULT;
            }
        }

        Arr aa = a instanceof Arr arr ? arr : new Arr(List.of(a));
        Arr ba = b instanceof Arr arr ? arr : new Arr(List.of(b));
        int len = Math.max(aa.elements.size(), ba.elements.size());
        for (int i = 0; i < len; i++) {
            if (i >= aa.elements.size()) return TriState.TRUE;
            if (i >= ba.elements.size()) return TriState.FALSE;
            var elementComparison = compare(aa.elements.get(i), ba.elements.get(i));
            if (elementComparison != TriState.DEFAULT) {
                return elementComparison;
            }
        }

        return TriState.DEFAULT;
    }

    private enum TriState {
        TRUE(-1), FALSE(1), DEFAULT(0);

        final int value;

        TriState(int value) {
            this.value = value;
        }
    }

    private sealed interface Element {}
    private record Num(int value) implements Element {}
    private record Arr(List<Element> elements) implements Element {}
}
