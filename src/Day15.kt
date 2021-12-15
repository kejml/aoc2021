fun main() {
    fun Pair<Int, Int>.neighbours(): List<Pair<Int, Int>> {
        return listOf(
            this.first - 1 to this.second,
            this.first to this.second - 1,
            this.first to this.second + 1,
            this.first + 1 to this.second,
        )
    }

    fun dijkstra(map: Map<Pair<Int, Int>, Node>): Map<Pair<Int, Int>, Node> {
        val processed: MutableMap<Pair<Int, Int>, Node> = HashMap()
        val candidates = map.filter { it.value.shortestPath == 0 }.toMutableMap()

        while (candidates.isNotEmpty()) {
            val nextNode = candidates.minByOrNull { it.value.shortestPath }!!
            nextNode.key.neighbours().filter { !processed.containsKey(it) }.forEach {
                val n = map[it]
                if (n != null) {
                    if (n.shortestPath > nextNode.value.shortestPath + n.price) {
                        n.shortestPath = nextNode.value.shortestPath + n.price
                        n.prevNode = it
                        candidates[it] = n
                    }
                }
            }
            processed[nextNode.key] = nextNode.value
            candidates.remove(nextNode.key)
        }
        return processed
    }

    fun part1(input: List<String>): Int {
        val map = input.mapIndexed { row, s -> s.toList().mapIndexed { column, c -> (row to column) to Node(c.digitToInt()) } }.flatten().toMap()
        map[Pair(0,0)]!!.shortestPath = 0

        val processed = dijkstra(map)

        return processed[Pair(input.size - 1, input.size - 1)]!!.shortestPath

    }

    fun Int.incWrapped(n: Int): Int = if (this + n > 9) this + n - 9 else this + n


    fun part2(input: List<String>): Int {
        val map: MutableMap<Pair<Int, Int>, Node> = HashMap()
        input.forEachIndexed { row, s ->
            s.toList().forEachIndexed { column, c ->
                for (i in 0..4) {
                    for (j in 0..4) {
                        map[Pair(row + i * input.size, column + j * input.size)] = Node(c.digitToInt().incWrapped(i + j))
                    }
                }

            }
        }
        map[Pair(0, 0)]!!.shortestPath = 0

        val processed = dijkstra(map)

        return processed[Pair(input.size * 5 - 1, input.size * 5 - 1)]!!.shortestPath
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day15_test")
    check(part1(testInput) == 40)

    val testInput2 = readInput("Day15_test2")
    check(part1(testInput2) == 14)

    val input = readInput("Day15")
    println(part1(input))

    check(part2(testInput) == 315)
    println(part2(input))
}

data class Node(
    val price: Int,
    var shortestPath: Int = Int.MAX_VALUE,
    var prevNode: Pair<Int, Int>? = null
)
