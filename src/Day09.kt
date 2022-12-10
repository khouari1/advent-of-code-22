import kotlin.math.absoluteValue

fun main() {
    fun part1(input: List<String>): Int {
        val tailVisitedCoords = mutableSetOf<Pair<Int, Int>>()
        var headCoord = (0 to 0)
        var tailCoord = (0 to 0)
        tailVisitedCoords.add((0 to 0))
        input.forEach { line ->
            val (direction, numberOfSteps) = line.split(" ")
            for (i in 1..numberOfSteps.toInt()) {
                var (headX, headY) = headCoord
                when (direction) {
                    "R" -> {
                        headCoord = (headX + 1 to headY)
                    }

                    "L" -> {
                        headCoord = (headX - 1 to headY)
                    }

                    "U" -> {
                        headCoord = (headX to headY + 1)
                    }

                    "D" -> {
                        headCoord = (headX to headY - 1)
                    }
                }
                headX = headCoord.first
                headY = headCoord.second
                val (tailX, tailY) = tailCoord
                if (!isTouching(headCoord, tailCoord)) {
                    when {
                        (headY - tailY == 2 && headX - tailX == 1) || (headY - tailY == 1 && headX - tailX == 2) -> { // move diagonally up and right
                            tailCoord = (tailX + 1 to tailY + 1)
                            tailVisitedCoords.add((tailX + 1 to tailY + 1))
                        }

                        (headY - tailY == 2 && headX - tailX == -1) || (headY - tailY == 1 && headX - tailX == -2) -> { // move diagonally up and left
                            tailCoord = (tailX - 1 to tailY + 1)
                            tailVisitedCoords.add((tailX - 1 to tailY + 1))
                        }

                        (headY - tailY == -2 && headX - tailX == 1) || (headY - tailY == -1 && headX - tailX == 2) -> { // move diagonally down and right
                            tailCoord = (tailX + 1 to tailY - 1)
                            tailVisitedCoords.add((tailX + 1 to tailY - 1))
                        }

                        (headY - tailY == -2 && headX - tailX == -1) || (headY - tailY == -1 && headX - tailX == -2) -> { // move diagonally down and left
                            tailCoord = (tailX - 1 to tailY - 1)
                            tailVisitedCoords.add((tailX - 1 to tailY - 1))
                        }

                        headX - tailX == 2 -> { // move right
                            tailCoord = (tailX + 1 to tailY)
                            tailVisitedCoords.add((tailX + 1 to tailY))
                        }

                        headX - tailX == -2 -> { // move left
                            tailCoord = (tailX - 1 to tailY)
                            tailVisitedCoords.add((tailX - 1 to tailY))
                        }

                        headY - tailY == 2 -> { // move up
                            tailCoord = (tailX to tailY + 1)
                            tailVisitedCoords.add((tailX to tailY + 1))
                        }

                        headY - tailY == -2 -> { // move down
                            tailCoord = (tailX to tailY - 1)
                            tailVisitedCoords.add((tailX to tailY - 1))
                        }
                    }
                }
            }
        }
        return tailVisitedCoords.size
    }

    fun part2(input: List<String>): Int {
        val tailVisitedCoords = mutableSetOf<Pair<Int, Int>>()
        var headCoord = (0 to 0)
        var tailCoords = (1..9).associateWith { (0 to 0) }
        tailVisitedCoords.add((0 to 0))
        input.forEach { line ->
            val (direction, numberOfSteps) = line.split(" ")
            for (i in 1..numberOfSteps.toInt()) {
                var (headX, headY) = headCoord
                when (direction) {
                    "R" -> {
                        headCoord = (headX + 1 to headY)
                    }

                    "L" -> {
                        headCoord = (headX - 1 to headY)
                    }

                    "U" -> {
                        headCoord = (headX to headY + 1)
                    }

                    "D" -> {
                        headCoord = (headX to headY - 1)
                    }
                }
                var head = headCoord
                val newTailCoords = tailCoords.toMutableMap()
                tailCoords.forEach { (index, tailCoord) ->
                    headX = head.first
                    headY = head.second
                    val (tailX, tailY) = tailCoord
                    if (!isTouching(head, tailCoord)) {
                        when {
                            headX - tailX == 2 && headY - tailY == 0 -> { // move right
                                val t = (tailX + 1 to tailY)
                                newTailCoords[index] = t
                                if (index == 9) {
                                    tailVisitedCoords.add((tailX + 1 to tailY))
                                }
                            }

                            headX - tailX == -2 && headY - tailY == 0 -> { // move left
                                val t = (tailX - 1 to tailY)
                                newTailCoords[index] = t
                                if (index == 9) {
                                    tailVisitedCoords.add((tailX - 1 to tailY))
                                }
                            }

                            headY - tailY == 2 && headX - tailX == 0 -> { // move up
                                val t = (tailX to tailY + 1)
                                newTailCoords[index] = t
                                if (index == 9) {
                                    tailVisitedCoords.add((tailX to tailY + 1))
                                }
                            }

                            headY - tailY == -2 && headX - tailX == 0 -> { // move down
                                val t = (tailX to tailY - 1)
                                newTailCoords[index] = t
                                if (index == 9) {
                                    tailVisitedCoords.add((tailX to tailY - 1))
                                }
                            }

                            headY - tailY > 0 && headX - tailX > 0 -> { // move diagonally up and right
                                val t = (tailX + 1 to tailY + 1)
                                newTailCoords[index] = t
                                if (index == 9) {
                                    tailVisitedCoords.add((tailX + 1 to tailY + 1))
                                }
                            }

                            headY - tailY > 0 && headX - tailX < 0 -> { // move diagonally up and left
                                val t = (tailX - 1 to tailY + 1)
                                newTailCoords[index] = t
                                if (index == 9) {
                                    tailVisitedCoords.add((tailX - 1 to tailY + 1))
                                }
                            }

                            headY - tailY < 0  && headX - tailX > 0 -> { // move diagonally down and right
                                val t = (tailX + 1 to tailY - 1)
                                newTailCoords[index] = t
                                if (index == 9) {
                                    tailVisitedCoords.add((tailX + 1 to tailY - 1))
                                }
                            }

                            headY - tailY < 0 && headX - tailX < 0 -> { // move diagonally down and left
                                val t = (tailX - 1 to tailY - 1)
                                newTailCoords[index] = t
                                if (index == 9) {
                                    tailVisitedCoords.add((tailX - 1 to tailY - 1))
                                }
                            }
                        }
                    }
                    head = newTailCoords[index]!!
                }
                tailCoords = newTailCoords
            }
        }
        return tailVisitedCoords.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 13)
    val testInput2 = readInput("Day09_test2")
    check(part2(testInput2) == 36)

    val input = readInput("Day09")
    println(part1(input))
    println(part2(input))
}

fun isTouching(headCoord: Pair<Int, Int>, tailCoord: Pair<Int, Int>): Boolean {
    val (headX, headY) = headCoord
    val (tailX, tailY) = tailCoord
    return (headX - tailX).absoluteValue < 2 && (headY - tailY).absoluteValue < 2
}
