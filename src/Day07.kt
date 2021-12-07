import kotlin.math.abs

fun main() {
    fun common(input: List<Int>, cost: (Int, Int) -> Int): Int {
        val min = input.minOf { it }
        val max = input.maxOf { it }

        var result = Int.MAX_VALUE

        for (i in min..max) {
            result = result.coerceAtMost(input.sumOf { cost(it, i)})
        }
        return result
    }

    fun part1(input: List<Int>): Int {
        return common(input) { value, target -> abs(value - target) }
    }

    fun part2(input: List<Int>): Int {
        return common(input) { value, target -> (1..abs(value - target)).sum() }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readLineInt("Day07_test")
    check(part1(testInput) == 37)

    val input = readLineInt("Day07")
    println(part1(input))

    check(part2(testInput) == 168)
    println(part2(input))
}
