fun main() {
    fun part1(input: List<Pair<String, Int>>): Int {
        return input.size
    }

    fun part2(input: List<Pair<String, Int>>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test").mapCommands()
    check(part1(testInput) == 150)

    val input = readInput("Day02").mapCommands()
    println(part1(input))

    check(part1(testInput) == 1)
    println(part2(input))
}

fun List<String>.mapCommands(): List<Pair<String, Int>> = this.map{
    val( command, value) = it.split(" ")
    Pair(command, value.toInt())
}
