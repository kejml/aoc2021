fun main() {
    fun part1(input: List<String>): Int {
        return input
            .map { fullLine -> fullLine.split("|")[1].trim() }
            .sumOf { rhs ->
                rhs.split(" ").filter { it.length == 2 || it.length == 3 || it.length == 4 || it.length == 7 }.size
            }

    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    check(part1(testInput) == 26)

    val input = readInput("Day08")
    println(part1(input))

    check(part2(testInput) == 61229)
    println(part2(input))
}
