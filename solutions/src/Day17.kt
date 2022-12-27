fun main() {
    fun part1(input: List<String>): Int {
        val tetrisGrid = TetrisGrid()
        val directions = mutableListOf<TetrisDirection>()
        val directionInput = input.flatMap { it.split("") }.filter { it.isNotBlank() }
        directionInput.forEach { c ->
            directions.add(c.toDirection())
            directions.add(TetrisDirection.DOWN)
        }
        var directionIndex = 0
        var shape = ShapeType.HORIZONTAL_LINE
        tetrisGrid.addShape(shape)
        while (tetrisGrid.numberOfShapes < 2023) {
            val nextDirection = directions[directionIndex]
            if (directionIndex == directions.size - 1) {
                directionIndex = 0
            } else {
                directionIndex++
            }
            when (tetrisGrid.move(nextDirection)) {
                MoveResult.SUCCESS -> {
                    // continue
                }

                MoveResult.BLOCKED -> {
                    // next shape
                    shape = shape.getNext()
                    tetrisGrid.addShape(shape)
                    continue
                }
            }
        }

        tetrisGrid.print()
        return tetrisGrid.grid.count { it.contains('#') }
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day17_test")
    check(part1(testInput) == 3068)
//    check(part2(testInput) == 0)

    val input = readInput("Day17")
    println(part1(input))
//    println(part2(input))
}

fun String.toDirection(): TetrisDirection {
    return when (this) {
        ">" -> TetrisDirection.RIGHT
        "<" -> TetrisDirection.LEFT
        else -> throw Exception("Unexpected direction string $this")
    }
}

class TetrisGrid {
    var grid = mutableListOf<MutableList<Char>>()
    var numberOfShapes: Int = 0
    private lateinit var currentShape: Shape
    private var currentRow: Int = 0

    init {
        // bottom
        grid.add(mutableListOf('-', '-', '-', '-', '-', '-', '-'))
    }

    fun print() {
        println("*".repeat(grid[0].size))
        grid.reversed().forEach { row ->
            println(row)
        }
        println("*".repeat(grid[0].size))
    }

    private fun addEmptyRows(rows: Int) {
        for (i in 0 until rows) {
            grid.add(mutableListOf('.', '.', '.', '.', '.', '.', '.'))
        }
    }

    fun addShape(shape: ShapeType) {
        // add shape to grid with 3 empty rows between it and bottom
        addEmptyRows(3)
        when (shape) {
            ShapeType.HORIZONTAL_LINE -> {
                grid.add(mutableListOf('.', '.', '@', '@', '@', '@', '.'))
                currentShape = Shape.HorizontalLine(2, 5)
                currentRow = grid.size - 1
            }

            ShapeType.CROSS -> {
                grid.add(mutableListOf('.', '.', '.', '@', '.', '.', '.'))
                grid.add(mutableListOf('.', '.', '@', '@', '@', '.', '.'))
                grid.add(mutableListOf('.', '.', '.', '@', '.', '.', '.'))
                currentShape = Shape.Cross(3, 2, 4, 3)
                currentRow = grid.size - 3
            }

            ShapeType.REVERSE_L -> {
                grid.add(mutableListOf('.', '.', '@', '@', '@', '.', '.'))
                grid.add(mutableListOf('.', '.', '.', '.', '@', '.', '.'))
                grid.add(mutableListOf('.', '.', '.', '.', '@', '.', '.'))
                currentShape = Shape.ReverseL(4, 4, 2, 4)
                currentRow = grid.size - 3
            }

            ShapeType.VERTICAL_LINE -> {
                grid.add(mutableListOf('.', '.', '@', '.', '.', '.', '.'))
                grid.add(mutableListOf('.', '.', '@', '.', '.', '.', '.'))
                grid.add(mutableListOf('.', '.', '@', '.', '.', '.', '.'))
                grid.add(mutableListOf('.', '.', '@', '.', '.', '.', '.'))
                currentShape = Shape.VerticalLine(2, 2, 2, 2)
                currentRow = grid.size - 4
            }

            ShapeType.SQUARE -> {
                grid.add(mutableListOf('.', '.', '@', '@', '.', '.', '.'))
                grid.add(mutableListOf('.', '.', '@', '@', '.', '.', '.'))
                currentShape = Shape.Square(2, 3, 2, 3)
                currentRow = grid.size - 2
            }
        }
        numberOfShapes++
    }

    private fun Char.isBlocked(): Boolean {
        return when (this) {
            '-',
            '#' -> true

            else -> false
        }
    }

    private fun prune() {
        var index = grid.size - 1
        while (!grid[index].contains('@') && !grid[index].contains('#')) {
            grid.removeAt(index)
            index--
        }
    }

    fun move(direction: TetrisDirection): MoveResult {
        val shape = currentShape
        val moveResult = when (direction) {
            TetrisDirection.LEFT -> {
                when (shape) {
                    is Shape.HorizontalLine -> {
                        val line = grid[currentRow]
                        val firstIndex = shape.leftMost
                        if (firstIndex == 0 || line[firstIndex - 1].isBlocked()) {
                            // do nothing
                        } else {
                            // move to left
                            line[firstIndex - 1] = '@'
                            line[shape.rightMost] = '.'
                            currentShape = currentShape.moveLeft()
                        }
                        MoveResult.SUCCESS
                    }

                    is Shape.Cross -> {
                        val lines = grid.subList(currentRow, currentRow + 3)
                        val firstTopIndex = shape.top
                        val firstMiddleIndex = shape.middleLeftMost
                        val firstBottomIndex = shape.bottom
                        if (firstMiddleIndex == 0 || lines[1][firstMiddleIndex - 1].isBlocked() ||
                            lines[2][firstTopIndex - 1].isBlocked() ||
                            lines[0][firstBottomIndex - 1].isBlocked()
                        ) {
                            // do nothing
                        } else {
                            // move to left
                            lines[2][firstTopIndex] = '.'
                            lines[2][firstTopIndex - 1] = '@'
                            lines[1][firstMiddleIndex - 1] = '@'
                            lines[1][shape.middleRightMost] = '.'
                            lines[0][firstBottomIndex - 1] = '@'
                            lines[0][firstBottomIndex] = '.'
                            currentShape = currentShape.moveLeft()
                        }
                        MoveResult.SUCCESS
                    }

                    is Shape.ReverseL -> {
                        val lines = grid.subList(currentRow, currentRow + 3)
                        val firstTopIndex = shape.top
                        val firstMiddleIndex = shape.middle
                        val firstBottomIndex = shape.bottomLeftMost
                        if (firstBottomIndex == 0 || lines[0][firstBottomIndex - 1].isBlocked() ||
                            lines[1][firstMiddleIndex - 1].isBlocked() ||
                            lines[2][firstTopIndex - 1].isBlocked()
                        ) {
                            // do nothing
                        } else {
                            // move to left
                            lines[0][firstBottomIndex - 1] = '@'
                            lines[0][shape.bottomRightMost] = '.'
                            lines[1][firstMiddleIndex - 1] = '@'
                            lines[1][firstMiddleIndex] = '.'
                            lines[2][firstTopIndex - 1] = '@'
                            lines[2][firstTopIndex] = '.'
                            currentShape = currentShape.moveLeft()
                        }
                        MoveResult.SUCCESS
                    }

                    is Shape.VerticalLine -> {
                        val lines = grid.subList(currentRow, currentRow + 4)
                        val firstIndex = shape.top
                        if (firstIndex == 0 || lines[0][firstIndex - 1].isBlocked() ||
                            lines[1][firstIndex - 1].isBlocked() ||
                            lines[2][firstIndex - 1].isBlocked() ||
                            lines[3][firstIndex - 1].isBlocked()
                        ) {
                            // do nothing
                        } else {
                            // move to left
                            lines[0][firstIndex] = '.'
                            lines[1][firstIndex] = '.'
                            lines[2][firstIndex] = '.'
                            lines[3][firstIndex] = '.'

                            lines[0][firstIndex - 1] = '@'
                            lines[1][firstIndex - 1] = '@'
                            lines[2][firstIndex - 1] = '@'
                            lines[3][firstIndex - 1] = '@'
                            currentShape = currentShape.moveLeft()
                        }
                        MoveResult.SUCCESS
                    }

                    is Shape.Square -> {
                        val lines = grid.subList(currentRow, currentRow + 2)
                        val firstIndex = shape.topLeft
                        if (firstIndex == 0 || lines[0][firstIndex - 1].isBlocked() ||
                            lines[1][firstIndex - 1].isBlocked()
                        ) {
                            // do nothing
                        } else {
                            // move to left
                            lines[0][firstIndex - 1] = '@'
                            lines[1][firstIndex - 1] = '@'
                            lines[0][shape.topRight] = '.'
                            lines[1][shape.bottomRight] = '.'
                            currentShape = currentShape.moveLeft()
                        }
                        MoveResult.SUCCESS
                    }
                }
            }

            TetrisDirection.RIGHT -> {
                val lastRowIndex = 6
                when (shape) {
                    is Shape.HorizontalLine -> {
                        val line = grid[currentRow]
                        val lastIndex = shape.rightMost
                        if (lastIndex == lastRowIndex || line[lastIndex + 1].isBlocked()) {
                            // do nothing
                        } else {
                            // move to right
                            line[lastIndex + 1] = '@'
                            line[shape.leftMost] = '.'
                            currentShape = currentShape.moveRight()
                        }
                        MoveResult.SUCCESS
                    }

                    is Shape.Cross -> {
                        val lines = grid.subList(currentRow, currentRow + 3)
                        val lastTopIndex = shape.top
                        val lastMiddleIndex = shape.middleRightMost
                        val lastBottomIndex = shape.bottom
                        if (lastMiddleIndex == lastRowIndex || lines[1][lastMiddleIndex + 1].isBlocked() ||
                            lines[2][lastBottomIndex + 1].isBlocked() ||
                            lines[0][lastTopIndex + 1].isBlocked()
                        ) {
                            // do nothing
                        } else {
                            // move to right
                            lines[2][lastTopIndex + 1] = '@'
                            lines[2][lastTopIndex] = '.'
                            lines[1][lastMiddleIndex + 1] = '@'
                            lines[1][shape.middleLeftMost] = '.'
                            lines[0][lastBottomIndex + 1] = '@'
                            lines[0][lastBottomIndex] = '.'
                            currentShape = currentShape.moveRight()
                        }
                        MoveResult.SUCCESS
                    }

                    is Shape.ReverseL -> {
                        val lines = grid.subList(currentRow, currentRow + 3)
                        val lastTopIndex = shape.top
                        val lastMiddleIndex = shape.middle
                        val lastBottomIndex = shape.bottomRightMost
                        if (lastBottomIndex == lastRowIndex || lines[0][lastBottomIndex + 1].isBlocked() ||
                            lines[1][lastMiddleIndex + 1].isBlocked() ||
                            lines[2][lastTopIndex + 1].isBlocked()
                        ) {
                            // do nothing
                        } else {
                            // move to right
                            lines[0][lastBottomIndex + 1] = '@'
                            lines[0][shape.bottomLeftMost] = '.'
                            lines[1][lastMiddleIndex + 1] = '@'
                            lines[1][lastMiddleIndex] = '.'
                            lines[2][lastTopIndex + 1] = '@'
                            lines[2][lastTopIndex] = '.'
                            currentShape = currentShape.moveRight()
                        }
                        MoveResult.SUCCESS
                    }

                    is Shape.VerticalLine -> {
                        val lines = grid.subList(currentRow, currentRow + 4)
                        val lastIndex = shape.top
                        if (lastIndex == lastRowIndex || lines[0][lastIndex + 1].isBlocked() ||
                            lines[1][lastIndex + 1].isBlocked() ||
                            lines[2][lastIndex + 1].isBlocked() ||
                            lines[3][lastIndex + 1].isBlocked()
                        ) {
                            // do nothing
                        } else {
                            // move to right
                            lines[0][lastIndex] = '.'
                            lines[1][lastIndex] = '.'
                            lines[2][lastIndex] = '.'
                            lines[3][lastIndex] = '.'

                            lines[0][lastIndex + 1] = '@'
                            lines[1][lastIndex + 1] = '@'
                            lines[2][lastIndex + 1] = '@'
                            lines[3][lastIndex + 1] = '@'
                            currentShape = currentShape.moveRight()
                        }
                        MoveResult.SUCCESS
                    }

                    is Shape.Square -> {
                        val lines = grid.subList(currentRow, currentRow + 2)
                        val firstIndex = shape.bottomRight
                        if (firstIndex == lastRowIndex || lines[0][firstIndex + 1].isBlocked() ||
                            lines[1][firstIndex + 1].isBlocked()
                        ) {
                            // do nothing
                        } else {
                            // move to right
                            lines[0][firstIndex + 1] = '@'
                            lines[1][firstIndex + 1] = '@'
                            lines[0][shape.topLeft] = '.'
                            lines[1][shape.bottomLeft] = '.'
                            currentShape = currentShape.moveRight()
                        }
                        MoveResult.SUCCESS
                    }
                }
            }

            TetrisDirection.DOWN -> {
                when (shape) {
                    is Shape.HorizontalLine -> {
                        val line = grid[currentRow]
                        val firstIndex = shape.leftMost
                        val lineBelow = grid[currentRow - 1]
                        if (lineBelow[firstIndex].isBlocked() ||
                            lineBelow[firstIndex + 1].isBlocked() ||
                            lineBelow[firstIndex + 2].isBlocked() ||
                            lineBelow[firstIndex + 3].isBlocked()
                        ) {
                            // blocked
                            line[firstIndex] = '#'
                            line[firstIndex + 1] = '#'
                            line[firstIndex + 2] = '#'
                            line[firstIndex + 3] = '#'
                            MoveResult.BLOCKED
                        } else {
                            // can move down
                            line[firstIndex] = '.'
                            line[firstIndex + 1] = '.'
                            line[firstIndex + 2] = '.'
                            line[firstIndex + 3] = '.'

                            lineBelow[firstIndex] = '@'
                            lineBelow[firstIndex + 1] = '@'
                            lineBelow[firstIndex + 2] = '@'
                            lineBelow[firstIndex + 3] = '@'
                            currentRow--
                            MoveResult.SUCCESS
                        }
                    }

                    is Shape.Cross -> {
                        val lines = grid.subList(currentRow, currentRow + 3)
                        val firstIndex = shape.bottom
                        val lineBelow = grid[currentRow - 1]
                        if (lineBelow[firstIndex].isBlocked() ||
                            lines[0][firstIndex - 1].isBlocked() ||
                            lines[0][firstIndex + 1].isBlocked()
                        ) {
                            lines[0][firstIndex] = '#'
                            lines[1][firstIndex] = '#'
                            lines[1][firstIndex - 1] = '#'
                            lines[1][firstIndex + 1] = '#'
                            lines[2][firstIndex] = '#'
                            MoveResult.BLOCKED
                        } else {
                            // can move down
                            lines[0][firstIndex] = '.'
                            lines[1][firstIndex] = '.'
                            lines[1][firstIndex - 1] = '.'
                            lines[1][firstIndex + 1] = '.'
                            lines[2][firstIndex] = '.'

                            lineBelow[firstIndex] = '@'
                            lines[0][firstIndex] = '@'
                            lines[0][firstIndex - 1] = '@'
                            lines[0][firstIndex + 1] = '@'
                            lines[1][firstIndex] = '@'
                            currentRow--
                            MoveResult.SUCCESS
                        }
                    }

                    is Shape.ReverseL -> {
                        val lines = grid.subList(currentRow, currentRow + 3)
                        val firstIndex = shape.bottomLeftMost
                        val lineBelow = grid[currentRow - 1]
                        if (lineBelow[firstIndex].isBlocked() ||
                            lineBelow[firstIndex + 1].isBlocked() ||
                            lineBelow[firstIndex + 2].isBlocked()
                        ) {
                            lines[0][firstIndex] = '#'
                            lines[0][firstIndex + 1] = '#'
                            lines[0][firstIndex + 2] = '#'
                            lines[1][firstIndex + 2] = '#'
                            lines[2][firstIndex + 2] = '#'
                            MoveResult.BLOCKED
                        } else {
                            // can move down
                            lines[0][firstIndex] = '.'
                            lines[0][firstIndex + 1] = '.'
                            lines[0][firstIndex + 2] = '.'
                            lines[1][firstIndex + 2] = '.'
                            lines[2][firstIndex + 2] = '.'

                            lineBelow[firstIndex] = '@'
                            lineBelow[firstIndex + 1] = '@'
                            lineBelow[firstIndex + 2] = '@'
                            lines[0][firstIndex + 2] = '@'
                            lines[1][firstIndex + 2] = '@'
                            currentRow--
                            MoveResult.SUCCESS
                        }
                    }

                    is Shape.VerticalLine -> {
                        val lines = grid.subList(currentRow, currentRow + 4)
                        val firstIndex = shape.bottom
                        val lineBelow = grid[currentRow - 1]
                        if (lineBelow[firstIndex].isBlocked()) {
                            lines[0][firstIndex] = '#'
                            lines[1][firstIndex] = '#'
                            lines[2][firstIndex] = '#'
                            lines[3][firstIndex] = '#'
                            MoveResult.BLOCKED
                        } else {
                            // can move down
                            lines[3][firstIndex] = '.'
                            lineBelow[firstIndex] = '@'
                            currentRow--
                            MoveResult.SUCCESS
                        }
                    }

                    is Shape.Square -> {
                        val lines = grid.subList(currentRow, currentRow + 2)
                        val firstIndex = shape.bottomLeft
                        val lineBelow = grid[currentRow - 1]
                        if (lineBelow[firstIndex].isBlocked() ||
                            lineBelow[firstIndex + 1].isBlocked()
                        ) {
                            lines[0][firstIndex] = '#'
                            lines[0][firstIndex + 1] = '#'
                            lines[1][firstIndex] = '#'
                            lines[1][firstIndex + 1] = '#'
                            MoveResult.BLOCKED
                        } else {
                            // can move down
                            lines[0][firstIndex] = '.'
                            lines[0][firstIndex + 1] = '.'
                            lines[1][firstIndex] = '.'
                            lines[1][firstIndex + 1] = '.'

                            lineBelow[firstIndex] = '@'
                            lineBelow[firstIndex + 1] = '@'
                            lines[0][firstIndex] = '@'
                            lines[0][firstIndex + 1] = '@'
                            currentRow--
                            MoveResult.SUCCESS
                        }
                    }
                }
            }
        }
        prune()
        return moveResult
    }
}

private fun ShapeType.getNext(): ShapeType {
    return when (this) {
        ShapeType.HORIZONTAL_LINE -> ShapeType.CROSS
        ShapeType.CROSS -> ShapeType.REVERSE_L
        ShapeType.REVERSE_L -> ShapeType.VERTICAL_LINE
        ShapeType.VERTICAL_LINE -> ShapeType.SQUARE
        ShapeType.SQUARE -> ShapeType.HORIZONTAL_LINE
    }
}

enum class ShapeType {
    HORIZONTAL_LINE,
    CROSS,
    REVERSE_L,
    VERTICAL_LINE,
    SQUARE,
}

sealed class Shape {
    abstract fun moveLeft(): Shape
    abstract fun moveRight(): Shape

    data class HorizontalLine(
        val leftMost: Int,
        val rightMost: Int,
    ) : Shape() {
        override fun moveLeft(): HorizontalLine {
            return this.copy(leftMost = leftMost - 1, rightMost = rightMost - 1)
        }

        override fun moveRight(): HorizontalLine {
            return this.copy(leftMost = leftMost + 1, rightMost = rightMost + 1)
        }
    }

    data class Cross(
        val top: Int,
        val middleLeftMost: Int,
        val middleRightMost: Int,
        val bottom: Int,
    ) : Shape() {
        override fun moveLeft(): Cross {
            return this.copy(
                top = top - 1,
                middleLeftMost = middleLeftMost - 1,
                middleRightMost = middleRightMost - 1,
                bottom = bottom - 1,
            )
        }

        override fun moveRight(): Cross {
            return this.copy(
                top = top + 1,
                middleLeftMost = middleLeftMost + 1,
                middleRightMost = middleRightMost + 1,
                bottom = bottom + 1,
            )
        }
    }

    data class ReverseL(
        val top: Int,
        val middle: Int,
        val bottomLeftMost: Int,
        val bottomRightMost: Int,
    ) : Shape() {
        override fun moveLeft(): ReverseL {
            return this.copy(
                top = top - 1,
                middle = middle - 1,
                bottomLeftMost = bottomLeftMost - 1,
                bottomRightMost = bottomRightMost - 1,
            )
        }

        override fun moveRight(): ReverseL {
            return this.copy(
                top = top + 1,
                middle = middle + 1,
                bottomLeftMost = bottomLeftMost + 1,
                bottomRightMost = bottomRightMost + 1,
            )
        }
    }

    data class VerticalLine(
        val top: Int,
        val topMiddle: Int,
        val bottomMiddle: Int,
        val bottom: Int,
    ) : Shape() {
        override fun moveLeft(): VerticalLine {
            return this.copy(
                top = top - 1,
                topMiddle = topMiddle - 1,
                bottomMiddle = bottomMiddle - 1,
                bottom = bottom - 1,
            )
        }

        override fun moveRight(): VerticalLine {
            return this.copy(
                top = top + 1,
                topMiddle = topMiddle + 1,
                bottomMiddle = bottomMiddle + 1,
                bottom = bottom + 1,
            )
        }
    }

    data class Square(
        val topLeft: Int,
        val topRight: Int,
        val bottomLeft: Int,
        val bottomRight: Int,
    ) : Shape() {
        override fun moveLeft(): Square {
            return this.copy(
                topLeft = topLeft - 1,
                topRight = topRight - 1,
                bottomLeft = bottomLeft - 1,
                bottomRight = bottomRight - 1,
            )
        }

        override fun moveRight(): Square {
            return this.copy(
                topLeft = topLeft + 1,
                topRight = topRight + 1,
                bottomLeft = bottomLeft + 1,
                bottomRight = bottomRight + 1,
            )
        }
    }
}

enum class TetrisDirection {
    LEFT,
    RIGHT,
    DOWN,
}

enum class MoveResult {
    SUCCESS,
    BLOCKED,
}
