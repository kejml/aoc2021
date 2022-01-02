import kotlin.math.max
import kotlin.math.min

fun main() {
    // ALU tests
    fun test1(input: Int): Long {
        val testInput = readInput("Day24_test")
        val alu = Alu()
        alu.execute(testInput, listOf(input))
        return alu.x
    }

    fun test2(input: List<Int>): Long {
        val program = readInput("Day24_test2")
        val alu = Alu()
        alu.execute(program, input)
        return alu.z
    }

    fun test3(input: Int): String {
        val testInput = readInput("Day24_test3")
        val alu = Alu()
        alu.execute(testInput, listOf(input))
        return alu.w.toString() + alu.x.toString() + alu.y.toString() + alu.z.toString()
    }

    // Serial number computation
    fun validSerialNumber(input: List<String>, chooser: (Long, Long) -> Long): Long {
        val alu = Alu()

        fun findZeds(
            k: Int,
            startZ: Int = 0,
            numSoFar: String = "",

            ): Map<Int, String> {
            val zeds = HashMap<Int, String>()
            val intRange = (9 downTo 1).shuffled()
            for (i in intRange) {
                alu.execute(input.take(k * 18).drop((k - 1) * 18), listOf(i), startZ.toLong())
                zeds[alu.z.toInt()] = numSoFar + i.toString()
            }
            return zeds.filter { it.key < 500_000 } // ad-hoc pruning constant
        }

        fun newZeds(k: Int, oldZeds: Map<Int, String>): Map<Int, String> {
            var result: Map<Int, String> = HashMap()
            oldZeds.forEach { zInputs ->
                val found = findZeds(k, zInputs.key, zInputs.value)

                result = (result.keys + found.keys)
                    .associateWith { zIndex ->
                        val b = result[zIndex]
                        val a = found[zIndex]
                        if (a == null) {
                            b ?: ""
                        } else {
                            if (b == null) a else chooser(a.toLong(), b.toLong()).toString()
                        }
                    }
            }
            return result
        }

        var zeds: Map<Int, String> = findZeds(1, 0)

        for (i in 2..14) {
            print(i)
            zeds = newZeds(i, zeds)
            println(" - size is ${zeds.size}")
        }
        return zeds[0]!!.toLong()
    }

    fun part1(input: List<String>): Long {
        return validSerialNumber(input, ::max)
    }

    fun part2(input: List<String>): Long {
        return validSerialNumber(input, ::min)
    }

    // test if ALU works
    check(test1(2) == -2L)
    check(test1(-3) == 3L)
    check(test1(0) == 0L)

    check(test2(listOf(2, 6)) == 1L)
    check(test2(listOf(3, 6)) == 0L)
    check(test2(listOf(0, 0)) == 1L)

    check(test3(5) == "0101")
    check(test3(17) == "0001")
    check(test3(0) == "0000")

    // actual computation
    val input = readInput("Day24")
    println(part1(input))
    println(part2(input))
}

class Alu {
    var w = 0L
    var x = 0L
    var y = 0L
    var z = 0L

    fun execute(instructions: List<String>, input: List<Int>, z: Long = 0) {
        this.reset()
        this.z = z
        var inputBuffer = input.toMutableList()
        instructions.forEach { inputBuffer = executeSingle(it, inputBuffer).toMutableList() }
    }

    private fun reset() {
        w = 0
        x = 0
        y = 0
        z = 0
    }

    private fun executeSingle(rawInstruction: String, input: List<Int>): List<Int> {
        val split = rawInstruction.split(" ")
        val code = split[0]
        val a = split[1]
        if (code == "inp") {
            when (a) {
                "w" -> w = input[0].toLong()
                "x" -> x = input[0].toLong()
                "y" -> y = input[0].toLong()
                "z" -> z = input[0].toLong()
                else -> throw IllegalArgumentException("Expected register name, but got '$a'")
            }
            return input.drop(1)
        } else {
            val b = split[2]
            val instruction = Instruction.values().first { code == it.code }
            val second = when (b) {
                "w" -> w
                "x" -> x
                "y" -> y
                "z" -> z
                else -> b.toLong()
            }

            when (a) {
                "w" -> w = instruction.execute(w, second)
                "x" -> x = instruction.execute(x, second)
                "y" -> y = instruction.execute(y, second)
                "z" -> z = instruction.execute(z, second)
                else -> throw IllegalArgumentException("Expected register name, but got '$a'")
            }
            return input
        }
    }
}

enum class Instruction(val code: String, val compute: (a: Long, b: Long) -> Long) {
    ADD("add", { a, b -> a + b }),
    MULTIPLY("mul", { a, b -> a * b }),
    DIVIDE("div", { a, b -> a / b }),
    MODULO("mod", { a, b -> a % b }),
    EQUAL("eql", { a, b -> if (a == b) 1 else 0 }),
}

fun Instruction.execute(a: Long, b: Long): Long {
    return this.compute(a, b)
}