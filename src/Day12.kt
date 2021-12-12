fun main() {
    fun part1(input: List<String>): Int {
        val connections = input.map { it.split("-").toSet() }

        val start = PathNode("start")
        start.nextCaves = connections.filter { it.contains("start") }.map { PathNode(it.other("start"), start) }
        start.nextCaves.forEach { it.nextNodes(connections) }

        return start.getEnds()
    }

    fun part2(input: List<String>): Int {
        val connections = input.map { it.split("-").toSet() }

        val start = PathNode("start")
        start.nextCaves = connections.filter { it.contains("start") }.map { PathNode(it.other("start"), start) }
        start.nextCaves.forEach { it.nextNodes2(connections) }

        return start.getEnds()
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

    check(part2(testInput) == 36)
    check(part2(testInput2) == 103)
    check(part2(testInput3) == 3509)
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
    private fun pathContains2(cave: String): Boolean {
        if (cave == "start") return true
        var found = 0
        var node: PathNode? = this
        while (node != null) {
            if (found == 1 && pathHasLowerCaseDuplicate())
                return true
            if (node.cave == cave && ++found == 2) return true
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

    fun nextNodes2(connections: List<Set<String>>) {
        val nextCaves = connections
            .filter { connection -> connection.contains(this.cave) }
            .map { connection -> connection.other(this.cave) }
            .filter { potentialCave -> if (potentialCave.isLowerCase()) !this.pathContains2(potentialCave) else true }
            .map { PathNode(it, this) }
        this.nextCaves = nextCaves
        this.nextCaves.filter { it.cave != "end" }.forEach { it.nextNodes2(connections) }
    }

    private fun pathFromRoot(): MutableList<String> {
        val path = mutableListOf<String>()
        var node: PathNode? = this
        while (node != null) {
            path.add(node.cave)
            node = node.parent
        }
        return path
    }

    private fun pathHasLowerCaseDuplicate(): Boolean =
        pathFromRoot().filter { it.isLowerCase() }.groupBy { it }.filterValues { it.size > 1 }.isNotEmpty()

    fun pathFromRootAsString(): String = pathFromRoot().reversed().joinToString(",")

    override fun toString() = "PathNode(cave='$cave')"
}
