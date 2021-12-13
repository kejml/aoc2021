fun main() {
    fun processInput(input: String, folds: String): Pair<Set<Pair<Int, Int>>, List<Fold>> {
        val inputMap = input.split("\r\n").map {
            val (x, y) = it.split(",").map { it.toInt() }
            x to y
        }.toSet()

        val processedFolds = folds.split("\r\n").map { val (axis, number) = it.substringAfterLast(" ").split("=")
            Fold(axis.single(), number.toInt())
        }

        return inputMap to processedFolds
    }

    fun part1(input: String, folds: String): Int {
        val (map, f) = processInput(input, folds)
        return map.fold(f.first()).size

    }

    fun part2(input: String, folds: String): Int {
        val (map, f) = processInput(input, folds)
        var folded = map
        f.forEach {
            folded = folded.fold(it)
        }
        folded.print()
        return folded.size
    }

    // test if implementation meets criteria from the description, like:
    val (testInput, testFolds) = readText("Day13_test").split("\r\n\r\n")
    check(part1(testInput, testFolds) == 17)

    val (input, folds) = readText("Day13").split("\r\n\r\n")
    println(part1(input, folds))

    check(part2(testInput, testFolds) == 16)
    println(part2(input, folds))
}

data class Fold(val axis: Char, val number: Int)

fun Set<Pair<Int, Int>>.fold(fold: Fold): Set<Pair<Int, Int>> {
    return when (fold.axis) {
        'x' -> this.map { if (it.first > fold.number) it.copy(first = 2 * fold.number - it.first) else it }
        'y' -> this.map { if (it.second > fold.number) it.copy(second = 2 * fold.number - it.second) else it }
        else -> throw IllegalArgumentException("Unknown fold '${fold.axis}'")
    }.toSet()
}

fun Set<Pair<Int, Int>>.print() {
    val maxX = this.maxOf { it.first }
    val maxY = this.maxOf { it.second }

    for (i in 0 ..maxY) {
        for (j in 0 ..maxX) {
            if (this.contains(j to i)) print("#") else print(" ")
        }
        println()
    }
}