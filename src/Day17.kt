fun main() {

    fun parseInput(input: String): Target {
        val (x, y) = input
            .substringAfter(":")
            .split(",")
            .map { it.trim() }
            .map { it.substringAfter("=") }
            .map { it.split("..") }
            .map { it[0].toInt() to it[1].toInt() }
        return Target(x, y)
    }

    fun part1(input: String): Int {
        val target = parseInput(input)
        var maxHeight = 0

        for (i in 0..target.x.second) {
            for (j in target.y.first..-target.y.first) {
                maxHeight = Probe(i, j).highestPoint(target)?.coerceAtLeast(maxHeight) ?: maxHeight
            }
        }
        return maxHeight
    }

    fun part2(input: String): Int {
        val target = parseInput(input)
        var hits = 0

        for (i in 0..target.x.second) {
            for (j in target.y.first..-target.y.first) {
                if (Probe(i, j).highestPoint(target) != null) hits++
            }
        }
        return hits
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readText("Day17_test")
    check(part1(testInput) == 45)

    val input = readText("Day17")
    println(part1(input))

    check(part2(testInput) == 112)
    println(part2(input))
}

data class Target(val x: Pair<Int, Int>, val y: Pair<Int, Int>) {
    fun inTarget(p: Pair<Int, Int>): Boolean = inTargetX(p) && inTargetY(p)
    fun inTargetX(p: Pair<Int, Int>): Boolean = p.first in x.first..x.second
    fun inTargetY(p: Pair<Int, Int>): Boolean = p.second in y.first..y.second
}

class Probe(private val startVx: Int, private val startVy: Int) {

    fun highestPoint(target: Target): Int? {
        var x = 0
        var y = 0
        var dx = startVx
        var dy = startVy

        fun step() {
            x += dx
            y += dy
            if (dx > 0) dx--
            if (dx < 0) dx++
            dy--
        }

        fun currentPosition() = x to y

        var maxHeight = 0
        while (true) {
            maxHeight = maxHeight.coerceAtLeast(y)
            if (target.inTarget(currentPosition())) return maxHeight
            if (dx == 0 && !target.inTargetX(currentPosition())) return null
            if (y < target.y.first) return null

            step()
        }
    }
}
