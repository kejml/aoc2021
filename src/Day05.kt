import java.util.HashMap

fun main() {
    fun HashMap<Pair<Int, Int>, Int>.increment(
        x: Int,
        y: Int
    ) {
        compute(Pair(x, y)) { _, v -> (v ?: 0) + 1 }
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


    fun part1(input: List<String>, diagonal: (Pair<List<Int>, List<Int>>, HashMap<Pair<Int, Int>, Int>) -> Unit = {_, _ -> }): Int {
        val map = HashMap<Pair<Int, Int>, Int>()
        input
            .map { it.split(" -> ") }
            .map { line ->
                Pair(
                    line[0].split(",").map { it.toInt() },
                    line[1].split(",").map { it.toInt() }
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
                } else {
                    diagonal(it, map)
                }
//                println(map.draw())
            }
//        println(map.draw())
        return map.count { it.value >= 2 }
    }

    fun part2(input: List<String>): Int {
        return part1(input) { coordinates, map ->
            if (coordinates.first[0] < coordinates.second[0]) {
                if (coordinates.first[1] < coordinates.second[1]) {
                    for (i in coordinates.first[0]..coordinates.second[0]) {
                        map.increment(i, coordinates.first[1] + i - coordinates.first[0])
                    }
                } else {
                    for (i in coordinates.first[0]..coordinates.second[0]) {
                        map.increment(i, coordinates.first[1] - i + coordinates.first[0])
                    }
                }
            } else {
                if (coordinates.second[1] < coordinates.first[1]) {
                    for (i in coordinates.second[0]..coordinates.first[0]) {
                        map.increment(i, coordinates.second[1] + i - coordinates.second[0])
                    }
                } else {
                    for (i in coordinates.second[0]..coordinates.first[0]) {
                        map.increment(i, coordinates.second[1] - i + coordinates.second[0])
                    }
                }
            }
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == 5)

    val input = readInput("Day05")
    println(part1(input))

    check(part2(testInput) == 12)
    println(part2(input))
}
