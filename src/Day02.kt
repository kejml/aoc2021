fun main() {
    fun part1(input: List<Pair<String, Int>>): Int {
        var pos = 0
        var depth = 0
        input.forEach {
            when (it.first) {
                "forward" -> pos += it.second
                "down" -> depth += it.second
                "up" -> depth -= it.second
            }
        }
        return depth * pos
    }

    fun part2(input: List<Pair<String, Int>>): Int {
        var pos = 0
        var depth = 0
        var aim = 0
        input.forEach {
            when (it.first) {
                "forward" -> {
                    pos += it.second
                    depth += aim * it.second
                }
                "down" -> aim += it.second
                "up" -> aim -= it.second
            }
        }
        return depth * pos
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test").mapCommands()
    check(part1(testInput) == 150)

    val input = readInput("Day02").mapCommands()
    println(part1(input))

    check(part2(testInput) == 900)
    println(part2(input))
}

fun List<String>.mapCommands(): List<Pair<String, Int>> = this.map {
    val (command, value) = it.split(" ")
    Pair(command, value.toInt())
}
