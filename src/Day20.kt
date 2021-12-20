fun main() {
    fun enhanceImage(input: List<String>, times: Int): Int {
        val algorithm = input[0]
        val image = input.drop(2).mapIndexed { rowNum, line -> line.mapIndexed { colNum, c -> Pair(rowNum, colNum) to c }.toMap() }
            .fold(HashMap<Pair<Int, Int>, Char>()) { acc, map ->
                acc.putAll(map)
                acc
            }
        var newImage = image
        var outside = '.'
        repeat(times) {
            val (newImage2, outside2) = newImage.enhance(algorithm, outside)
            newImage = newImage2
            outside = outside2
        }
        return newImage.values.filter { it == '#' }.size
    }

    fun part1(input: List<String>): Int {
        return enhanceImage(input, 2)
    }

    fun part2(input: List<String>): Int {
        return enhanceImage(input, 50)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day20_test")
    check(part1(testInput) == 35)

    val input = readInput("Day20")
    println(part1(input))

    check(part2(testInput) == 3351)
    println(part2(input))
}

operator fun Map<Pair<Int, Int>, Char>.get(x: Int, y: Int, default: Char): Char {
    return this.getOrDefault(Pair(x, y), default)
}

fun Map<Pair<Int, Int>, Char>.enhance(algorithm: String, outside: Char): Pair<HashMap<Pair<Int, Int>, Char>, Char> {
    val minX = this.keys.minOf { it.first } - 1
    val maxX = this.keys.maxOf { it.first } + 1
    val minY = this.keys.minOf { it.second } - 1
    val maxY = this.keys.maxOf { it.second } + 1

    val newImage = HashMap<Pair<Int,Int>, Char>()
    for (i in minX..maxX) {
        for (j in minY..maxY) {
            val algPosition = listOf(
                this[i - 1, j - 1, outside],
                this[i - 1, j, outside],
                this[i - 1, j + 1, outside],
                this[i, j - 1, outside],
                this[i, j, outside],
                this[i, j + 1, outside],
                this[i + 1, j - 1, outside],
                this[i + 1, j, outside],
                this[i + 1, j + 1, outside],
            ).map { if (it == '#') '1' else '0' }.joinToString("").toInt(2)
            newImage[Pair(i, j)] = algorithm[algPosition]
        }
    }

    return newImage to (if (outside == '.') algorithm[0] else algorithm[511])

}
