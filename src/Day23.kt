import kotlin.math.abs

const val maxInit = Int.MAX_VALUE
var bestSoFar = maxInit

fun main() {

    fun initState(state: State) {
        val toDo = state.init().toMutableList()
        var lastReported = 0
        while (toDo.isNotEmpty()) {
            val last = toDo.last()
            toDo.removeAt(toDo.size - 1)
            last.parent?.children?.remove(last)
            toDo.addAll(last.init())
            val reported = state.children.size
            if (lastReported != reported) {
                println(reported)
                lastReported = reported
            }
        }
    }

    fun part1(input: List<String>): Int {
        bestSoFar = maxInit
        val start = State(
            listOf(
                Room(Amphipod.A, input[2][3].toAmphipod(), input[3][3].toAmphipod()),
                Room(Amphipod.B, input[2][5].toAmphipod(), input[3][5].toAmphipod()),
                Room(Amphipod.C, input[2][7].toAmphipod(), input[3][7].toAmphipod()),
                Room(Amphipod.D, input[2][9].toAmphipod(), input[3][9].toAmphipod()),
            ), Hallway(),
            0, parent = null
        )
        initState(start)
        println("Tree created")
        println(bestSoFar)

        return bestSoFar
    }

    fun part2(input: List<String>): Int {
        bestSoFar = maxInit
        val start = State(
            listOf(
                Room(Amphipod.A, Array(4) { Cell() }),
                Room(Amphipod.B, Array(4) { Cell() }),
                Room(Amphipod.C, Array(4) { Cell() }),
                Room(Amphipod.D, Array(4) { Cell() }),
            ), Hallway(),
            0, parent = null
        )

        start.rooms[0].cells[0] = Cell(input[2][3].toAmphipod())
        start.rooms[0].cells[3] = Cell(input[3][3].toAmphipod())
        start.rooms[1].cells[0] = Cell(input[2][5].toAmphipod())
        start.rooms[1].cells[3] = Cell(input[3][5].toAmphipod())
        start.rooms[2].cells[0] = Cell(input[2][7].toAmphipod())
        start.rooms[2].cells[3] = Cell(input[3][7].toAmphipod())
        start.rooms[3].cells[0] = Cell(input[2][9].toAmphipod())
        start.rooms[3].cells[3] = Cell(input[3][9].toAmphipod())

        start.rooms[0].cells[1] = Cell(Amphipod.D)
        start.rooms[0].cells[2] = Cell(Amphipod.D)
        start.rooms[1].cells[1] = Cell(Amphipod.C)
        start.rooms[1].cells[2] = Cell(Amphipod.B)
        start.rooms[2].cells[1] = Cell(Amphipod.B)
        start.rooms[2].cells[2] = Cell(Amphipod.A)
        start.rooms[3].cells[1] = Cell(Amphipod.A)
        start.rooms[3].cells[2] = Cell(Amphipod.C)

        initState(start)
        println("Tree created")
        println(bestSoFar)

        return bestSoFar
    }

    // test if implementation meets criteria from the description, like:
    val testInput2 = readInput("Day23_test2")
    check(part1(testInput2) == 46)

    val testInput = readInput("Day23_test")
    check(part1(testInput) == 12521)

    val input = readInput("Day23")
//    println(part1(input))

    check(part2(testInput) == 44169)
    println(part2(input))
}

class Cell(val content: Amphipod? = null) {
    fun isEmpty() = content == null

    override fun toString(): String {
        return content?.toString() ?: "."
    }
}

class Room(val homeOwner: Amphipod, val cells: Array<Cell> = Array(2) { Cell() }) {


    constructor(homeOwner: Amphipod, topContent: Amphipod? = null, bottomContent: Amphipod? = null) : this(homeOwner) {
        cells[0] = Cell(topContent)
        cells[1] = Cell(bottomContent)
    }

    fun isDone() = cells.all { it.content == homeOwner }

    fun canAccetpt(a: Amphipod): Boolean {
        if (homeOwner != a) return false
        if (cells.filter { !it.isEmpty() }.all { it.content == homeOwner } && cells[0].isEmpty()) {
            return true
        }
        return false
    }

    fun getFirst(): Amphipod {
        return getFirstOrNull() ?: throw IllegalStateException("Can't remove amphipod from an empty room")
    }

    fun getFirstOrNull(): Amphipod? {
        return cells.firstOrNull { !it.isEmpty() }?.content
    }

    fun canMoveFirst(): Boolean {
        if (cells.filter { !it.isEmpty() }.all { it.content == homeOwner }) return false
        return getFirstOrNull() != null
    }

    fun removeFirst(): Room {
        val firstIndex = cells.mapIndexed { index, cell -> if (!cell.isEmpty()) index else null }.filterNotNull().first()
        val newCells = cells.mapIndexed { index, cell -> if (index == firstIndex) Cell() else cell }.toTypedArray()
        return Room(homeOwner, cells = newCells)
    }

    fun add(amphipod: Amphipod): Room {
        val firstIndex = cells.mapIndexed { index, cell -> if (cell.isEmpty()) index else null }.filterNotNull().last()
        val newCells = cells.mapIndexed { index, cell -> if (index == firstIndex) Cell(amphipod) else cell }.toTypedArray()
        return Room(homeOwner, cells = newCells)
    }

    fun emptyDistance(): Int {
        return cells.mapIndexed { index, cell -> if (cell.isEmpty()) index + 1 else null }.filterNotNull().lastOrNull()  ?: 0
    }

    override fun toString(): String {
        return "Room(homeOwner=$homeOwner, cells=$cells"
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
    val children: MutableList<State> = mutableListOf(),
    val parent: State?
) {
    fun init(): List<State> {
        //println(priceUntilNow)
        if (this.isDone()) {
            if (priceUntilNow < bestSoFar) {
                bestSoFar = priceUntilNow
                println("Found! $bestSoFar")
            }
        } else if (priceUntilNow < bestSoFar) {

            val statesFromRooms = rooms.filter {
                it.canMoveFirst()
            }.map { roomFrom ->
                reachableHallways(roomFrom).map { newState(roomFrom, it) } +
                        rooms.mapNotNull { roomTo ->
                            if (isRoomReachable(roomFrom, roomTo)) newState(
                                roomFrom,
                                roomTo
                            ) else null
                        }
            }.flatten()

            val statesFromHallway =
                hallway.cells.mapIndexedNotNull { index, cell -> if (!cell.isEmpty()) index else null }.map { index ->
                    rooms.filter { isRoomReachable(it, index) }.map { room -> newState(index, room) }
                }.flatten()

            if (children.isNotEmpty()) throw IllegalStateException("Non empty children")

            val newChildren = (statesFromRooms + statesFromHallway).filter { it.priceUntilNow < bestSoFar }
            children.addAll(newChildren)
            return newChildren
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
        return State(rooms, hallway, priceUntilNow + distance * amphipod.cost, parent = this)
    }

    fun newState(fromIndex: Int, to: Room): State {
        val amphipod = hallway[fromIndex].content ?: throw IllegalArgumentException("Empty hallway at $fromIndex")
        val roomId = to.homeOwner.roomPositon / 2 - 1
        val distance = abs(to.homeOwner.roomPositon - fromIndex) + to.emptyDistance()
        val rooms = this.rooms.mapIndexed { index, room -> if (index == roomId) room.add(amphipod) else room }
        val hallway = Hallway(this.hallway.cells.mapIndexed { index, cell -> if (index == fromIndex) Cell() else cell }
            .toTypedArray())
        return State(rooms, hallway, priceUntilNow + distance * amphipod.cost, parent = this)
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
        return State(rooms, hallway, priceUntilNow + distance * amphipod.cost, parent = this)
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
        var res = "$priceUntilNow\n" +
                "#############\n"
        res += "#$hallway#\n"
        for (i in 0 until rooms[0].cells.size) {
            res += "###${rooms[0].cells[i]}#${rooms[1].cells[i]}#${rooms[2].cells[i]}#${rooms[3].cells[i]}###\n"
        }
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