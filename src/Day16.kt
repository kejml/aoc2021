fun main() {

    fun part1(input: String): Int {
        val binarySequence = input.asSequence().map { it.toString().toInt(16).toString(2).padStart(4, '0') }
        val binaryString = binarySequence.joinToString("")
        val packets = Packet.fromString(binaryString)
        require(packets.size == 1)
        return packets[0].versionSum()
    }

    fun part2(input: String): Long {
        val binarySequence = input.asSequence().map { it.toString().toInt(16).toString(2).padStart(4, '0') }
        val binaryString = binarySequence.joinToString("")
        val packets = Packet.fromString(binaryString)
        require(packets.size == 1)
        return packets[0].calculate()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readText("Day16_test")
    check(part1(testInput) == 6)

    check(part1("38006F45291200") == 9)

    val testInput2 = readText("Day16_test2")
    check(part1(testInput2) == 16)

    val testInput3 = readText("Day16_test3")
    check(part1(testInput3) == 12)

    val testInput4 = readText("Day16_test4")
    check(part1(testInput4) == 23)

    val testInput5 = readText("Day16_test5")
    check(part1(testInput5) == 31)

    val input = readText("Day16")
    println(part1(input))

    check(part2("C200B40A82") == 3L)
    println(part2(input))
}

infix fun Int.fillTo(base: Int): Int {
    return if (this % base == 0) 0 else base - (this % base)
}

sealed class Packet(val version: Int, val bitLength: Int) {
    companion object {
        fun fromString(input: String): List<Packet> {
            val packets = mutableListOf<Packet>()
            var rawInput = input
            while (rawInput.isNotEmpty()) {
                val packet = when (rawInput.substring(3, 6)) {
                    "100" -> Literal.fromString(rawInput)
                    else -> Operator.fromString(rawInput)
                }
                packets.add(packet)
                rawInput = rawInput.drop(packet.bitLength)
                if (rawInput.length < 11) rawInput = ""
            }
            return packets
        }
    }

    abstract fun versionSum(): Int
    abstract fun calculate(): Long
}


class Literal(version: Int, val value: Long, bitLength: Int) : Packet(version, bitLength) {
    companion object {
        fun fromString(input: String): Literal {
            val rawNumsBeforeLast = input.drop(6).chunked(5).takeWhile { it[0] != '0' }
            val lastNum = input.drop(6 + rawNumsBeforeLast.size * 5).take(5)
            val value = (rawNumsBeforeLast + lastNum).joinToString("") { it.substring(1) }.toLong(2)
            return Literal(input.take(3).toInt(2), value, 6 + rawNumsBeforeLast.size * 5 + 5)
        }
    }

    override fun versionSum(): Int = version
    override fun calculate(): Long = value
}

class Operator(version: Int, val operation: Int, val operands: List<Packet>, bitLength: Int) :
    Packet(version, bitLength) {
    companion object {
        fun fromString(input: String): Operator {
            val version = input.take(3).toInt(2)
            val operation = input.drop(3).take(3).toInt(2)
            var bitLengthL = 0
            val operands = when (input[6]) {
                '0' -> {
                    val length = input.drop(7).take(15).toInt(2)
                    bitLengthL = 15
                    Packet.fromString(input.drop(7 + 15).take(length))
                }
                '1' -> {
                    val numOfPackets = input.drop(7).take(11).toInt(2)
                    bitLengthL = 11
                    val packets = Packet.fromString(input.drop(7 + 11))
                    //require(packets.size == numOfPackets)

                    packets
                }
                else -> throw IllegalArgumentException("Unexpected char '${input[6]}'")
            }
            val bitLengthRaw = 7 + bitLengthL + operands.sumOf { it.bitLength }
            return Operator(version, operation, operands, bitLengthRaw)
        }
    }

    override fun versionSum(): Int = this.version + operands.sumOf { it.versionSum() }

    override fun calculate(): Long {
        return when (operation) {
            0 -> operands.sumOf { it.calculate() }
            1 -> operands.fold(1) {acc, packet -> acc*packet.calculate() }
            2 -> operands.minOf { it.calculate() }
            3 -> operands.maxOf { it.calculate() }
            5 -> if (operands[0].calculate() > operands[1].calculate()) 1 else 0
            6 -> if (operands[0].calculate() < operands[1].calculate()) 1 else 0
            7 -> if (operands[0].calculate() == operands[1].calculate()) 1 else 0
            else -> throw IllegalArgumentException("Unknow operation $operation")
        }
    }
}
