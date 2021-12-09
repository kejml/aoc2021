fun main() {
    fun createMap(input: List<String>): Map<Pair<Int, Int>, Int> {
        return input.mapIndexed { rowIndex, row -> row.mapIndexed { columnIndex, c -> Pair(rowIndex, columnIndex) to c.digitToInt() }.toMap() }
            .flatMap { it.asSequence() }
            .groupBy({ it.key }, { it.value })
            .mapValues { it.value.single() }
    }

    fun findMins(map: Map<Pair<Int, Int>, Int>): Map<Pair<Int, Int>, Int> {
        val minimums = map.filter {
            map.getOrDefault(it.key.copy(first = it.key.first - 1), 10) > it.value &&
                    map.getOrDefault(it.key.copy(first = it.key.first + 1), 10) > it.value &&
                    map.getOrDefault(it.key.copy(second = it.key.second - 1), 10) > it.value &&
                    map.getOrDefault(it.key.copy(second = it.key.second + 1), 10) > it.value
        }
        return minimums
    }

    fun part1(input: List<String>): Int {
        return findMins(createMap(input)).values.sumOf { it + 1 }
    }

    fun MutableMap<Pair<Int, Int>, Int>.addNeighboursToBasin(
        startingPoint: Pair<Int, Int>,
        map: Map<Pair<Int, Int>, Int>
    ) {
        startingPoint.neighbours().forEach { newPoint ->
            if (map[newPoint] != null && map[newPoint] != 9 && !contains(newPoint)) {
                this[newPoint] = map[newPoint]!!
                this@addNeighboursToBasin.addNeighboursToBasin(newPoint, map)
            }
        }
    }

    fun part2(input: List<String>): Int {
        val map = createMap(input)
        val basins = findMins(map).map {
            mutableMapOf(it.key to it.value)
        }
        basins.forEach { basin ->
            val startingPoint = basin.keys.single()
            basin.addNeighboursToBasin(startingPoint, map)
        }
        return basins.map { it.size }.sortedDescending().subList(0, 3).reduce { acc, i -> acc * i }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 15)

    val input = readInput("Day09")
    println(part1(input))

    check(part2(testInput) == 1134)
    println(part2(input))
}

fun MutableMap<Pair<Int, Int>, Int>.addNeighbours(map: Map<Pair<Int, Int>, Int>, newItem: Pair<Int, Int>) {
    newItem.neighbours().forEach {
        if (map[it] != null && map[it] != 9 && !this.contains(it)) {
            this[it] = map[it]!!
        }
    }
}

fun Pair<Int, Int>.neighbours(): List<Pair<Int, Int>> = listOf(
    this.copy(first = this.first + 1),
    this.copy(first = this.first - 1),
    this.copy(second = this.second + 1),
    this.copy(second = this.second - 1),
)
