fun main() {
    fun numberOfPaths(input: List<String>, maxCaveRepeats: Int = 1): Int {
        val connections = input.map { it.split("-").toSet() }

        return Cave("start").calculateNextCaves(connections, maxCaveRepeats).getEnds()
    }

    fun part1(input: List<String>): Int {
        return numberOfPaths(input)
    }

    fun part2(input: List<String>): Int {
        return numberOfPaths(input, 2)
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

class Cave(private val name:String, private val parent: Cave? = null) {
    private var nextCaves: List<Cave> = ArrayList()

    fun getEnds(): Int {
        var ends = 0
        nextCaves.forEach {
            if (it.name == "end") ends++ else ends += it.getEnds()
        }
        return ends
    }

    private fun pathContains(cave: String, maxCaveRepeats: Int = 1): Boolean {
        if (cave == "start") return true
        var found = 0
        var node: Cave? = this
        while (node != null) {
            if (found == 1 && pathHasLowerCaseDuplicate())
                return true
            if (node.name == cave && ++found == maxCaveRepeats) return true
            node = node.parent
        }
        return false
    }

    fun calculateNextCaves(connections: List<Set<String>>, maxCaveRepeats: Int = 1): Cave {
        val nextCaves = connections
            .filter { it.contains(this.name) }
            .map { it.other(this.name) }
            .filter { if (it.isLowerCase()) !this.pathContains(it, maxCaveRepeats) else true }
            .map { Cave(it, this) }
        this.nextCaves = nextCaves
        this.nextCaves.filter { it.name != "end" }.forEach { it.calculateNextCaves(connections, maxCaveRepeats) }
        return this
    }

    private fun pathFromRoot(): MutableList<String> {
        val path = mutableListOf<String>()
        var node: Cave? = this
        while (node != null) {
            path.add(node.name)
            node = node.parent
        }
        return path
    }

    private fun pathHasLowerCaseDuplicate(): Boolean =
        pathFromRoot().filter { it.isLowerCase() }.groupBy { it }.filterValues { it.size > 1 }.isNotEmpty()

    override fun toString() = "PathNode(cave='$name')"
}
