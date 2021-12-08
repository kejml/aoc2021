fun main() {
    fun part1(input: List<String>): Int {
        return input
            .map { fullLine -> fullLine.split("|")[1].trim() }
            .sumOf { rhs ->
                rhs.split(" ").filter { it.length == 2 || it.length == 3 || it.length == 4 || it.length == 7 }.size
            }

    }

    fun part2(input: List<String>): Int {
        return input.map { line ->
            val (lhs, rhs) = line.split("|").map { it.trim() }
            val mapping = computeMapping(lhs.split(" ").map { it.sort() })
            rhs.split(" ").map { it.sort() }.map { mapping.indexOf(it) }.joinToString("").toInt()
        }.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    check(part1(testInput) == 26)

    val input = readInput("Day08")
    println(part1(input))

    check(part2(testInput) == 61229)
    println(part2(input))
}

fun computeMapping(numbers: List<String>): List<String> {
    val one = numbers.single { it.length == 2 }
    val seven = numbers.single { it.length == 3 }
    val four = numbers.single { it.length == 4 }
    val eight = numbers.single { it.length == 7 }

    val candidatesTwo = numbers.filter { !it.contains(one[0]) }
    val two = if (candidatesTwo.size == 1) candidatesTwo.single() else numbers.single { !it.contains(one[1]) }
    val three = numbers.single { it.length == 5 && it.toList().filter { c -> !two.contains(c) }.size == 1 }
    val five = numbers.single { it.length == 5 && it != two && it != three }

    val nine = numbers.single { it.length == 6 && it.toList().filter { c -> !three.contains(c) }.size == 1 }
    val six = numbers.single { it.length == 6 && it != nine && it.toList().filter { c -> !five.contains(c) }.size == 1 }
    val zero = numbers.single { it.length == 6 && it != six && it != nine }

    return listOf(zero, one, two, three, four, five, six, seven, eight, nine)
}

fun String.sort(): String {
    return this.toList().sorted().joinToString("")
}