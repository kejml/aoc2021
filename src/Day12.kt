fun main() {
    fun part1(input: List<String>): Int {
        val connections = input.map { it.split("-").toSet() }

        val start = PathNode("start")
        start.nextCaves = connections.filter { it.contains("start") }.map { PathNode(it.other("start"), start) }
        start.nextCaves.forEach { it.nextNodes(connections) }

        return start.getEnds()
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    check(part1(testInput) == 10)
    val testInput2 = readInput("Day12_test2")
    check(part1(testInput2) == 19)
    val testInput3 = readInput("Day12_test3")
    check(part1(testInput3) == 226)


    val input = readInput("Day12")
    println(part1(input))

    check(part2(testInput) == 1)
    println(part2(input))
}

fun Set<String>.other(e: String): String {
    return this.single { it != e }
}

fun String.isLowerCase(): Boolean = this.lowercase() == this

class PathNode(private val cave:String, private val parent: PathNode? = null, var nextCaves: List<PathNode> = ArrayList()) {
    fun getEnds(): Int {
        var ends = 0
        nextCaves.forEach {
            if (it.cave == "end") ends++ else ends += it.getEnds()
        }
        return ends
    }

    private fun pathContains(cave: String): Boolean {
        var node: PathNode? = this
        while (node != null) {
            if (node.cave == cave) return true
            node = node.parent
        }
        return false
    }

    fun nextNodes(connections: List<Set<String>>) {
        val nextCaves = connections
            .filter { it.contains(this.cave) }
            .map { it.other(this.cave) }
            .filter { if (it.isLowerCase()) !this.pathContains(it) else true }
            .map { PathNode(it, this) }
        this.nextCaves = nextCaves
        this.nextCaves.filter { it.cave != "end" }.forEach { it.nextNodes(connections) }
    }

    override fun toString() = "PathNode(cave='$cave')"
}
