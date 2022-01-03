import kotlin.math.max
import kotlin.math.min

fun main() {
    fun parseCuboids(input: List<String>): List<Cuboid> = input.map {
        val on = it.substringBefore(" ") == "on"
        val coords = it.substringAfter(" ").split(",")
        Cuboid(coords[0].toIntRange(), coords[1].toIntRange(), coords[2].toIntRange(), on)
    }

    fun part1(input: List<String>): Int {
        val boundary = Cuboid(-50..50, -50..50, -50..50)
        val cubes = parseCuboids(input).mapNotNull { it.intersect(boundary) }
            .fold(setOf<Cube>()) { acc, cuboid -> if (cuboid.on) acc + cuboid.cubes else acc - cuboid.cubes }

        return cubes.intersect(boundary.cubes).size
    }

    fun part2(input: List<String>): Long {
        val cuboidGroup = CuboidGroup()
        parseCuboids(input).forEach { cuboidGroup.add(it) }
        return cuboidGroup.count()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day22_test")
    check(part1(testInput) == 39)

    val testInput2 = readInput("Day22_test2")
    check(part1(testInput2) == 590784)

    val input = readInput("Day22")
    println(part1(input))

    val testInput3 = readInput("Day22_test3")
    check(part1(testInput3) == 474140)

    check(part2(testInput3) == 2758514936282235)
    println(part2(input))
}

fun String.toIntRange(): IntRange {
    val (from, to) = this.substringAfter("=").split("..").map { it.toInt() }
    return from..to
}

fun IntRange.intersectAsRange(other: IntRange): IntRange? {
    val first = max(this.first, other.first)
    val last = min(this.last, other.last)
    return if (first <= last) {
        first..last
    } else {
        null
    }
}

data class Cube(val x: Int, val y: Int, val z: Int)

data class Cuboid(val x: IntRange, val y: IntRange, val z: IntRange, val on: Boolean = true) {

    private fun coordinate(c: Char): IntRange {
        return when (c) {
            'x' -> x
            'y' -> y
            'z' -> z
            else -> throw IllegalArgumentException("Unknown coordinate '$c'")
        }
    }

    private val offs = mutableListOf<Cuboid>()

    private fun List<Cuboid>.getSortedCoordinates(c: Char): List<Coordinate> =
        this.map { listOf(Coordinate(it.coordinate(c).first, true, it), Coordinate(it.coordinate(c).last + 1, false, it)) }.flatten().sortedBy { it.value }

    fun contains(x: Int, y: Int, z: Int): Boolean =
        x >= this.x.first && x <= this.x.last && y >= this.y.first && y <= this.y.last && z >= this.z.first && z <= this.z.last

    fun intersect(other: Cuboid): Cuboid? {
        val newX = x.intersectAsRange(other.x)
        val newY = y.intersectAsRange(other.y)
        val newZ = z.intersectAsRange(other.z)
        return if (newX == null || newY == null || newZ == null) {
            null
        } else {
            Cuboid(newX, newY, newZ, on)
        }
    }

    fun addOff(cuboid: Cuboid?) {
        if (cuboid != null) {
            offs.add(cuboid)
        }
    }

    override fun toString(): String {
        return "Cuboid(x=$x, y=$y, z=$z)"
    }

    data class Coordinate(val value: Int, val start: Boolean, val cuboid: Cuboid)

    private fun planeSweepOffs(): Long {
        val zeds = offs.getSortedCoordinates('z')
        val activeCuboids = mutableListOf<Cuboid>()
        var lastZ = Int.MIN_VALUE
        var size = 0L
        for (z in zeds) {
            var area = 0L
            if (z.value > lastZ) {
                area = lineSweep(activeCuboids)
            }
            size += (z.value - lastZ) * area
            if (z.start) activeCuboids.add(z.cuboid) else activeCuboids.remove(z.cuboid)
            lastZ = z.value
        }

        return size
    }

    private fun lineSweep(activeByZ: List<Cuboid> = offs): Long {
        val xs = activeByZ.getSortedCoordinates('x')
        var lastX = Int.MIN_VALUE
        val activeCuboids = mutableListOf<Cuboid>()

        var sizeX = 0L

        for (x in xs) {
            var sizeY = 0L
            if (x.value > lastX) {
                val ys = activeCuboids.getSortedCoordinates('y')
                var activeYs = 0
                var lastStartY = Int.MIN_VALUE

                for (y in ys) {
                    if (activeYs == 0) {
                        lastStartY = y.value
                    }
                    if (y.start) activeYs++
                    else activeYs--

                    if (activeYs == 0) {
                        sizeY += (y.value - lastStartY)
                    }
                }
            }
            sizeX += (x.value - lastX) * sizeY
            if (x.start) activeCuboids.add(x.cuboid)
            else activeCuboids.remove(x.cuboid)
            lastX = x.value
        }
        return sizeX
    }

    val activeSize
        get() = (x.last - x.first + 1).toLong() * (y.last - y.first + 1).toLong() * (z.last - z.first + 1).toLong() - planeSweepOffs()

    val cubes
        get(): Set<Cube> {
            val cubes = mutableSetOf<Cube>()
            for (i in x) for (j in y) for (k in z) {
                cubes.add(Cube(i, j, k))
            }
            return cubes
        }
}

class CuboidGroup(private val cuboids: MutableList<Cuboid> = mutableListOf()) {
    fun add(c: Cuboid): CuboidGroup {
        cuboids.forEach { it.addOff(it.intersect(c)) }
        if (c.on) {
            cuboids.add(c)
        }
        return this
    }

    fun count(): Long {
        return cuboids.fold(0L) { acc, cuboid -> acc + cuboid.activeSize }
    }
}
