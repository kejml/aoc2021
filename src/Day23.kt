import java.util.*
import kotlin.math.abs

val maxInit = 30000
var bestSoFar = maxInit

fun main() {

    fun initState(state: State) {
        val toDo = state.init()
        var lastReported = 0
        while (toDo.isNotEmpty()) {
            val last = toDo.last()
            val lastIndex = toDo.size - 1
            toDo.addAll(last.init())
            toDo.removeAt(lastIndex)
            val reported = toDo.size / 100_000
            if (lastReported != reported) {
                println(toDo.size)
                lastReported = reported
            }
        }
    }

    fun part1(input: List<String>): Int {
        val start = State(
            listOf(
                Room(Amphipod.A, input[2][3].toAmphipod(), input[3][3].toAmphipod()),
                Room(Amphipod.B, input[2][5].toAmphipod(), input[3][5].toAmphipod()),
                Room(Amphipod.C, input[2][7].toAmphipod(), input[3][7].toAmphipod()),
                Room(Amphipod.D, input[2][9].toAmphipod(), input[3][9].toAmphipod()),
            ), Hallway(),
            0
        )
        initState(start)
        println("Tree created")
        println(bestSoFar)

        return bestSoFar
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput2 = readInput("Day23_test2")
    check(part1(testInput2) == 46)

    bestSoFar = maxInit

    val testInput = readInput("Day23_test")
    check(part1(testInput) == 12521)

    bestSoFar = maxInit //20534

    val input = readInput("Day23")
    println(part1(input))

    check(part2(testInput) == 1)
    println(part2(input))
}

class Cell(val content: Amphipod? = null) {
    fun isEmpty() = content == null

    override fun toString(): String {
        return content?.toString() ?: "."
    }

}

class Room(val homeOwner: Amphipod, topContent: Amphipod? = null, bottomContent: Amphipod? = null) {
    val cellTop = Cell(topContent)
    val cellBottom = Cell(bottomContent)
    fun isDone() = cellTop.content == homeOwner && cellBottom.content == homeOwner

    fun canAccetpt(a: Amphipod): Boolean {
        if (homeOwner != a) return false
        if (!cellTop.isEmpty()) return false
        if (cellTop.isEmpty() && cellBottom.isEmpty()) return true
        if (cellTop.isEmpty() && cellBottom.content == homeOwner) return true
        return false
    }

    fun getFirst(): Amphipod {
        return getFirstOrNull() ?: throw IllegalStateException("Can't remove amphipod from an empty room")
    }

    fun getFirstOrNull(): Amphipod? {
        if (cellTop.content != null) return cellTop.content
        if (cellBottom.content != null) return cellBottom.content
        return null
    }

    fun removeFirst(): Room {
        if (cellTop.content != null) return Room(homeOwner, bottomContent = cellBottom.content)
        if (cellBottom.content != null) return Room(homeOwner)
        throw IllegalStateException("Can't remove amphipod from an empty room")
    }

    fun add(amphipod: Amphipod): Room {
        return if (cellBottom.content == null) {
            Room(homeOwner, bottomContent = amphipod)
        } else if (cellTop.content == null) {
            Room(homeOwner, amphipod, cellBottom.content)
        } else {
            throw IllegalStateException("Room $homeOwner is already full!")
        }
    }

    fun emptyDistance(): Int {
        return if (cellBottom.isEmpty()) 2
        else if (cellTop.isEmpty()) 1
        else 0
    }

    override fun toString(): String {
        return "Room(homeOwner=$homeOwner, cellTop=$cellTop, cellBottom=$cellBottom)"
    }


}

class Hallway(
    val cells: Array<Cell> = Array(11) { Cell() }
) {
    val entries = listOf(2, 4, 6, 8)

    operator fun get(i: Int) = cells[i]
    override fun toString(): String {
        return cells.map { if (it.isEmpty()) "." else it.content }.joinToString("")
    }
}


class State(
    val rooms: List<Room> = listOf(Room(Amphipod.A), Room(Amphipod.B), Room(Amphipod.C), Room(Amphipod.D)),
    val hallway: Hallway = Hallway(),
    val priceUntilNow: Int,
    val children: MutableList<State> = LinkedList()
) {
    fun init(): MutableList<State> {
        //println(priceUntilNow)
        if (this.isDone()) {
            if (priceUntilNow < bestSoFar) {
                bestSoFar = priceUntilNow
                println("Found! $bestSoFar")
            }
        } else if (priceUntilNow < bestSoFar) {

            val statesFromRooms = rooms.filter {
                if (!it.cellTop.isEmpty() && (it.cellTop.content != it.homeOwner || it.cellBottom.content != it.homeOwner)) {
                    true
                } else it.cellTop.isEmpty() && !it.cellBottom.isEmpty() && it.cellBottom.content != it.homeOwner
            }.map { room ->
                reachableHallways(room).map { newState(room, it) } +
                        rooms.mapNotNull { roomnTo ->
                            if (isRoomReachable(room, roomnTo)) newState(
                                room,
                                roomnTo
                            ) else null
                        }
            }.flatten()

            val statesFromHallway =
                hallway.cells.mapIndexedNotNull { index, cell -> if (!cell.isEmpty()) index else null }.map { index ->
                    rooms.filter { isRoomReachable(it, index) }.map { room -> newState(index, room) }
                }.flatten()

            children.addAll(statesFromRooms + statesFromHallway)
            return children
        }

        return mutableListOf()
    }

    fun isDone(): Boolean = rooms.all { it.isDone() }

    fun newState(from: Room, hallwayIndex: Int): State {
        val amphipod = from.getFirst()
        val roomId = from.homeOwner.roomPositon / 2 - 1
        val distance = abs(from.homeOwner.roomPositon - hallwayIndex) + from.emptyDistance() + 1
        val rooms = this.rooms.mapIndexed { index, room -> if (index == roomId) room.removeFirst() else room }
        val hallway =
            Hallway(this.hallway.cells.mapIndexed { index, cell -> if (index == hallwayIndex) Cell(amphipod) else cell }
                .toTypedArray())
        return State(rooms, hallway, priceUntilNow + distance * amphipod.cost)
    }

    fun newState(fromIndex: Int, to: Room): State {
        val amphipod = hallway[fromIndex].content ?: throw IllegalArgumentException("Empty hallway at $fromIndex")
        val roomId = to.homeOwner.roomPositon / 2 - 1
        val distance = abs(to.homeOwner.roomPositon - fromIndex) + to.emptyDistance()
        val rooms = this.rooms.mapIndexed { index, room -> if (index == roomId) room.add(amphipod) else room }
        val hallway = Hallway(this.hallway.cells.mapIndexed { index, cell -> if (index == fromIndex) Cell() else cell }
            .toTypedArray())
        return State(rooms, hallway, priceUntilNow + distance * amphipod.cost)
    }

    fun newState(from: Room, to: Room): State {
        val amphipod = from.getFirst()
        val fromRoomId = from.homeOwner.roomPositon / 2 - 1
        val toRoomId = to.homeOwner.roomPositon / 2 - 1
        val distance =
            abs(from.homeOwner.roomPositon - to.homeOwner.roomPositon) + 1 + from.emptyDistance() + to.emptyDistance()
        val rooms = this.rooms.mapIndexed { index, room ->
            if (index == fromRoomId) {
                room.removeFirst()
            } else if (index == toRoomId) {
                room.add(amphipod)
            } else {
                room
            }
        }
        val hallway = this.hallway
        return State(rooms, hallway, priceUntilNow + distance * amphipod.cost)
    }

    fun reachableHallways(from: Room): List<Int> {
        return (0..10).filter { isHallwayReachable(it, from) }
    }

    fun isHallwayReachable(i: Int, from: Room): Boolean {
        if (!hallway[i].isEmpty()) return false
        if (i in hallway.entries) return false

        val destination = from.homeOwner.roomPositon
        return (if (i > destination) destination..i else i..destination).map { hallway[it] }.all { it.isEmpty() }
    }

    fun isRoomReachable(room: Room, fromIndex: Int): Boolean {
        val a = hallway[fromIndex].content ?: throw IllegalArgumentException("Hallway at $fromIndex is empty!")
        if (!room.canAccetpt(a)) return false
        val destination = room.homeOwner.roomPositon
        return (if (fromIndex < destination) (fromIndex + 1)..destination else destination until fromIndex).map { hallway[it] }
            .all { it.isEmpty() }
    }

    fun isRoomReachable(from: Room, to: Room): Boolean {
        if (from.homeOwner == to.homeOwner) return false
        if (!to.canAccetpt(from.getFirst())) return false

        val start = from.homeOwner.roomPositon
        val destination = to.homeOwner.roomPositon
        return (if (start < destination) start..destination else destination..start).map { hallway[it] }
            .all { it.isEmpty() }
    }

    override fun toString(): String {
        var res = "#############\n"
        res += "#$hallway#\n"
        res += "###${rooms[0].cellTop}#${rooms[1].cellTop}#${rooms[2].cellTop}#${rooms[3].cellTop}###\n"
        res += "  #${rooms[0].cellBottom}#${rooms[1].cellBottom}#${rooms[2].cellBottom}#${rooms[3].cellBottom}###\n"
        res += "  #########"
        return res
    }

}

enum class Amphipod(val cost: Int, val roomPositon: Int) {
    A(1, 2), B(10, 4), C(100, 6), D(1000, 8),
}

fun Char.toAmphipod(): Amphipod {
    return when (this) {
        'A' -> Amphipod.A
        'B' -> Amphipod.B
        'C' -> Amphipod.C
        'D' -> Amphipod.D
        else -> throw IllegalArgumentException("Can't parse char '$this'")
    }
}