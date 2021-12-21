fun main() {
    fun <T> Sequence<T>.repeat() = sequence { while (true) yieldAll(this@repeat) }

    fun part1(input: List<String>): Int {
        var lastScore = 0
        val players = input.map { Player(it[7].digitToInt(), it.last().digitToInt().toLong()) }.asSequence().repeat()
        val die = DDie(100)
        val iterator = players.iterator()

        while (lastScore < 1000) {
            lastScore = iterator.next().move(die.roll(3))
        }

        return die.numRolls * iterator.next().score // only 2 players
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day21_test")
    check(part1(testInput) == 739785)

    val input = readInput("Day21")
    println(part1(input))

    check(part2(testInput) == 1)
    println(part2(input))
}

class Player(val id: Int, private val startingPosition: Long) {
    private val boardSize = 10
    private var _score = 0
    private var _position = startingPosition -1

    fun move(roll: Int): Int {
        _position += roll
        _score += position

        return _score
    }

    val score: Int
        get() = _score

    val position: Int
        get() = (_position % boardSize).toInt() + 1

}

class DDie(private val maxValue: Int) {
    private var lastRoll = -1L
    private var _numRolls = 0
    fun roll(times: Int = 1): Int {
        var roll = 0
        repeat(times) {
            roll += ((++lastRoll % maxValue) + 1).toInt()
            _numRolls++
        }
        return roll
    }

    val numRolls: Int
        get() = _numRolls

}
