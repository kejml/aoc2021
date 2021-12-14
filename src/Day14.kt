fun main() {
    fun process(input: List<String>): Pair<String, Map<String, String>> {
        val i = input[0]

        val rules = input.subList(2, input.size).map { it.split(" -> ") }.associate { it[0] to it[1] }
        return i to rules
    }

    fun part1(input: List<String>): Int {
        val (s, rules) = process(input)

        var polymer = s
        repeat(10) {
            polymer = polymer.windowed(2).joinToString("-") { if (rules[it] != null) it[0] + rules[it]!! + it[1] else it }.replace(Regex("-."), "")
        }
        val eachCount = polymer.groupingBy { it }.eachCount()
        return eachCount.maxOf { it.value } - eachCount.minOf { it.value }
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day14_test")
    check(part1(testInput) == 1588)

    val input = readInput("Day14")
    println(part1(input))

    check(part2(testInput) == 1)
    println(part2(input))
}
