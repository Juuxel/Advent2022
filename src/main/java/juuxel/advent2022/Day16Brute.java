package juuxel.advent2022;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Deprecated // even if it works, takes way too much time and memory so can't be used :(
public class Day16Brute {
    private static final Pattern PATTERN = Pattern.compile("^Valve (.+) has flow rate=(.+); tunnels? leads? to valves? (.+)$");

    public static void main(String[] args) throws Exception {
        var executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        new Part1(executor, Loader.lines(16).toArray(String[]::new)).run();
    }

    private static class Part1 implements Runnable {
        private final Executor executor;
        private int totalFlowRate = 0;
        private int totalFlow = 0;
        private final Set<String> open;
        private final Graph<String, Integer> graph;
        private final String current;
        private final int step;

        Part1(Executor executor, String[] args) {
            this.executor = executor;
            open = new HashSet<>();
            graph = new Graph<>();
            current = "AA";
            step = 0;

            for (String line : args) {
                readLine(graph, line);
            }
        }

        Part1(Executor executor, Part1 other, String current) {
            this.executor = executor;
            totalFlowRate = other.totalFlowRate;
            totalFlow = other.totalFlow;
            open = new HashSet<>(other.open);
            graph = other.graph;
            this.current = current;
            step = other.step + 1;
        }

        @Override
        public void run() {
            totalFlow += totalFlowRate;

            if (step >= 29) {
                System.out.println(totalFlow);
                return;
            }

            var currentNode = graph.get(current);
            List<String> moves = new ArrayList<>(currentNode.connectsTo);
            moves.add(current);

            for (String move : moves) {
                Part1 next = new Part1(executor, this, move);
                if (move.equals(current)) {
                    if (!open.contains(current)) {
                        next.open.add(current);
                        next.totalFlowRate += currentNode.value;
                    }
                }
                executor.execute(next);
            }
        }
    }

    private static void readLine(Graph<String, Integer> graph, String line) {
        Matcher matcher = PATTERN.matcher(line);
        if (matcher.matches()) {
            String key = matcher.group(1);
            int flowRate = Integer.parseInt(matcher.group(2));
            String[] connectsTo = matcher.group(3).split(", ");
            graph.put(key, new Node<>(key, flowRate, Set.of(connectsTo)));
        }
    }

    private record Node<K, V>(K key, V value, Set<K> connectsTo) {}
    private static final class Graph<K, V> {
        private final Map<K, Node<K, V>> nodes = new HashMap<>();

        Node<K, V> get(K key) {
            return Objects.requireNonNull(nodes.get(key), "Node " + key + " is missing!");
        }

        void put(K key, Node<K, V> node) {
            nodes.put(key, node);
        }
    }
}
