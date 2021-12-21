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

    fun part2(input: List<String>): Long {
        var p1 = 0L
        var p2 = 0L
        val players = input.map { Player(it[7].digitToInt(), it.last().digitToInt().toLong()) }
        val game = Game(players[0], players[1])
        var games = game.roll().first
        while (games.isNotEmpty()) {
            games = games.map { it.roll() }.flatMap {
                // !! side effect
                p1 += it.second.first
                p2 += it.second.second
                it.first
            }.toMutableList()
        }
        return if (p1 > p2) p1 else p2
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day21_test")
    check(part1(testInput) == 739785)

    val input = readInput("Day21")
    println(part1(input))

    check(part2(testInput) == 444356092776315L)
    println(part2(input))
}

class Game(private val p1: Player, private val p2: Player, private val multiplier: Long = 1) {
    fun roll(): Pair<MutableList<Game>, Pair<Long, Long>> {
        val games = mutableListOf<Game>()
        var p1w = 0L
        var p2w = 0L

        val rolls = 3..9
        rolls.forEach {
            val newMultiplier = multiplier * when (it) {
                3, 9 -> 1
                4, 8 -> 3
                5, 7 -> 6
                6 -> 7
                else -> throw IllegalArgumentException("Illegal roll $it")
            }
            val newGame = Game(p2, p1.moveCopy(it), newMultiplier)
            when (newGame.winner()) {
                null -> games.add(newGame)
                1 -> p1w += newMultiplier
                2 -> p2w += newMultiplier
            }

        }
        return games to Pair(p1w, p2w)
    }

    private fun winner(): Int? {
        if (p2.score >= 21) {
            return p2.id
        }
        return null
    }

    override fun toString(): String {
        return "Game(${p1.score}:${p2.score}, p1=${p1.id}, p2=${p2.id}, multiplier=$multiplier)"
    }
}

class Player(val id: Int, startingPosition: Long, startingScore: Int = 0) {
    private val boardSize = 10
    private var _score = startingScore
    private var _position = startingPosition - 1

    fun move(roll: Int): Int {
        _position += roll
        _score += position

        return _score
    }

    fun moveCopy(roll: Int): Player {
        val p = copy()
        p.move(roll)
        return p
    }

    private fun copy(): Player {
        return Player(id, position.toLong(), score)
    }

    val score: Int
        get() = _score

    private val position: Int
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
