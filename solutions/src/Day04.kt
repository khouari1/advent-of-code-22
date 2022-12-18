fun main() {
    fun part1(input: List<String>): Int {
        return input.count { line ->
            val (one, two) = line.asNumberPairs()
            val (oneLowInt, oneHighInt) = one
            val (twoLowInt, twoHighInt) = two
            (oneLowInt >= twoLowInt && oneHighInt <= twoHighInt) || (twoLowInt >= oneLowInt && twoHighInt <= oneHighInt)
        }
    }

    fun part2(input: List<String>): Int {
        return input.count { line ->
            val (one, two) = line.asNumberPairs()
            val (oneLowInt, oneHighInt) = one
            val (twoLowInt, twoHighInt) = two
            val oneRange: IntRange = oneLowInt..oneHighInt
            val twoRange = twoLowInt..twoHighInt
            oneRange.any { it in twoRange }
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}

private fun String.asNumberPairs(): Pair<Pair<Int, Int>, Pair<Int, Int>> {
    val (one, two) = split(",")
    val (oneLow, oneHigh) = one.split("-")
    val (twoLow, twoHigh) = two.split("-")
    val oneLowInt = oneLow.toInt()
    val oneHighInt = oneHigh.toInt()
    val twoLowInt = twoLow.toInt()
    val twoHighInt = twoHigh.toInt()
    return (oneLowInt to oneHighInt) to (twoLowInt to twoHighInt)
}
