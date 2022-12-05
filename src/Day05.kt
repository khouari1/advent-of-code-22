fun main() {
    fun part1(input: List<String>): String = getTopCrates(input) { from, to, numberToMove ->
        for (i in 1..numberToMove) {
            val poppedChar = from.removeLast()
            to.addLast(poppedChar)
        }
    }

    fun part2(input: List<String>): String = getTopCrates(input) { from, to, numberToMove ->
        val fromDeque = ArrayDeque<Char>()
        for (i in 1..numberToMove) {
            val poppedChar = from.removeLast()
            fromDeque.addFirst(poppedChar)
        }
        fromDeque.toList().forEach { to.add(it) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == "CMZ")
    check(part2(testInput) == "MCD")

    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}

private fun getTopCrates(
    input: List<String>,
    moveCrates: (from: ArrayDeque<Char>, to: ArrayDeque<Char>, numberToMove: Int) -> Unit,
): String {
    val numberOfColumns = input.getNumberOfColumns()
    val deques = mutableMapOf<Int, ArrayDeque<Char>>()
    for (i in 1..numberOfColumns) {
        deques[i] = ArrayDeque()
    }
    var lineCounter = 0
    var rowCharIndex = 1
    run outer@{
        input.forEach {
            for (i in 0 until deques.size) {
                if (rowCharIndex > it.length) {
                    rowCharIndex = 1
                    lineCounter++
                    return@forEach
                }
                val colValue = it[rowCharIndex]
                if (colValue == '1') {
                    lineCounter++
                    return@outer
                }
                if (colValue != ' ') {
                    deques[i + 1]?.addFirst(colValue)
                }
                rowCharIndex = rowCharIndex.moveToNextColIndex()
            }
            rowCharIndex = ROW_CHAR_STARTING_INDEX
            lineCounter++
        }
    }
    input.drop(lineCounter + 1)
        .forEach {
            val (_, move, _, from, _, to) = it.split(" ")
            val fromDeque = deques[from.toInt()]!!
            val toDeque = deques[to.toInt()]!!
            moveCrates(fromDeque, toDeque, move.toInt())
        }
    return deques.values
        .filter { it.isNotEmpty() }
        .map { it.removeLast() }
        .joinToString(separator = "")
}

private fun List<String>.getNumberOfColumns() =
    first { it[1] == '1' }
        .filterNot { it.isWhitespace() }
        .toCharArray()
        .last()
        .digitToInt()

private fun Int.moveToNextColIndex() = this + 4
private const val ROW_CHAR_STARTING_INDEX = 1

private operator fun <E> List<E>.component6() = this[5]
