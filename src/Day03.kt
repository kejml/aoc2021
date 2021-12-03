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

    fun getValue(input: List<String>, criteria: BitCriteria): Int {
        tailrec fun singleValue(input: List<String>, bit: Int, bitCriteria: BitCriteria): ArrayList<String> {
            val ones = ArrayList<String>()
            val zeros = ArrayList<String>()

            input.forEach {
                if (it[bit] == '1') {
                    ones.add(it)
                } else {
                    zeros.add(it)
                }
            }
            val subResult = when (bitCriteria) {
                BitCriteria.OXYGEN -> if (ones.size >= zeros.size) ones else zeros
                BitCriteria.CO2 -> if (ones.size < zeros.size) ones else zeros
            }
            return if (subResult.size <= 1) {
                subResult
            } else {
                singleValue(subResult, bit + 1, bitCriteria)
            }
        }

        return singleValue(input, 0, criteria).first().toInt(2)
    }

    fun part2(input: List<String>): Int {
        val oxygen = getValue(input, BitCriteria.OXYGEN)
        val co2 = getValue(input, BitCriteria.CO2)

        return oxygen * co2
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 198)

    val input = readInput("Day03")
    println(part1(input))

    check(part2(testInput) == 230)
    println(part2(input))
}

enum class BitCriteria {
    OXYGEN,
    CO2,
}
