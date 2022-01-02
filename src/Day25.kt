import java.util.HashMap

typealias CucumberMap = MutableMap<Coordinates, Cucumber>

fun main() {

    fun CucumberMap.move(cucumber: Cucumber): CucumberMap? {
        var moved = false
        val newMap = map {
            if (it.value == cucumber) {
                if (this[cucumber.neighbour(it.key)] == null) {
                    moved = true
                    cucumber.neighbour(it.key) to cucumber
                } else {
                    it.key to it.value
                }
            } else {
                it.key to it.value
            }
        }.toMap() as CucumberMap
        return if (moved) newMap else null
    }

    fun part1(input: List<String>): Int {

        var map: CucumberMap = HashMap<Coordinates, Cucumber>()
        val height = input.size
        val width = input[0].length

        fun CucumberMap.print() {
            for (i in 0 until height) {
                for (j in 0 until width) {
                    when (this[Coordinates(i, j)]) {
                        Cucumber.E -> print(">")
                        Cucumber.S -> print("v")
                        else -> print(".")
                    }
                }
                println()
            }
        }

        input.forEachIndexed { row, s ->
            s.forEachIndexed { column, c ->
                when (c) {
                    '>' -> map[Coordinates(row, column, height, width)] = Cucumber.E
                    'v' -> map[Coordinates(row, column, height, width)] = Cucumber.S
                }
            }
        }

        var steps = 0
        while (true) {
            //println("*** step $steps ***")
            //map.print()
            val movedEast = map.move(Cucumber.E)
            map = movedEast ?: map
            val movedSouth = map.move(Cucumber.S)
            map = movedSouth ?: map
            steps++
            if (movedEast == null && movedSouth == null) break
            //println()
        }

        return steps
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day25_test")
    check(part1(testInput) == 58)

    val input = readInput("Day25")
    println(part1(input))

    check(part2(testInput) == 1)
    println(part2(input))
}

data class Coordinates(val x: Int, val y: Int, val maxX: Int = 0, val maxY: Int = 0) {
    fun south(): Coordinates = if (x + 1 == maxX) {
        this.copy(x = 0)
    } else {
        this.copy(x = x + 1)
    }

    fun east(): Coordinates = if (y + 1 == maxY) {
        this.copy(y = 0)
    } else {
        this.copy(y = y + 1)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Coordinates

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }
}

enum class Cucumber(val neighbour: (Coordinates) -> Coordinates) {
    S(Coordinates::south),
    E(Coordinates::east),
}