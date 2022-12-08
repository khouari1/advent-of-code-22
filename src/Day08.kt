fun main() {
    fun part1(input: List<String>): Int {
        val visibleTreeCoords = mutableSetOf<Pair<Int, Int>>()
        val treeGrid = createTreeGrid(input, visibleTreeCoords) { rowIndex, colIndex, maxRowSize, maxColSize ->
            rowIndex == 0 || colIndex == 0 || rowIndex == maxRowSize || colIndex == maxColSize
        }
        treeGrid.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { colIndex, col ->
                val currentTree = treeGrid[rowIndex][colIndex]
                // left
                var foundTallerTreeLeft = false
                for (i in colIndex - 1 downTo 0) {
                    val tree = treeGrid[rowIndex][i]
                    if (tree >= currentTree) {
                        foundTallerTreeLeft = true
                        break
                    }
                }
                if (!foundTallerTreeLeft) {
                    visibleTreeCoords.add(rowIndex to colIndex)
                    return@forEachIndexed
                }
                // right
                var foundTallerTreeRight = false
                for (i in colIndex + 1 until row.size) {
                    val tree = treeGrid[rowIndex][i]
                    if (tree >= currentTree) {
                        foundTallerTreeRight = true
                        break
                    }
                }
                if (!foundTallerTreeRight) {
                    visibleTreeCoords.add(rowIndex to colIndex)
                    return@forEachIndexed
                }
                // up
                var foundTallerTreeUp = false
                for (i in rowIndex - 1 downTo 0) {
                    val tree = treeGrid[i][colIndex]
                    if (tree >= currentTree) {
                        foundTallerTreeUp = true
                        break
                    }
                }
                if (!foundTallerTreeUp) {
                    visibleTreeCoords.add(rowIndex to colIndex)
                    return@forEachIndexed
                }
                // down
                var foundTallerTreeDown = false
                for (i in rowIndex + 1 until treeGrid.size) {
                    val tree = treeGrid[i][colIndex]
                    if (tree >= currentTree) {
                        foundTallerTreeDown = true
                        break
                    }
                }
                if (!foundTallerTreeDown) {
                    visibleTreeCoords.add(rowIndex to colIndex)
                    return@forEachIndexed
                }
            }
        }
        return visibleTreeCoords.size
    }

    fun part2(input: List<String>): Int {
        val treeGrid = createTreeGrid(input)
        var topScenicScore = 0

        treeGrid.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { colIndex, col ->
                val currentTree = treeGrid[rowIndex][colIndex]

                var leftScore = 0
                var rightScore = 0
                var upScore = 0
                var downScore = 0

                // left
                if (colIndex != 0) {
                    for (i in colIndex - 1 downTo 0) {
                        leftScore++
                        val tree = treeGrid[rowIndex][i]
                        if (tree >= currentTree) {
                            break
                        }
                    }
                }
                // right
                if (colIndex != row.size - 1) {
                    for (i in colIndex + 1 until row.size) {
                        rightScore++
                        val tree = treeGrid[rowIndex][i]
                        if (tree >= currentTree) {
                            break
                        }
                    }
                }
                // up
                if (rowIndex != 0) {
                    for (i in rowIndex - 1 downTo 0) {
                        upScore++
                        val tree = treeGrid[i][colIndex]
                        if (tree >= currentTree) {
                            break
                        }
                    }
                }
                // down
                if (rowIndex != treeGrid.size - 1) {
                    for (i in rowIndex + 1 until treeGrid.size) {
                        downScore++
                        val tree = treeGrid[i][colIndex]
                        if (tree >= currentTree) {
                            break
                        }
                    }
                }

                val totalScore = leftScore * rightScore * upScore * downScore
                if (totalScore > topScenicScore) {
                    topScenicScore = totalScore
                }
            }
        }
        return topScenicScore
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    check(part1(testInput) == 21)
    check(part2(testInput) == 8)

    val input = readInput("Day08")
    println(part1(input))
    println(part2(input))
}

private fun createTreeGrid(
    input: List<String>,
    coords: MutableSet<Pair<Int, Int>> = mutableSetOf(),
    addCoordsPredicate: ((rowIndex: Int, colIndex: Int, maxRowSize: Int, maxColSize: Int) -> Boolean)? = null
): Array<IntArray> {
    val treeGrid = Array(input[0].length) { IntArray(input.size) }
    input.forEachIndexed { rowIndex, line ->
        val chars = line.toCharArray()
        chars.forEachIndexed { colIndex, c ->
            treeGrid[rowIndex][colIndex] = c.digitToInt()
            if (addCoordsPredicate?.invoke(rowIndex, colIndex, input.size - 1, chars.size - 1) == true) {
                coords.add(rowIndex to colIndex)
            }
        }
    }
    return treeGrid
}
