fun main() {
    fun part1(input: List<Int>): Int {
        return input.size
    }

    fun part2(input: List<Int>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readLineInt("Day07_test")
    check(part1(testInput) == 37)

    val input = readLineInt("Day07")
    println(part1(input))

    check(part2(testInput) == 1)
    println(part2(input))
}
