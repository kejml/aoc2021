fun main() {
    fun part1(numbers: List<Int>, cards: List<List<MutableList<Int>>>): Int {
        numbers.forEach { n ->
            cards.forEach { card ->
                card.replace(n)
                if (card.check()) {
                    return card.sumOf { row -> row.filter { it != -1 }.sum() } * n
                }
            }
        }
        throw IllegalStateException("Didn't find any winning card")
    }

    fun part2(numbers: List<Int>, cards: List<List<MutableList<Int>>>): Int {
        val winning = ArrayList<Int>()
        numbers.forEach { n ->
            cards.forEachIndexed { index, card ->
                if (winning.contains(index)) {
                    return@forEachIndexed
                }
                card.replace(n)
                if (card.check()) {
                    winning.add(index)
                    if (winning.size == cards.size) {
                        return card.sumOf { row -> row.filter { it != -1 }.sum() } * n
                    }
                }
            }
        }
        throw IllegalStateException("Didn't find all winning cards")
    }

    // test if implementation meets criteria from the description, like:
    val (numbersTest, cardsTest) = readInput("Day04_test").processInput()
    check(part1(numbersTest, cardsTest) == 4512)

    val (numbers, cards) = readInput("Day04").processInput()
    println(part1(numbers, cards))

    check(part2(numbersTest, cardsTest) == 1924)
    println(part2(numbers, cards))
}

fun List<String>.processInput(): Pair<List<Int>, List<List<MutableList<Int>>>> {
    val numbers = this.first().split(",").map { it.toInt() }
    val cards = this.subList(2, this.size)
        .joinToString("\n")
        .split("\n\n")
        .map { card ->
            card.split("\n")
                .map { line ->
                    line.trim().split(Regex(" +"))
                        .map { num -> num.toInt() }
                        .toMutableList()
                }.toList()
        }.toList()
    return Pair(numbers, cards)
}

fun List<MutableList<Int>>.replace(num: Int) =
    this.forEach { it.forEachIndexed { index, i -> if (i == num) it[index] = -1 } }

fun List<MutableList<Int>>.check(): Boolean {
    for (i in 0..4) {
        if (this[i].all { it == -1 }) {
            return true
        }
    }
    for (i in 0 .. 4) {
        val column = ArrayList<Int>()
        for (j in 0..4) {
            column.add(this[j][i])
        }
        if (column.all { it == -1 }) {
            return true
        }
    }
    return false
}



