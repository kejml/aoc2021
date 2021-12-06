fun main() {
    fun part1(input: String): Int {
        val school = input.split(",").map { Fish(it.toInt()) }.toMutableList()
        for (i in 1 .. 80) {
            school.nextDay()
        }
        return school.size
    }

    fun part2(input: String): Int {
        return 0
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readText("Day06_test")
    check(part1(testInput) == 5934)

    val input = readText("Day06")
    println(part1(input))

    check(part2(testInput) == 1)
    println(part2(input))
}

class Fish(var age: Int = 8) {
    fun nextDay(): Boolean {
        age--
        if (age < 0) {
            age = 6
            return true
        }
        return false
    }
}

fun MutableList<Fish>.nextDay() {
    var newFish = 0
    this.forEach { if (it.nextDay()) { newFish++ } }
    for (i in 1..newFish) {
        this.add(Fish())
    }
}