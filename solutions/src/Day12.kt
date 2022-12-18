import java.util.*

fun main() {
    fun part1(input: List<String>): Int {
        val grid = parseGrid(input, destinationChar = 'E')
        return search(
            grid = grid,
            destinationChar = 'E',
            fromCharGetter = { char ->
                if (char == 'S') {
                    'a'
                } else {
                    char
                }
            },
            toCharGetter = { char ->
                if (char == 'E') {
                    'z'
                } else {
                    char
                }
            },
            isWithinDistance = { to, from -> to - from <= 1 }
        )
    }

    fun part2(input: List<String>): Int {
        val grid = parseGrid(input, destinationChar = 'S') { 'a' }
        var start = Node(0, 0)

        grid.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { colIndex, _ ->
                val current = grid[rowIndex][colIndex]
                if (current == 'E') {
                    start = Node(rowIndex, colIndex)
                }
            }
        }

        return search(
            grid = grid,
            start = start,
            destinationChar = 'a',
            fromCharGetter = { char ->
                if (char == 'E') {
                    'z'
                } else {
                    char
                }
            },
            toCharGetter = { char ->
                if (char == 'S') {
                    'a'
                } else {
                    char
                }
            },
            isWithinDistance = { to, from -> from - to <= 1 }
        )
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    check(part1(testInput) == 31)
    check(part2(testInput) == 29)

    val input = readInput("Day12")
    println(part1(input))
    println(part2(input))
}

private fun search(
    grid: List<List<Char>>,
    start: Node = Node(0, 0),
    destinationChar: Char,
    fromCharGetter: (c: Char) -> Char,
    toCharGetter: (c: Char) -> Char,
    isWithinDistance: (to: Char, from: Char) -> Boolean,
): Int {
    val height = grid.size - 1
    val width = grid[0].size - 1

    val visited = mutableSetOf<Node>()
    val queue = LinkedList<Pair<Node, Int>>()
    visited.add(start)
    queue.add(start to 0)
    var currentLeader = Int.MAX_VALUE

    while (queue.isNotEmpty()) {
        val (node, count) = queue.removeLast()
        val (rowIndex, colIndex) = node
        val currentNodeChar = grid[rowIndex][colIndex]
        if (currentNodeChar == destinationChar) {
            if (count < currentLeader) {
                currentLeader = count
            }
            break
        }

        val neighbours = mutableListOf<Node>()
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

        neighbours.forEach { neighbour ->
            val (neighbourX, neighbourY) = neighbour
            val neighbourChar = grid[neighbourX][neighbourY]
            val from = fromCharGetter(currentNodeChar)
            val to = toCharGetter(neighbourChar)

            if (isWithinDistance(to, from) && !visited.contains(neighbour)) {
                visited.add(neighbour)
                queue.addFirst(neighbour to count + 1)
            }
        }
    }
    return currentLeader
}

private fun parseGrid(
    input: List<String>,
    destinationChar: Char,
    destinationCharTransformer: (c: Char) -> Char = { it },
): List<List<Char>> {
    return input.map {
        it.split("")
            .filterNot { it == "" }
            .map {
                val char = it.single()
                if (char == destinationChar) {
                    destinationCharTransformer(char)
                } else {
                    char
                }
            }
    }
}

data class Node(
    val rowIndex: Int,
    val colIndex: Int,
)
