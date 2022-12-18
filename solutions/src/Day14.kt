fun main() {
    fun part1(input: List<String>): Int {
        val minMaxCoords = findMinMaxValues(input)
        val (smallestXCoord, biggestXCoord, biggestYCoord) = minMaxCoords
        val width = biggestXCoord - smallestXCoord + 1
        val grid = createGrid(input, minMaxCoords)
        fun printGrid() {
            printGrid(grid, width)
        }
        println("Grid at start:")
        printGrid()
        var sandSettledCount = 0
        fun newSandCoord() = (500 - smallestXCoord to 0)
        var sandCoord = newSandCoord()
        grid[sandCoord.second][sandCoord.first] = SAND
        // keep sand falling until hit sand or rock
        while (true) {
            // check if below is abyss
            if (sandCoord.second + 1 > biggestYCoord) {
                break
            }
            val spaceBelow = grid[sandCoord.second + 1][sandCoord.first]
            when (spaceBelow) {
                AIR -> {
                    // keep falling
                    sandCoord = grid.moveSandInDirection(sandCoord, Direction.DOWN)
                }

                ROCK -> {
                    // hit a rock
                    // check if something to left, right and left down and right down of rock or out of bounds
                    if (sandCoord.first - 1 < 0 || sandCoord.first + 1 > biggestXCoord) {
                        // Out of bounds
                        break
                    }
                    val itemToLeft = grid[sandCoord.second][sandCoord.first - 1]
                    val itemToRight = grid[sandCoord.second][sandCoord.first + 1]
                    val itemToLeftDown = grid[sandCoord.second + 1][sandCoord.first - 1]
                    val itemToRightDown = grid[sandCoord.second + 1][sandCoord.first + 1]
                    if (itemToLeft != AIR && itemToRight != AIR && itemToLeftDown != AIR && itemToRightDown != AIR) {
                        // obstructions both sides
                        // must settle
                        grid[sandCoord.second][sandCoord.first] = SAND
                        sandSettledCount++
                        // reset sand
                        sandCoord = newSandCoord()
                        continue
                    }
                    // check to see if can fall left
                    // if so then fall left
                    when (itemToLeftDown) {
                        AIR -> {
                            // can fall left
                            sandCoord = grid.moveSandInDirection(sandCoord, Direction.LEFT_DOWN)
                            continue
                        }

                        ROCK,
                        SAND -> {
                            // rock or sand, cannot fall to left
                            // check right
                            when (itemToRightDown) {
                                AIR -> {
                                    // can fall right
                                    sandCoord = grid.moveSandInDirection(sandCoord, Direction.RIGHT_DOWN)
                                    continue
                                }

                                ROCK,
                                SAND -> {
                                    // rock or sand, cannot fall to right
                                    // sand has settled
                                    grid[sandCoord.second][sandCoord.first] = SAND
                                    sandSettledCount++
                                    // reset sand
                                    sandCoord = newSandCoord()
                                    continue
                                }
                            }
                        }
                    }
                }

                SAND -> {
                    // hit sand
                    // check to see if can fall left
                    // check if something to left and right of rock
                    if (sandCoord.first - 1 < 0 || sandCoord.first + 1 > biggestXCoord) {
                        // Out of bounds
                        break
                    }
                    val itemToLeft = grid[sandCoord.second][sandCoord.first - 1]
                    val itemToRight = grid[sandCoord.second][sandCoord.first + 1]
                    val itemToLeftDown = grid[sandCoord.second + 1][sandCoord.first - 1]
                    val itemToRightDown = grid[sandCoord.second + 1][sandCoord.first + 1]
                    if (itemToLeft != AIR && itemToRight != AIR && itemToLeftDown != AIR && itemToRightDown != AIR) {
                        // obstructions both sides
                        // must settle
                        grid[sandCoord.second][sandCoord.first] = SAND
                        sandSettledCount++
                        // reset sand
                        sandCoord = newSandCoord()
                        continue
                    }
                    // if so then fall left
                    when (itemToLeftDown) {
                        AIR -> {
                            // can fall left
                            sandCoord = grid.moveSandInDirection(sandCoord, Direction.LEFT_DOWN)
                            continue
                        }

                        ROCK,
                        SAND -> {
                            // rock or sand, cannot fall to left
                            // check right
                            when (itemToRightDown) {
                                AIR -> {
                                    // can fall right
                                    sandCoord = grid.moveSandInDirection(sandCoord, Direction.RIGHT_DOWN)
                                    continue
                                }

                                ROCK,
                                SAND -> {
                                    // rock or sand, cannot fall to right
                                    // sand has settled
                                    grid[sandCoord.second][sandCoord.first] = SAND
                                    sandSettledCount++
                                    // reset sand
                                    sandCoord = newSandCoord()
                                    continue
                                }
                            }
                        }
                    }
                }
            }
        }
        // check if can fall to left
        // if it can then fall to left, otherwise check if can fall to right, if it can then fall to right
        // keep going until cannot fall or is lower than any rock (optimisation: check if any rock lower in that column)
        println("Grid at end:")
        printGrid()
        return sandSettledCount
    }

    fun part2(input: List<String>): Int {
        val originalMinMaxValues = findMinMaxValues(input)
        val minMaxValues = originalMinMaxValues.copy(
            biggestYCoord = originalMinMaxValues.biggestYCoord + 2,
            smallestXCoord = originalMinMaxValues.smallestXCoord - originalMinMaxValues.biggestYCoord,
            biggestXCoord = originalMinMaxValues.biggestXCoord + originalMinMaxValues.biggestYCoord,
        )
        val minMaxCoords = minMaxValues
        val (smallestXCoord, biggestXCoord, _) = minMaxCoords
        val width = biggestXCoord - smallestXCoord + 1
        // '#' denotes rock, 'o' sand and '.' air
        val grid = createGrid(input, minMaxCoords).withFloor()
        fun printGrid() {
            printGrid(grid, width)
        }
        println("Grid at start:")
        printGrid()
        var sandSettledCount = 0
        val xStart = 500 - smallestXCoord
        fun newSandCoord() = (xStart to 0)
        var sandCoord = newSandCoord()
        grid[sandCoord.second][sandCoord.first] = SAND
        // keep sand falling until hit sand or rock
        while (true) {
            val spaceBelow = grid[sandCoord.second + 1][sandCoord.first]
            when (spaceBelow) {
                AIR -> {
                    // keep falling
                    sandCoord = grid.moveSandInDirection(sandCoord, Direction.DOWN)
                }

                ROCK -> {
                    // hit a rock
                    // check if something to left, right and left down and right down of rock or out of bounds
                    if (sandCoord.first - 1 < 0 || sandCoord.first + 1 > biggestXCoord) {
                        // Out of bounds
                        break
                    }
                    val itemToLeft = grid[sandCoord.second][sandCoord.first - 1]
                    val itemToRight = grid[sandCoord.second][sandCoord.first + 1]
                    val itemToLeftDown = grid[sandCoord.second + 1][sandCoord.first - 1]
                    val itemToRightDown = grid[sandCoord.second + 1][sandCoord.first + 1]
                    if (itemToLeft != AIR && itemToRight != AIR && itemToLeftDown != AIR && itemToRightDown != AIR) {
                        // obstructions both sides
                        // must settle
                        sandSettledCount++
                        // reset sand
                        if (grid[0][xStart - 1] == SAND) {
                            // already sand at top
                            break
                        }
                        sandCoord = newSandCoord()
                        continue
                    }
                    // check to see if can fall left
                    // if so then fall left
                    when (itemToLeftDown) {
                        AIR -> {
                            // can fall left
                            sandCoord = grid.moveSandInDirection(sandCoord, Direction.LEFT_DOWN)
                            continue
                        }

                        ROCK,
                        SAND -> {
                            // rock or sand, cannot fall to left
                            // check right
                            when (itemToRightDown) {
                                AIR -> {
                                    // can fall right
                                    sandCoord = grid.moveSandInDirection(sandCoord, Direction.RIGHT_DOWN)
                                    continue
                                }

                                ROCK,
                                SAND -> {
                                    // rock or sand, cannot fall to right
                                    // sand has settled
                                    grid[sandCoord.second][sandCoord.first] = SAND
                                    sandSettledCount++
                                    if (grid[0][xStart - 1] == SAND) {
                                        // already sand at top
                                        break
                                    }
                                    // reset sand
                                    sandCoord = newSandCoord()
                                    continue
                                }
                            }
                        }
                    }
                }

                SAND -> {
                    // hit sand
                    // check to see if can fall left
                    // check if something to left and right of rock
                    val itemToLeft = grid[sandCoord.second][sandCoord.first - 1]
                    val itemToRight = grid[sandCoord.second][sandCoord.first + 1]
                    val itemToLeftDown = grid[sandCoord.second + 1][sandCoord.first - 1]
                    val itemToRightDown = grid[sandCoord.second + 1][sandCoord.first + 1]
                    if (itemToLeft != AIR && itemToRight != AIR && itemToLeftDown != AIR && itemToRightDown != AIR) {
                        // obstructions both sides
                        // must settle
                        grid[sandCoord.second][sandCoord.first] = SAND
                        sandSettledCount++
                        // reset sand
                        sandCoord = newSandCoord()
                        continue
                    }
                    // check if sand is settling at the top
                    if (itemToLeftDown == SAND && itemToRightDown == SAND && sandCoord == newSandCoord()) {
                        sandSettledCount++
                        break
                    }
                    // if so then fall left
                    when (itemToLeftDown) {
                        AIR -> {
                            // can fall left
                            sandCoord = grid.moveSandInDirection(sandCoord, Direction.LEFT_DOWN)
                            continue
                        }

                        ROCK,
                        SAND -> {
                            // rock or sand, cannot fall to left
                            // check right
                            when (itemToRightDown) {
                                AIR -> {
                                    // can fall right
                                    sandCoord = grid.moveSandInDirection(sandCoord, Direction.RIGHT_DOWN)
                                    continue
                                }

                                ROCK,
                                SAND -> {
                                    // rock or sand, cannot fall to right
                                    // sand has settled
                                    grid[sandCoord.second][sandCoord.first] = SAND
                                    sandSettledCount++
                                    if (grid[0][xStart - 1] == SAND) {
                                        // already sand at top
                                        break
                                    }
                                    // reset sand
                                    sandCoord = newSandCoord()
                                    continue
                                }
                            }
                        }
                    }
                }
            }
        }
        // check if can fall to left
        // if it can then fall to left, otherwise check if can fall to right, if it can then fall to right
        // keep going until cannot fall or is lower than any rock (optimisation: check if any rock lower in that column)
        println("Grid at end:")
        printGrid()
        return sandSettledCount
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day14_test")
    check(part1(testInput) == 24)
    check(part2(testInput) == 93)

    val input = readInput("Day14")
    println(part1(input))
    println(part2(input))
}

private fun printGrid(grid: Grid, width: Int) {
    val divider = "*".repeat(width * 2)
    println(divider)
    grid.forEach { row ->
        val rowString = row.joinToString(" ")
        println(rowString)
    }
    println(divider)
}

data class MinMaxCoords(
    val smallestXCoord: Int,
    val biggestXCoord: Int,
    val biggestYCoord: Int,
)

private fun findMinMaxValues(input: List<String>): MinMaxCoords {
    var smallestXCoord = Int.MAX_VALUE
    var biggestXCoord = Int.MIN_VALUE
    var biggestYCoord = Int.MIN_VALUE
    input.forEach { line ->
        val coords = line.split(" -> ").map { it.split(",") }
        coords.forEach { (x, y) ->
            when {
                x.toInt() < smallestXCoord -> smallestXCoord = x.toInt()
                x.toInt() > biggestXCoord -> biggestXCoord = x.toInt()
                y.toInt() > biggestYCoord -> biggestYCoord = y.toInt()
            }
        }
    }
    return MinMaxCoords(smallestXCoord, biggestXCoord, biggestYCoord)
}

private fun createGrid(input: List<String>, minMaxCoords: MinMaxCoords): Grid {
    val (smallestXCoord, biggestXCoord, biggestYCoord) = minMaxCoords
    val grid = Array(biggestYCoord + 1) { CharArray(biggestXCoord - smallestXCoord + 1) { '.' } }
    input.forEach { line ->
        val coords = line.split(" -> ").map { it.split(",") }
        for (i in 1 until coords.size) {
            val (prevX, prevY) = coords[i - 1]
            val (currentX, currentY) = coords[i]
            if (prevX == currentX) {
                // moving on y axis
                if (prevY.toInt() < currentY.toInt()) {
                    for (j in currentY.toInt() downTo prevY.toInt()) {
                        grid[j][currentX.toInt() - smallestXCoord] = '#'
                    }
                } else {
                    for (j in currentY.toInt()..prevY.toInt()) {
                        grid[j][currentX.toInt() - smallestXCoord] = '#'
                    }
                }
            } else if (prevY == currentY) {
                // moving on x axis
                if (prevX.toInt() < currentX.toInt()) {
                    for (j in currentX.toInt() downTo prevX.toInt()) {
                        grid[currentY.toInt()][j - smallestXCoord] = '#'
                    }
                } else {
                    for (j in currentX.toInt()..prevX.toInt()) {
                        grid[currentY.toInt()][j - smallestXCoord] = '#'
                    }
                }
            }
        }
    }
    return grid
}

private fun Grid.withFloor(): Grid {
    val bottomRow = this[this.size - 1]
    for (i in bottomRow.indices) {
        this[this.size - 1][i] = '#'
    }
    return this
}

typealias Grid = Array<CharArray>
typealias SandCoord = Pair<Int, Int>

private fun Grid.moveSandInDirection(sandCoord: SandCoord, direction: Direction): SandCoord {
    return when (direction) {
        Direction.DOWN -> {
            val prevCoord = (sandCoord.first to sandCoord.second)
            val newSandCoord = (sandCoord.first to sandCoord.second + 1)
            // update grid with new sand
            this[newSandCoord.second][newSandCoord.first] = SAND
            // remove old position
            this[prevCoord.second][prevCoord.first] = AIR
            newSandCoord
        }

        Direction.LEFT_DOWN -> {
            val prevCoord = (sandCoord.first to sandCoord.second)
            val newSandCoord = (sandCoord.first - 1 to sandCoord.second + 1)
            // update grid with new sand
            this[newSandCoord.second][newSandCoord.first] = SAND
            // remove old position
            this[prevCoord.second][prevCoord.first] = AIR
            newSandCoord
        }

        Direction.RIGHT_DOWN -> {
            val prevCoord = (sandCoord.first to sandCoord.second)
            val newSandCoord = (sandCoord.first + 1 to sandCoord.second + 1)
            // update grid with new sand
            this[newSandCoord.second][newSandCoord.first] = SAND
            // remove old position
            this[prevCoord.second][prevCoord.first] = AIR
            newSandCoord
        }
    }
}

enum class Direction {
    DOWN,
    LEFT_DOWN,
    RIGHT_DOWN,
}

private const val AIR = '.'
private const val ROCK = '#'
private const val SAND = 'o'
