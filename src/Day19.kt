import java.lang.Exception

fun main() {
    fun part1(input: String): Int {
        val scanners = input.split("\r\n\r\n").map { line -> line.split("\r\n") }.map { scanner ->
            val scannerId = scanner[0].split(" ")[2].toInt()
            val beacons = scanner.drop(1).map { beacon -> beacon.split(",").map { it.toInt() } }
                .map { Beacon(it[0], it[1], it[2]) }
            Scanner(beacons, scannerId)
        }.toMutableList()
        var completeMap = scanners.removeAt(0)
        while (scanners.isNotEmpty()) {
            val iterator = scanners.iterator()
            while (iterator.hasNext()) {
                try {
                    val candidate = iterator.next()
                    completeMap += completeMap.maxOverlaps(candidate)
                    iterator.remove()
                } catch (ex: Exception) {
                    println("Didn't work, trying a different one, current map size is ${completeMap.beacons.size}, scanners left ${scanners.size}")
                }
            }
        }

        return completeMap.beacons.size
    }

    fun part2(input: String): Int {
        return input.length
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readText("Day19_test")
    check(part1(testInput) == 79)

    val input = readText("Day19")
    println(part1(input))

    check(part2(testInput) == 1)
    println(part2(input))
}

val rX = arrayOf(
    intArrayOf(1, 0, 0),
    intArrayOf(0, 0, -1),
    intArrayOf(0, 1, 0),
)

val rY = arrayOf(
    intArrayOf(0, 0, 1),
    intArrayOf(0, 1, 0),
    intArrayOf(-1, 0, 0),
)

val rZ = arrayOf(
    intArrayOf(0, -1, 0),
    intArrayOf(1, 0, 0),
    intArrayOf(0, 0, 1),
)

data class Beacon(val x: Int, val y: Int, val z: Int) {
    private fun rotate(m: Array<IntArray>): Beacon {
        val (x2, y2, z2) = m.map { it[0] * x + it[1] * y + it[2] * z }
        return Beacon(x2, y2, z2)
    }

    operator fun minus(other: Beacon) = Beacon(other.x - x, other.y - y, other.z - z)
    operator fun plus(other: Beacon) = Beacon(other.x + x, other.y + y, other.z + z)

    fun rotations(): List<Beacon> {
        return listOf(
            Beacon(x, y, z),
            Beacon(x, y, z).rotate(rZ),
            Beacon(x, y, z).rotate(rZ).rotate(rZ),
            Beacon(x, y, z).rotate(rZ).rotate(rZ).rotate(rZ),
            Beacon(x, y, z).rotate(rY),
            Beacon(x, y, z).rotate(rY).rotate(rX),
            Beacon(x, y, z).rotate(rY).rotate(rX).rotate(rX),
            Beacon(x, y, z).rotate(rY).rotate(rX).rotate(rX).rotate(rX),
            Beacon(x, y, z).rotate(rY).rotate(rZ),
            Beacon(x, y, z).rotate(rY).rotate(rZ).rotate(rY),
            Beacon(x, y, z).rotate(rY).rotate(rZ).rotate(rY).rotate(rY),
            Beacon(x, y, z).rotate(rY).rotate(rZ).rotate(rY).rotate(rY).rotate(rY),
            Beacon(x, y, z).rotate(rY).rotate(rZ).rotate(rZ),
            Beacon(x, y, z).rotate(rY).rotate(rZ).rotate(rZ).rotate(rX),
            Beacon(x, y, z).rotate(rY).rotate(rZ).rotate(rZ).rotate(rX).rotate(rX),
            Beacon(x, y, z).rotate(rY).rotate(rZ).rotate(rZ).rotate(rX).rotate(rX).rotate(rX),
            Beacon(x, y, z).rotate(rY).rotate(rZ).rotate(rZ).rotate(rZ),
            Beacon(x, y, z).rotate(rY).rotate(rZ).rotate(rZ).rotate(rZ).rotate(rY),
            Beacon(x, y, z).rotate(rY).rotate(rZ).rotate(rZ).rotate(rZ).rotate(rY).rotate(rY),
            Beacon(x, y, z).rotate(rY).rotate(rZ).rotate(rZ).rotate(rZ).rotate(rY).rotate(rY).rotate(rY),
            Beacon(x, y, z).rotate(rY).rotate(rY),
            Beacon(x, y, z).rotate(rY).rotate(rY).rotate(rZ),
            Beacon(x, y, z).rotate(rY).rotate(rY).rotate(rZ).rotate(rZ),
            Beacon(x, y, z).rotate(rY).rotate(rY).rotate(rZ).rotate(rZ).rotate(rZ),
        )
    }
}

data class Scanner(val beacons: List<Beacon>, val scannerId: Int? = null) {
    operator fun plus(other: Scanner): Scanner = Scanner(beacons.union(other.beacons).toList())

    private infix fun overlap(other: Scanner) = beacons.intersect(other.beacons.toSet()).size
    private fun rotations(): List<Scanner> = this.beacons.map { it.rotations() }.transpose().map { Scanner(it) }

    private fun translate(by: Beacon): Scanner {
        return Scanner(beacons.map { it + by })
    }

    fun maxOverlaps(other: Scanner): Scanner {
        this.beacons.forEach { b1 ->
            other.rotations().forEach { r ->
                r.beacons.forEach { b2 ->
                    val diff = b2 - b1
                    val candidate = r.translate(diff)
                    if (this overlap candidate >= 12) {
                        return candidate
                    }
                }
            }
        }
        throw IllegalStateException("No overlap found!")
    }
}

fun <E> List<List<E>>.transpose(): List<List<E>> {

    val rowSize = this[0].size
    val rowIndices = 0 until rowSize

    return rowIndices.map { rowIndex ->
        indices.map { columnIndex ->
            this[columnIndex][rowIndex]
        }
    }
}
