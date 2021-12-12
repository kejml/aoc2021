fun main() {
    fun part1(input: List<String>): Int {
        return input.size
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    check(part1(testInput) == 10)
    val testInput2 = readInput("Day12_test2")
    check(part1(testInput2) == 19)
    val testInput3 = readInput("Day12_test3")
    check(part1(testInput3) == 226)


    val input = readInput("Day12")
    println(part1(input))

    check(part2(testInput) == 1)
    println(part2(input))
}
