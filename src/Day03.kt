fun main() {
    fun part1(input: List<String>): Int {
        val result = MutableList(input.first().length) { 0 }
        input.forEach {
            it.toList().forEachIndexed { index, c -> result[index] += c.digitToInt() }
        }

        val gammaString = result
            .map { k ->
                if (k / input.size.toDouble() > 0.5) '1' else '0'
            }
            .joinToString("")
        val gamma = gammaString.toInt(2)
        val epsilon = gammaString.map { if (it == '1') '0' else '1' }.joinToString("").toInt(2)

        return gamma * epsilon
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 198)

    val input = readInput("Day03")
    println(part1(input))

    check(part2(testInput) == 1)
    println(part2(input))
}
