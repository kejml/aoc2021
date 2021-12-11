fun main() {
    fun part1(input: List<List<Int>>): Int {
        var result = 0
        var flashed = input.map { it.toMutableList() }
        repeat(100) {
            flashed = flashed.map { it.map { i -> (i + 1).coerceAtMost(10) } }.map { it.toMutableList() }
            flashed.forEachIndexed { row, ints ->
                ints.forEachIndexed { column, i ->
                    if (i == 10) {
                        flashed.increaseNeighbours(
                            row,
                            column
                        )
                    }
                }
            }

            flashed.forEachIndexed { row, ints ->
                ints.forEachIndexed { column, i ->
                    if (i >= 10) {
                        result++
                        flashed[row][column] = 0
                    }
                }
            }
        }

        return result

    }

    fun part2(input: List<List<Int>>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readDigitMatrix("Day11_test")
    check(part1(testInput) == 1656)

    val input = readDigitMatrix("Day11")
    println(part1(input))

    check(part2(testInput) == 1)
    println(part2(input))
}


fun Pair<Int, Int>.neighbours(): List<Pair<Int, Int>> {
    return listOf(
        this.first - 1 to this.second,
        this.first - 1 to this.second - 1,
        this.first - 1 to this.second + 1,
        this.first to this.second - 1,
        this.first to this.second + 1,
        this.first + 1 to this.second - 1,
        this.first + 1 to this.second,
        this.first + 1 to this.second + 1,
    ).filter { it.first in 0..9 && it.second in 0..9 }
}

fun List<MutableList<Int>>.increaseNeighbours(row: Int, column: Int) {
    Pair(row, column).neighbours().filter { this[it.first][it.second] < 10 }.forEach {
        if (this[it.first][it.second] < 10) {
            this[it.first][it.second]++
            if (this[it.first][it.second] == 10) {
                this.increaseNeighbours(it.first, it.second)
                this[it.first][it.second]++ // To avoid multiple expansion by the main loop
            }
        }
    }
}

fun List<List<Int>>.print() {
    this.forEach {
        it.forEach { print(" $it " ) }
        println()
    }
}