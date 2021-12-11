fun main() {
    fun step(map: List<MutableList<Int>>): StepResult {
        var mapAfterStep = map
        var flashes = 0
        mapAfterStep = mapAfterStep.map { it.map { i -> (i + 1).coerceAtMost(10) } }.map { it.toMutableList() }
        mapAfterStep.forEachIndexed { row, ints ->
            ints.forEachIndexed { column, i ->
                if (i == 10) {
                    mapAfterStep.increaseNeighbours(
                        row,
                        column
                    )
                }
            }
        }

        mapAfterStep.forEachIndexed { row, ints ->
            ints.forEachIndexed { column, i ->
                if (i >= 10) {
                    flashes++
                    mapAfterStep[row][column] = 0
                }
            }
        }
        return StepResult(flashes, mapAfterStep)
    }

    fun part1(input: List<List<Int>>): Int {
        var flashes = 0
        var stepResult = StepResult(flashes, input.map { it.toMutableList() })
        repeat(100) {
            stepResult = step(stepResult.map)
            flashes += stepResult.flashes
        }

        return flashes

    }

    fun part2(input: List<List<Int>>): Int {
        var stepResult = StepResult(0, input.map { it.toMutableList() })

        var steps = 0
        while (!stepResult.map.shining()) {
            stepResult = step(stepResult.map)
            steps++
        }
        return steps
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readDigitMatrix("Day11_test")
    check(part1(testInput) == 1656)

    val input = readDigitMatrix("Day11")
    println(part1(input))

    check(part2(testInput) == 195)
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

fun List<List<Int>>.shining(): Boolean = this.flatten().filter { it == 0 }.size == this.flatten().size

fun List<List<Int>>.print() {
    this.forEach {
        it.forEach { print(" $it " ) }
        println()
    }
}

data class StepResult(val flashes: Int , val map: List<MutableList<Int>>)