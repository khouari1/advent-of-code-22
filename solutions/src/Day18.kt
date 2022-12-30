fun main() {
    fun part1(input: List<String>): Int {
        val coords = toCoords(input)
        var count = 0
        coords.forEach { (x, y, z) ->
            val front = CubeCoord(x, y, z - 1)
            if (front !in coords) {
                count++
            }
            val back = CubeCoord(x, y, z + 1)
            if (back !in coords) {
                count++
            }
            val top = CubeCoord(x, y - 1, z)
            if (top !in coords) {
                count++
            }
            val bottom = CubeCoord(x, y + 1, z)
            if (bottom !in coords) {
                count++
            }
            val left = CubeCoord(x - 1, y, z)
            if (left !in coords) {
                count++
            }
            val right = CubeCoord(x + 1, y, z)
            if (right !in coords) {
                count++
            }
        }
        return count
    }

    fun part2(input: List<String>): Int {
        val coords = toCoords(input)
        var count = 0
        val knownAirPocketCubes = mutableSetOf<CubeCoord>()
        coords.forEach { (x, y, z) ->
            val front = CubeCoord(x, y, z - 1)
            if (front !in knownAirPocketCubes) {
                val frontInAirPocket = isAirPocket(front, coords, knownAirPocketCubes)
                if (!frontInAirPocket && front !in coords) {
                    count++
                }
            }
            val back = CubeCoord(x, y, z + 1)
            if (back !in knownAirPocketCubes) {
                val backInAirPocket = isAirPocket(back, coords, knownAirPocketCubes)
                if (!backInAirPocket && back !in coords) {
                    count++
                }
            }
            val top = CubeCoord(x, y - 1, z)
            if (top !in knownAirPocketCubes) {
                val topInAirPocket = isAirPocket(top, coords, knownAirPocketCubes)
                if (!topInAirPocket && top !in coords) {
                    count++
                }
            }
            val bottom = CubeCoord(x, y + 1, z)
            if (bottom !in knownAirPocketCubes) {
                val bottomInAirPocket = isAirPocket(bottom, coords, knownAirPocketCubes)
                if (!bottomInAirPocket && bottom !in coords) {
                    count++
                }
            }
            val left = CubeCoord(x - 1, y, z)
            if (left !in knownAirPocketCubes) {
                val leftInAirPocket = isAirPocket(left, coords, knownAirPocketCubes)
                if (!leftInAirPocket && left !in coords) {
                    count++
                }
            }
            val right = CubeCoord(x + 1, y, z)
            if (right !in knownAirPocketCubes) {
                val rightInAirPocket = isAirPocket(right, coords, knownAirPocketCubes)
                if (!rightInAirPocket && right !in coords) {
                    count++
                }
            }
        }
        return count
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day18_test")
    check(part1(testInput) == 64)
    check(part2(testInput) == 58)

    val input = readInput("Day18")
    println(part1(input))
    println(part2(input))
}

typealias CubeCoord = Triple<Int, Int, Int>

private fun toCoords(input: List<String>): Set<CubeCoord> = input.map { line ->
    val (x, y, z) = line.split(",")
    CubeCoord(x.toInt(), y.toInt(), z.toInt())
}.toSet()

private fun isAirPocket(
    cube: CubeCoord,
    coords: Set<CubeCoord>,
    knownAirPocketCubes: MutableSet<CubeCoord>,
): Boolean {
    val visited = mutableSetOf<CubeCoord>()
    val queue = ArrayDeque<CubeCoord>()
    queue.add(cube)
    while (queue.isNotEmpty()) {
        val cubeToCheck = queue.removeFirst()
        visited.add(cubeToCheck)
        val (x, y, z) = cubeToCheck
        val cubesInFront = coords.firstOrNull { (x2, y2, z2) -> x2 == x && y2 == y && z2 < z }
        val cubesBehind = coords.firstOrNull { (x2, y2, z2) -> x2 == x && y2 == y && z2 > z }
        val cubesAbove = coords.firstOrNull { (x2, y2, z2) -> x2 == x && y2 > y && z2 == z }
        val cubesBelow = coords.firstOrNull { (x2, y2, z2) -> x2 == x && y2 < y && z2 == z }
        val cubesLeft = coords.firstOrNull { (x2, y2, z2) -> x2 < x && y2 == y && z2 == z }
        val cubesRight = coords.firstOrNull { (x2, y2, z2) -> x2 > x && y2 == y && z2 == z }
        if (cubesInFront == null ||
            cubesBehind == null ||
            cubesAbove == null ||
            cubesBelow == null ||
            cubesLeft == null ||
            cubesRight == null
        ) {
            // not air pocket
            return false
        } else {
            val front = CubeCoord(x, y, z - 1)
            if (front !in coords && front !in visited) {
                queue.add(front)
                visited.add(front)
            }
            val back = CubeCoord(x, y, z + 1)
            if (back !in coords && back !in visited) {
                queue.add(back)
                visited.add(back)
            }
            val top = CubeCoord(x, y + 1, z)
            if (top !in coords && top !in visited) {
                queue.add(top)
                visited.add(top)
            }
            val bottom = CubeCoord(x, y - 1, z)
            if (bottom !in coords && bottom !in visited) {
                queue.add(bottom)
                visited.add(bottom)
            }
            val left = CubeCoord(x - 1, y, z)
            if (left !in coords && left !in visited) {
                queue.add(left)
                visited.add(left)
            }
            val right = CubeCoord(x + 1, y, z)
            if (right !in coords && right !in visited) {
                queue.add(right)
                visited.add(right)
            }
        }
    }
    knownAirPocketCubes.addAll(visited)
    return true
}
