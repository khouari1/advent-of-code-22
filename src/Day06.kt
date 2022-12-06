fun main() {
    fun part1(input: String): Int = count(input, 4)

    fun part2(input: String): Int = count(input, 14)

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test").first()
    check(part1(testInput) == 7)
    check(part2(testInput) == 19)

    val input = readInput("Day06").first()
    println(part1(input))
    println(part2(input))
}

private fun count(input: String, startOfPacketMarkerCount: Int): Int {
    val startOfPacketMarkerIndex = startOfPacketMarkerCount - 1
    var count = startOfPacketMarkerIndex
    var letters: String
    while (count < input.length) {
        letters = input.substring(count - startOfPacketMarkerIndex, count + 1)
        if (!hasDuplicate(letters, startOfPacketMarkerIndex)) {
            break
        } else {
            count++
        }
    }
    return count + 1
}

private fun hasDuplicate(letters: String, until: Int): Boolean {
    val sortedChars = letters.toCharArray().sorted()
    for (i in 0 until until) {
        if (sortedChars[i] == sortedChars[i + 1]) {
            return true
        }
    }
    return false
}
