package juuxel.advent2022

import java.util.stream.Stream

fun main(lines: Stream<String>) {
    val ranges = lines
        .map {
            val (firstS, secondS) = it.split(',', limit = 2)
            val first = readRange(firstS)
            val second = readRange(secondS)
            first to second
        }
        .toList()
    val part1 = ranges.count { (first, second) ->
        (first.first <= second.first && second.last <= first.last) ||
            (second.first <= first.first && first.last <= second.last)
    }
    println(part1)
    val part2 = ranges.count { (a, b) ->
        val (first, second) = if (a.first < b.first) a to b else b to a
        first.last >= second.first
    }
    println(part2)
}

private fun readRange(str: String): IntRange {
    val (start, end) = str.split('-', limit = 2)
    return start.toInt()..end.toInt()
}
