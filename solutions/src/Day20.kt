import java.util.*

fun main() {
    fun part1(input: List<String>): Long = mix(input)

    fun part2(input: List<String>): Long = mix(input, timesToMix = 10, decryptionKey = 811589153)

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day20_test")
    check(part1(testInput) == 3L)
    check(part2(testInput) == 1623178306L)

    val input = readInput("Day20")
    println(part1(input))
    println(part2(input))
}

private fun mix(input: List<String>, timesToMix: Int = 1, decryptionKey: Int = 1): Long {
    val numbers = input.map { line -> line.toLong() * decryptionKey }
    val indexMap = mutableMapOf<Int, Int>()
    val numbersCopy = LinkedList<Long>()
    numbers.forEachIndexed { index, number ->
        indexMap[index] = index
        numbersCopy.add(number)
    }
    for (i in 1..timesToMix) {
        numbers.forEachIndexed { index, number ->
            val oldIndex = indexMap[index]!!
            if (number == 0L) return@forEachIndexed
            val tempNewIndex = (oldIndex + number) % (numbers.size - 1)
            val newIndex = if (tempNewIndex < 0) {
                numbers.size + tempNewIndex - 1
            } else {
                tempNewIndex
            }
            if (newIndex.toInt() == oldIndex) return@forEachIndexed

            numbersCopy.removeAt(oldIndex)
            numbersCopy.add(newIndex.toInt(), number)
            // find out which numbers' index has been affected by the move
            // update their index
            if (newIndex > oldIndex) {
                // moved right
                // everything before has been shifted to the left
                for (i in 0 until indexMap.size) {
                    val currentIndex = indexMap[i]!!
                    if (currentIndex in oldIndex + 1..newIndex) {
                        indexMap[i] = indexMap[i]!! - 1
                    }
                }
            } else {
                // moved left
                // everything after has been shifted to the right
                for (i in 0 until indexMap.size) {
                    val currentIndex = indexMap[i]!!
                    if (currentIndex in newIndex until oldIndex) {
                        indexMap[i] = indexMap[i]!! + 1
                    }
                }
            }
            indexMap[index] = newIndex.toInt()
        }
    }
    val offsetIndex = numbersCopy.indexOf(0)
    val oneThousandth = numbersCopy[(1000 + offsetIndex) % numbers.size]
    val twoThousandth = numbersCopy[(2000 + offsetIndex) % numbers.size]
    val threeThousandth = numbersCopy[(3000 + offsetIndex) % numbers.size]
    return oneThousandth + twoThousandth + threeThousandth
}
