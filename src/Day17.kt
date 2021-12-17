fun main() {
    fun part1(input: List<String>): Int {
        return input.size
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day17_test")
    check(part1(testInput) == 45)

    val input = readInput("Day17")
    println(part1(input))

    check(part2(testInput) == 1)
    println(part2(input))
}
