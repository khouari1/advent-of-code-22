import kotlin.math.min

fun main() {
    fun part1(input: List<String>): Int {
        val packetPairs = input.split { it.isBlank() }.map { it[0] to it[1] }
        return packetPairs.mapIndexed { index, (packet1, packet2) ->
            val packet1List = mutableListOf<Any>()
            parseToList(packet1, packet1List)
            val packet2List = mutableListOf<Any>()
            parseToList(packet2, packet2List)

            if (compareLists(packet1List, packet2List) <= 0) {
                index + 1
            } else {
                0
            }
        }.sum()
    }

    fun part2(input: List<String>): Int = 0

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test")
    check(part1(testInput) == 13)
//    check(part2(testInput) == 29)

    val input = readInput("Day13")
    println(part1(input))
//    println(part2(input))
}

private fun <T> List<T>.split(predicate: (T) -> Boolean): List<List<T>> =
    fold(mutableListOf(mutableListOf<T>())) { acc, t ->
        if (predicate(t)) {
            acc.add(mutableListOf())
        } else {
            acc.last().add(t)
        }
        acc
    }.filterNot { it.isEmpty() }

private fun parseToList(value: String, list: MutableList<Any>): Int {
    var newTotalIndex = 0
    while (newTotalIndex < value.length - 1) {
        when (value[newTotalIndex]) {
            '[' -> {
                val subList = mutableListOf<Any>()
                list.add(subList)
                val remainder = value.substring(newTotalIndex + 1)
                val newIndex = parseToList(remainder, subList)
                newTotalIndex += newIndex
            }

            ']' -> {
                newTotalIndex++
                break
            }

            ',' -> Unit
            else -> {
                var number = ""
                while (value[newTotalIndex].isDigit()) {
                    number += value[newTotalIndex]
                    newTotalIndex++
                }
                list.add(number.toInt())
                continue
            }
        }
        newTotalIndex++
    }
    return newTotalIndex
}

private fun compareLists(packet1List: List<*>, packet2List: List<*>): Int {
    val minSize = min(packet1List.size, packet2List.size)
    for (i in 0 until minSize) {
        val packet1Item = packet1List[i]!!
        val packet2Item = packet2List[i]!!

        val diff = compareValues(packet1Item, packet2Item)
        if (diff < 0) {
            return -1
        } else if (diff > 0) {
            return 1
        }
    }
    return packet1List.size - packet2List.size
}

private fun compareValues(packet1Item: Any, packet2Item: Any): Int = when {
    packet1Item is Int && packet2Item is Int -> {
        packet1Item.toInt() - packet2Item.toInt()
    }

    packet1Item is Int && packet2Item is List<*> -> {
        compareLists(listOf(packet1Item), packet2Item)
    }

    packet2Item is Int && packet1Item is List<*> -> {
        compareLists(packet1Item, listOf(packet2Item))
    }

    packet1Item is List<*> && packet2Item is List<*> -> {
        compareLists(packet1Item, packet2Item)
    }

    else -> throw Exception("Cannot compare $packet1Item to $packet2Item one is not an Int or List<*>")
}
