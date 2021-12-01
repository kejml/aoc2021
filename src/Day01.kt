fun main() {
    fun part1(input: List<Int>): Int {
        var inc = 0
        input.reduce { acc, i ->
            if (i > acc) {
                inc++
            }
            i
        }
        return inc
    }

    fun part2(input: List<Int>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test").map { it.toInt() }
    check(part1(testInput) == 7)

    val input = readInput("Day01").map { it.toInt() }
    println(part1(input))

    check(part2(testInput) == 5)

    println(part2(input))
}
