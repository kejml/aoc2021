fun main() {
    fun parseCuboids(input: List<String>): List<Cuboid> = input.map {
        val on = it.substringBefore(" ") == "on"
        val coords = it.substringAfter(" ").split(",")
        Cuboid(coords[0].toIntRange(), coords[1].toIntRange(), coords[2].toIntRange(), on)
    }

    fun part1(input: List<String>): Int {
        val boundary = Cuboid(-50..50, -50..50, -50..50)
        val cubes = parseCuboids(input).mapNotNull { it.intersect(boundary) }.fold(setOf<Cube>()) { acc, cuboid -> if (cuboid.on) acc + cuboid.cubes else acc - cuboid.cubes }

        return cubes.intersect(boundary.cubes).size
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day22_test")
    check(part1(testInput) == 39)

    val testInput2 = readInput("Day22_test2")
    check(part1(testInput2) == 590784)

    val input = readInput("Day22")
    println(part1(input))

    check(part2(testInput) == 1)
    println(part2(input))
}

fun String.toIntRange(): IntRange {
    val (from, to) = this.substringAfter("=").split("..").map { it.toInt() }
    return from..to
}

fun IntRange.intersectAsRange(other: IntRange): IntRange? {
    val intersection = this.intersect(other)
    return if (intersection.isEmpty()) {
        null
    } else {
        intersection.first()..intersection.last()
    }
}

data class Cube(val x: Int, val y: Int, val z: Int)

class Cuboid(val x: IntRange, val y: IntRange, val z: IntRange, val on: Boolean = true) {

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

    val cubes
        get(): Set<Cube> {
            val cubes = mutableSetOf<Cube>()
            for (i in x) for (j in y) for (k in z) {
                cubes.add(Cube(i, j, k))
            }
            return cubes
        }
}
