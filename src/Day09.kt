fun main() {
    fun findMins(input: List<String>): Map<Pair<Int, Int>, Int> {
        val map = input.mapIndexed { rowIndex, row -> row.mapIndexed { columnIndex, c -> Pair(rowIndex, columnIndex) to c.digitToInt() }.toMap() }
            .flatMap { it.asSequence() }
            .groupBy({ it.key }, { it.value })
            .mapValues { it.value.single() }

        val minimums = map.filter {
            map.getOrDefault(it.key.copy(first = it.key.first - 1), 10) > it.value &&
                    map.getOrDefault(it.key.copy(first = it.key.first + 1), 10) > it.value &&
                    map.getOrDefault(it.key.copy(second = it.key.second - 1), 10) > it.value &&
                    map.getOrDefault(it.key.copy(second = it.key.second + 1), 10) > it.value
        }
        return minimums
    }

    fun part1(input: List<String>): Int {
        return findMins(input).values.sumOf { it + 1 }
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 15)

    val input = readInput("Day09")
    println(part1(input))

    check(part2(testInput) == 1)
    println(part2(input))
}
