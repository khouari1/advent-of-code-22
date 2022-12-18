import kotlinx.browser.document
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.dom.clear
import org.w3c.dom.*
import react.dom.client.createRoot

fun main() {
    val inputElement = document.createElement<HTMLTextAreaElement>("textarea")
    inputElement.value = """
        498,4 -> 498,6 -> 496,6
        503,4 -> 502,4 -> 502,9 -> 494,9
    """.trimIndent()

    val button = document.createElement<HTMLButtonElement>("button")
    button.textContent = "Run"

    val result = document.createElement("div")
    val container = document.createElement("div")
    document.body!!.appendChild(container)

    container.appendChild(inputElement)
    container.appendChild(button)
    container.appendChild(result)
    val gridElement = document.createElement("div")
    container.appendChild(gridElement)
    createRoot(container)

    button.onclick = {

        gridElement.clear()

        val input = inputElement.value.split("\n")

        val originalMinMaxValues = findMinMaxValues(input)
        val minMaxValues = originalMinMaxValues.copy(
            biggestYCoord = originalMinMaxValues.biggestYCoord + 2,
            smallestXCoord = originalMinMaxValues.smallestXCoord - originalMinMaxValues.biggestYCoord,
            biggestXCoord = originalMinMaxValues.biggestXCoord + originalMinMaxValues.biggestYCoord,
        )
        val (smallestXCoord, biggestXCoord, _) = minMaxValues
        // '#' denotes rock and 'o' sand

        val grid = createGrid(input, minMaxValues, gridElement).withFloor()

        MainScope().launch {

            var sandSettledCount = 0
            val xStart = 500 - smallestXCoord
            fun newSandCoord() = (xStart to 0)
            var sandCoord = newSandCoord()
            grid[sandCoord.second][sandCoord.first] = SAND

            val sandCell = document.getHTMLElementById("cell-${sandCoord.first}:${sandCoord.second}")
            sandCell.textContent = SAND_STRING
            sandCell.style.left = "${sandCoord.first}px"
            sandCell.style.top = "${sandCoord.second}px"

            // keep sand falling until hit sand or rock
            while (true) {
                delay(1)

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
                            val newSandCell = document.getHTMLElementById("cell-${500 - smallestXCoord}:0")
                            newSandCell.textContent = SAND_STRING
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
                                            val newSandCell = document.getHTMLElementById("cell-${500 - smallestXCoord}:0")
                                            newSandCell.textContent = SAND_STRING
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
            result.textContent = "Result = $sandSettledCount"
        }
    }
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

private fun createGrid(input: List<String>, minMaxCoords: MinMaxCoords, gridElement: Element): Grid {
    val (smallestXCoord, biggestXCoord, biggestYCoord) = minMaxCoords
    val grid = Array(biggestYCoord + 1) { CharArray(biggestXCoord - smallestXCoord + 1) { '.' } }

    grid.forEachIndexed { rowIndex, row ->
        val rowElement = document.createHTMLElement("div")
        rowElement.classList.add("row")
        rowElement.id = "row-$rowIndex"
        rowElement.style.display = "flex"
        row.forEachIndexed { cellIndex, _ ->
            val cellElement = document.createHTMLElement("div")
            cellElement.id = "cell-${cellIndex}:$rowIndex"

            cellElement.style.left = "${cellIndex}px"
            cellElement.style.top = "${rowIndex}px"
            cellElement.style.width = "1.2rem"
            cellElement.style.height = "1.2rem"
            rowElement.appendChild(cellElement)
        }
        gridElement.appendChild(rowElement)
    }

    input.forEach { line ->
        val coords = line.split(" -> ").map { it.split(",") }
        for (i in 1 until coords.size) {
            val (prevX, prevY) = coords[i - 1]
            val (currentX, currentY) = coords[i]
            val i1 = currentX.toInt() - smallestXCoord

            if (prevX == currentX) {
                // moving on y axis
                if (prevY.toInt() < currentY.toInt()) {
                    for (j in currentY.toInt() downTo prevY.toInt()) {
                        grid[j][currentX.toInt() - smallestXCoord] = ROCK
                        val cell = document.getHTMLElementById("cell-${currentX.toInt() - smallestXCoord}:$j")
                        cell.classList.add("rock")
                        cell.textContent = ROCK_STRING
                    }
                } else {
                    for (j in currentY.toInt()..prevY.toInt()) {
                        grid[j][currentX.toInt() - smallestXCoord] = ROCK
                        val cell = document.getHTMLElementById("cell-${currentX.toInt() - smallestXCoord}:$j")
                        cell.classList.add("rock")
                        cell.textContent = ROCK_STRING
                    }
                }
            } else if (prevY == currentY) {
                // moving on x axis
                if (prevX.toInt() < currentX.toInt()) {
                    for (j in currentX.toInt() downTo prevX.toInt()) {
                        grid[currentY.toInt()][j - smallestXCoord] = ROCK
                        val cell = document.getHTMLElementById("cell-${j - smallestXCoord}:${currentY.toInt()}")
                        cell.classList.add("rock")
                        cell.textContent = ROCK_STRING
                    }
                } else {
                    for (j in currentX.toInt()..prevX.toInt()) {
                        grid[currentY.toInt()][j - smallestXCoord] = ROCK
                        val cell = document.getHTMLElementById("cell-${j - smallestXCoord}:${currentY.toInt()}")
                        cell.classList.add("rock")
                        cell.textContent = ROCK_STRING
                    }
                }
            }
        }
    }
    val cell = document.getElementById("cell-${500 - smallestXCoord}:0") as HTMLElement
    cell.classList.add("start")
    cell.textContent = "+"
    return grid
}

fun Document.getHTMLElementById(id: String) = document.getElementById(id) as HTMLElement
fun Document.createHTMLElement(type: String) = document.createElement<HTMLElement>(type)
fun <T : Element> Document.createElement(type: String) = document.createElement(type) as T

private fun Grid.withFloor(): Grid {
    val bottomRow = this[this.size - 1]
    for (i in bottomRow.indices) {
        val x = this.size - 1
        this[x][i] = ROCK
        val cell = document.getHTMLElementById("cell-${i}:$x")
        cell.classList.add("rock")
        cell.textContent = ROCK_STRING
    }
    return this
}

typealias Grid = Array<CharArray>
typealias SandCoord = Pair<Int, Int>

private fun Grid.moveSandInDirection(
    sandCoord: SandCoord,
    direction: Direction,
): SandCoord {
    val prevPosition = document.getHTMLElementById("cell-${sandCoord.first}:${sandCoord.second}")
    prevPosition.textContent = if (sandCoord.second == 0) {
        "+"
    } else {
        null
    }

    val pair = when (direction) {
        Direction.DOWN -> {
            val prevCoord = (sandCoord.first to sandCoord.second)
            val newSandCoord = (sandCoord.first to sandCoord.second + 1)
            // update grid with new sand
            this[newSandCoord.second][newSandCoord.first] = SAND
            // remove old position
            this[prevCoord.second][prevCoord.first] = AIR

            val newSandCell = document.getHTMLElementById("cell-${newSandCoord.first}:${newSandCoord.second}")

            newSandCell.textContent = SAND_STRING
            newSandCoord
        }

        Direction.LEFT_DOWN -> {
            val prevCoord = (sandCoord.first to sandCoord.second)
            val newSandCoord = (sandCoord.first - 1 to sandCoord.second + 1)
            // update grid with new sand
            this[newSandCoord.second][newSandCoord.first] = SAND
            // remove old position
            this[prevCoord.second][prevCoord.first] = AIR

            val newSandCell = document.getHTMLElementById("cell-${newSandCoord.first}:${newSandCoord.second}")
            newSandCell.textContent = SAND_STRING
            newSandCoord
        }

        Direction.RIGHT_DOWN -> {
            val prevCoord = (sandCoord.first to sandCoord.second)
            val newSandCoord = (sandCoord.first + 1 to sandCoord.second + 1)
            // update grid with new sand
            this[newSandCoord.second][newSandCoord.first] = SAND
            // remove old position
            this[prevCoord.second][prevCoord.first] = AIR

            val newSandCell = document.getHTMLElementById("cell-${newSandCoord.first}:${newSandCoord.second}")
            newSandCell.textContent = SAND_STRING
            newSandCoord
        }
    }
    return pair
}

enum class Direction {
    DOWN,
    LEFT_DOWN,
    RIGHT_DOWN,
}

private const val AIR = '.'
private const val ROCK = '#'
private const val SAND = 'o'
private const val ROCK_STRING = " # "
private const val SAND_STRING = " o "
