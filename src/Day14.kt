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

    fun part2(input: List<String>): Long {
        val (s, rules) = process(input)

        var twins: MutableMap<String, Long> = HashMap()
        s.windowed(2).forEach { twins[it] = (twins[it] ?: 0) + 1  }

        repeat(40) {
            val newTwins: MutableMap<String, Long> = HashMap()

            twins = twins.map {
                if (rules[it.key] != null) {
                    newTwins[it.key[0] + rules[it.key]!!] = (newTwins[it.key[0] + rules[it.key]!!] ?: 0) + it.value
                    newTwins[rules[it.key]!! + it.key[1]] = (newTwins[rules[it.key]!! + it.key[1]] ?: 0) + it.value
                    it.key to 0L
                } else
                    it.key to it.value
            }.toMap().toMutableMap()

            newTwins.forEach{
                twins.merge(it.key, it.value) {i1, i2 -> i1 + i2}
            }
        }

        val counts = HashMap<Char, Long>()
        twins.forEach { (twin, count) -> counts[twin[0]] = (counts[twin[0]] ?: 0) + count }
        counts[s.last()] = (counts[s.last()] ?: 0) + 1

        return counts.maxOf { it.value } - counts.minOf { it.value }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day14_test")
    check(part1(testInput) == 1588)

    val input = readInput("Day14")
    println(part1(input))

    check(part2(testInput) == 2188189693529)
    println(part2(input))
}
