import java.util.HashMap

fun main() {
    fun HashMap<Pair<Int, Int>, Int>.increment(
        x: Int,
        y: Int
    ) {
        val coordinates = Pair(x, y)
        computeIfPresent(coordinates) { _, v -> v + 1 }
        putIfAbsent(coordinates, 1)
    }

    fun HashMap<Pair<Int, Int>, Int>.draw(): String {
        var result = ""
        for (i in 0..this.keys.maxOf { it.second }) {
            for (j in 0 .. this.keys.maxOf { it.first }) {
                result += this[Pair(j,i)] ?: "."
            }
            result += "\n"
        }
        return result
    }


    fun part1(input: List<String>): Int {
        val map = HashMap<Pair<Int, Int>, Int>()
        input
            .map { it.split(" -> ") }
            .map {
                Pair(
                    it[0].split(",").map { it.toInt() },
                    it[1].split(",").map { it.toInt() }
                )
            }
            .forEach {
                if (it.first[0] == it.second[0]) {
                    if (it.first[1]<= it.second[1]) {
                        for (i in it.first[1]..it.second[1]) {
                            map.increment(it.first[0], i)
                        }
                    } else {
                        for (i in it.second[1]..it.first[1]) {
                            map.increment(it.first[0], i)
                        }
                    }
                } else if (it.first[1] == it.second[1]) {
                    if (it.first[0] <= it.second[0]) {
                        for (i in it.first[0]..it.second[0]) {
                            map.increment(i, it.first[1])
                        }
                    } else {
                        for (i in it.second[0]..it.first[0]) {
                            map.increment(i, it.first[1])
                        }
                    }
                }
//                println(map.draw())
            }
//        println(map.draw())
        return map.count { it.value >= 2 }
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == 5)

    val input = readInput("Day05")
    println(part1(input))

    check(part2(testInput) == 1)
    println(part2(input))
}
