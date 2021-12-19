import kotlin.math.ceil
import kotlin.math.floor

fun main() {
    fun part1(input: List<String>): Long {
        return input.map { SfPair.fromString(it) }.reduce { acc, sfPair -> acc + sfPair }.magnitude()
    }

    fun part2(input: List<String>): Long {
        val allPairs = cartesianProduct(input, input).filter { it.first != it.second }
        var max = 0L
        allPairs.forEach {
            max = max.coerceAtLeast(part1(listOf(it.first, it.second)))
        }
        return max
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day18_test")
    check(part1(testInput) == 4140L)

    val input = readInput("Day18")
    println(part1(input))

    check(part2(testInput) == 3993L)
    println(part2(input))
}

sealed class SfNumber(var parent: SfPair? = null) {
    operator fun plus(other: SfNumber): SfPair {
        val result = SfPair(this, other)
        this.parent = result
        other.parent = result
        return result.reduce()
    }

    abstract fun magnitude(): Long
}

class SfRegular(var value: Long, parent: SfPair? = null) : SfNumber(parent) {
    override fun toString(): String = value.toString()
    override fun magnitude(): Long = value
    fun split(parent: SfPair?): SfPair =
        SfPair(SfRegular(floor(value / 2.0).toLong()), SfRegular(ceil(value / 2.0).toLong()), parent)
}

object SfNothing : SfNumber() {
    override fun toString(): String = ""
    override fun magnitude(): Long = Long.MIN_VALUE
}

class SfPair(var left: SfNumber, var right: SfNumber, parent: SfPair? = null) : SfNumber(parent) {
    companion object {
        fun fromString(input: String): SfPair {
            var current: SfPair? = null
            input.forEach {
                when (it) {
                    '[' -> {
                        val newCurrent = SfPair(SfNothing, SfNothing, current)
                        if (current != null) {
                            if (current!!.left == SfNothing) {
                                current!!.left = newCurrent
                            } else {
                                current!!.right = newCurrent
                            }
                        }
                        current = newCurrent

                    }
                    ']' -> {
                        if (current!!.parent == null) {
                            return current!!
                        } else {
                            current = current!!.parent
                        }
                    }
                    ',' -> {}
                    else -> {
                        if (current!!.left == SfNothing) {
                            current!!.left = SfRegular(it.digitToInt().toLong())
                        } else {
                            current!!.right = SfRegular(it.digitToInt().toLong())
                        }
                    }
                }
            }
            throw IllegalArgumentException("Can't parse input")
        }
    }

    override fun magnitude(): Long = 3 * left.magnitude() + 2 * right.magnitude()

    fun reduce(): SfPair {
        var workDone = true
        while (workDone) {
            workDone = explode()
            if (!workDone) {
                workDone = split()
            }
        }
        return this
    }

    private fun explode(node: SfPair = this, depth: Int = 0): Boolean {
        if (node.left is SfPair) {
            if (depth == 3) {
                explodeInternal(node.left as SfPair)
                node.left = SfRegular(0, node.parent)
                return true
            }
            if (explode(node.left as SfPair, depth + 1)) {
                return true
            }
        }
        if (node.right is SfPair) {
            if (depth == 3) {
                explodeInternal(node.right as SfPair)
                node.right = SfRegular(0, node.parent)
                return true
            }
            if (explode(node.right as SfPair, depth + 1)) {
                return true
            }
        }
        return false
    }

    private fun explodeInternal(sfPair: SfPair) {
        explodeLeftParent(sfPair, (sfPair.left as SfRegular).value)
        explodeRightParent(sfPair, (sfPair.right as SfRegular).value)
    }

    private fun explodeRightParent(sfPair: SfPair, value: Long) {
        var parent = sfPair
        var previous: SfPair?
        while (parent.parent != null) {
            previous = parent
            parent = parent.parent!!
            if (parent.right is SfRegular) {
                (parent.right as SfRegular).value += value
                break
            } else if (parent.right != previous) {
                explodeLeftChild(parent.right as SfPair, value)
                break
            }
        }
    }

    private fun explodeLeftParent(sfPair: SfPair, value: Long) {
        var parent = sfPair
        var previous: SfPair?

        while (parent.parent != null) {
            previous = parent
            parent = parent.parent!!
            if (parent.left is SfRegular) {
                (parent.left as SfRegular).value += value
                break
            } else if (parent.left != previous) {
                explodeRightChild(parent.left as SfPair, value)
                break
            }
        }
    }

    private fun explodeLeftChild(sfPair: SfPair, value: Long) {
        var number = sfPair
        while (number.left !is SfRegular) {
            number = number.left as SfPair
        }
        (number.left as SfRegular).value += value
    }

    private fun explodeRightChild(sfPair: SfPair, value: Long) {
        var number = sfPair
        while (number.right !is SfRegular) {
            number = number.right as SfPair
        }
        (number.right as SfRegular).value += value
    }

    private fun split(): Boolean {
        if (left is SfRegular && (left as SfRegular).value >= 10) {
            left = (left as SfRegular).split(this)
            return true
        }
        val leftSplit = if (left is SfPair) (left as SfPair).split() else false
        if (leftSplit) return true

        if (right is SfRegular && (right as SfRegular).value >= 10) {
            right = (right as SfRegular).split(this)
            return true
        }

        return if (right is SfPair) (right as SfPair).split() else false
    }

    override fun toString(): String = "[$left,$right]"
}
