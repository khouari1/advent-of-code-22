import java.util.*

fun main() {
    fun part1(input: List<String>): Int {
        val grid = parseGrid(input)
        val height = grid.size - 1
        val width = grid[0].size - 1

        var start = Node(0, 0)
        var destination = Node(0, 0)

        grid.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { colIndex, _ ->
                val current = grid[rowIndex][colIndex]
                if (current == 'S') {
                    start = Node(rowIndex, colIndex)
                } else if (current == 'E') {
                    destination = Node(rowIndex, colIndex)
                }
            }
        }

        val visited = mutableSetOf<Node>()
        val queue = LinkedList<Pair<Node, Int>>()
        visited.add(start)
        queue.add(start to 0)
        var currentLeader = Int.MAX_VALUE

        while (queue.isNotEmpty()) {
            val (node, count) = queue.removeLast()
            if (node == destination) {
                if (count < currentLeader) {
                    currentLeader = count
                }
                continue
            }

            val neighbours = mutableListOf<Node>()
            val (rowIndex, colIndex) = node
            if (rowIndex > 0) {
                neighbours.add(Node(rowIndex - 1, colIndex))
            }
            if (rowIndex < height) {
                neighbours.add(Node(rowIndex + 1, colIndex))
            }
            if (colIndex > 0) {
                neighbours.add(Node(rowIndex, colIndex - 1))
            }
            if (colIndex < width) {
                neighbours.add(Node(rowIndex, colIndex + 1))
            }

            val currentNodeChar = grid[rowIndex][colIndex]
            neighbours.forEach { neighbour ->
                val (neighbourX, neighbourY) = neighbour
                val neighbourChar = grid[neighbourX][neighbourY]
                val from = currentNodeChar.getNormalisedFromChar()
                val to = neighbourChar.getNormalisedToChar()

                if ((currentNodeChar == 'S' || to - from <= 1) && !visited.contains(neighbour)) {
                    visited.add(neighbour)
                    queue.addFirst(neighbour to count + 1)
                }
            }
        }
        return currentLeader
    }

    fun part2(input: List<String>): Int = 0

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    check(part1(testInput) == 31)
//    check(part2(testInput) == 10)

    val input = readInput("Day12")
    println(part1(input))
//    println(part2(input))
}

private fun parseGrid(input: List<String>): List<List<Char>> {
    return input.map {
        it.split("")
            .filterNot { it == "" }
            .map { it.single() }
    }
}

private fun Char.getNormalisedFromChar(): Char {
    return if (this == 'S') {
        'a'
    } else {
        this
    }
}

private fun Char.getNormalisedToChar(): Char {
    return if (this == 'E') {
        'z'
    } else {
        this
    }
}

data class Node(
    val rowIndex: Int,
    val colIndex: Int,
)
