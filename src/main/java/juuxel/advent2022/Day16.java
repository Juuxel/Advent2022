package juuxel.advent2022;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Deprecated // doesn't work
public class Day16 {
    private static final Pattern PATTERN = Pattern.compile("^Valve (.+) has flow rate=(.+); tunnels? leads? to valves? (.+)$");

    public static void main(String[] args) {
        new Part1(args).simulate();
    }

    private static class Part1 {
        private static final double MOVE_COST = 0.6;
        private int totalFlowRate = 0;
        private int totalFlow = 0;
        private final Set<String> open = new HashSet<>();
        private final Graph<String, Integer> graph = new Graph<>();

        Part1(String[] args) {
            for (String line : args) {
                readLine(graph, line);
            }
        }

        void simulate() {
            String previous = null;
            String current = "AA";
            for (int i = 0; i < 30; i++) {
                totalFlow += totalFlowRate;
                var currentNode = graph.get(current);
                System.out.print("Minute " + (i + 1) + ": ");
                double bestWeight = getSelfWeight(currentNode, Double.MIN_VALUE);
                String bestMove = current;
                for (String candidate : currentNode.connectsTo) {
                    double weight = MOVE_COST * getWeight(graph.get(candidate), Math.min(2, 29 - i), Set.of(current));
                    if (weight >= bestWeight) {
                        bestWeight = weight;
                        bestMove = candidate;
                    }
                }

                if (bestMove.equals(current)) {
                    if (!open.contains(current)) {
                        open.add(current);
                        System.out.println("Opened " + current);
                        totalFlowRate += currentNode.value;
                    } else {
                        System.out.println("Stayed in " + current);
                    }
                } else {
                    previous = current;
                    current = bestMove;
                    System.out.println("Moved to " + bestMove);
                }
            }

            System.out.println(totalFlow);
        }

        private double getSelfWeight(Node<String, Integer> node, double fallback) {
            return !open.contains(node.key) ? node.value : fallback;
        }

        private double getWeight(Node<String, Integer> node, int depth, Set<String> exclude) {
            if (depth < 0) return 0;
            double weight = getSelfWeight(node, 0.0);

            for (String next : node.connectsTo) {
                if (exclude.contains(next)) continue;
                Set<String> newExclude = new HashSet<>(exclude);
                newExclude.add(node.key);
                weight += getWeight(graph.get(next), depth - 1, newExclude) * MOVE_COST;
            }

            return weight;
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
