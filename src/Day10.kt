import java.util.*

fun main() {

    val openingChunks = setOf('(', '[', '{', '<')

    infix fun Char.closes(c: Char): Boolean = when (this) {
        ')' -> c == '('
        ']' -> c == '['
        '}' -> c == '{'
        '>' -> c == '<'
        else -> throw IllegalArgumentException("Unsupported input char '$c'")
    }

    fun Char.toScore():Int = when(this) {
        ')' -> 1
        ']' -> 2
        '}' -> 3
        '>' -> 4
        else -> throw IllegalArgumentException("Can't score '$this'")
    }

    fun CharArray.invalidChar(): Char? {
        val stack: Deque<Char> = ArrayDeque()
        this.forEach {
            if (openingChunks.contains(it)) {
                stack.add(it)
            } else {
                if (!(it closes stack.removeLast())) {
                    return it
                }
            }
        }
        return null
    }
    fun CharArray.missing(): CharArray {
        val stack: Deque<Char> = ArrayDeque()
        this.forEach {
            if (openingChunks.contains(it)) {
                stack.add(it)
            } else {
                if (!(it closes stack.removeLast())) {
                    throw IllegalStateException("Corrupted input $this")
                }
            }
        }
        return stack.reversed().map { when (it) {
            '(' -> ')'
            '[' -> ']'
            '{' -> '}'
            '<' -> '>'
            else -> throw IllegalArgumentException("Unsupported char in ouput $it")
        } }.toCharArray()
    }

    fun part1(input: List<String>): Int {
        return input.mapNotNull { it.toCharArray().invalidChar() }.sumOf { when(it) {
            ')' -> 3
            ']' -> 57
            '}' -> 1197
            '>' -> 25137
            else -> throw IllegalArgumentException("Unsupported char '$it'")
        } as Int }
    }

    fun part2(input: List<String>): Long {
        return input
            .filter { it.toCharArray().invalidChar() == null }
            .map { it.toCharArray().missing() }
            .map { it.fold(0L) {acc, next -> acc * 5 + next.toScore()} }
            .sorted()
            .let { it[it.size / 2] }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
    check(part1(testInput) == 26397)

    val input = readInput("Day10")
    println(part1(input))

    check(part2(testInput) == 288957L)
    println(part2(input))
}
