import kotlin.math.abs

typealias Coord = Pair<Int, Int>

fun main() {
    fun part1(input: List<String>, targetLineY: Int): Int {
        // find sensors close to the target line
        val allBeaconCoords = getBeaconCoords(input)
        val intersectedCoords = mutableSetOf<Coord>()

        input.forEach { line ->
            val lineSplit = line.split(" ")
            val (sensorCoords, beaconCoords) = getCoords(lineSplit)
            val (sensorX, sensorY) = sensorCoords
            val (beaconX, beaconY) = beaconCoords
            val manhattanDistance = abs(sensorX - beaconX) + abs(sensorY - beaconY)

            if (sensorY < targetLineY) {
                // sensor is higher than the line
                val lowestYSensorReaches = sensorY + manhattanDistance
                val yDiff = lowestYSensorReaches - targetLineY
                if (yDiff >= 0) {
                    // intersects line
                    val xDiff = manhattanDistance - (targetLineY - sensorY)
                    val newIntersectedCoords = getIntersectedCoords(xDiff, sensorX, targetLineY, allBeaconCoords)
                    intersectedCoords.addAll(newIntersectedCoords)
                }
            } else if (sensorY > targetLineY) {
                // sensor is lower than the line
                val highestYSensorReaches = sensorY - manhattanDistance
                val yDiff = highestYSensorReaches - targetLineY
                if (yDiff <= 0) {
                    // intersects line
                    val xDiff = manhattanDistance - (sensorY - targetLineY)
                    val newIntersectedCoords = getIntersectedCoords(xDiff, sensorX, targetLineY, allBeaconCoords)
                    intersectedCoords.addAll(newIntersectedCoords)
                }
            }
        }
        return intersectedCoords.size
    }

    fun part2(input: List<String>, upperCoordLimit: Int): Long {
        val manhattanDistanceBySensorCoords = getManhattanDistanceBySensorCoords(input)
        var potential: Coord? = null

        outerloop@ for (y in 0..upperCoordLimit) {
            var xCount = 0
            while (xCount <= upperCoordLimit) {
                val coord = xCount to y
                // is it within any sensor manhattan distances, if not then it's the distress beacon
                run sensorloop@{
                    manhattanDistanceBySensorCoords.forEach { (sensorCoords, maxManhattanDistance) ->
                        val (sensorX, sensorY) = sensorCoords
                        val manhattanDistance = abs(xCount - sensorX) + abs(y - sensorY)
                        if (manhattanDistance > maxManhattanDistance) {
                            // not within range of sensor
                            if (potential == null) {
                                potential = coord
                            }
                        } else {
                            // within range of sensor
                            potential = null
                            // find out the manhattan distance diff and advance x coord by that amount
                            val manhattanDistanceDiff = maxManhattanDistance - manhattanDistance
                            xCount += manhattanDistanceDiff
                            return@sensorloop
                        }
                    }
                }
                if (potential != null) {
                    break@outerloop
                }
                xCount++
            }
        }

        val (distressBeaconX, distressBeaconY) = potential!!
        return (distressBeaconX.toLong() * 4_000_000) + distressBeaconY
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day15_test")
    check(part1(testInput, 10) == 26)
    check(part2(testInput, 20) == 56_000_011L)

    val input = readInput("Day15")
    println(part1(input, 2_000_000))
    println(part2(input, 4_000_000))
}

private fun getCoords(lineSplit: List<String>): Pair<Pair<Int, Int>, Pair<Int, Int>> {
    val sensorX = lineSplit[2].substringAfter("=").substringBefore(",").toInt()
    val sensorY = lineSplit[3].substringAfter("=").substringBefore(":").toInt()
    val beaconX = lineSplit[8].substringAfter("=").substringBefore(",").toInt()
    val beaconY = lineSplit[9].substringAfter("=").toInt()
    return (sensorX to sensorY) to (beaconX to beaconY)
}

private fun getBeaconCoords(input: List<String>): Set<Pair<Int, Int>> = input.map { line ->
    val lineSplit = line.split(" ")
    val (_, beaconCoord) = getCoords(lineSplit)
    beaconCoord
}.toSet()

private fun getIntersectedCoords(xDiff: Int, sensorX: Int, targetLineY: Int, allBeaconCoords: Set<Coord>): Set<Coord> {
    val intersectedCoords = mutableSetOf<Coord>()
    for (i in xDiff downTo 0) {
        val coord = (sensorX - i) to targetLineY
        if (coord !in allBeaconCoords) {
            intersectedCoords.add(coord)
        }
    }
    // coords to the right
    for (i in 0..xDiff) {
        val coord = (sensorX + i) to targetLineY
        if (coord !in allBeaconCoords) {
            intersectedCoords.add(coord)
        }
    }
    // middle coord
    val coord = sensorX to targetLineY
    if (coord !in allBeaconCoords) {
        intersectedCoords.add(coord)
    }
    return intersectedCoords
}

private fun getManhattanDistanceBySensorCoords(input: List<String>) = input.associate { line ->
    val lineSplit = line.split(" ")
    val (sensorCoords, beaconCoords) = getCoords(lineSplit)
    val (sensorX, sensorY) = sensorCoords
    val (beaconX, beaconY) = beaconCoords
    val manhattanDistance = abs(sensorX - beaconX) + abs(sensorY - beaconY)
    sensorCoords to manhattanDistance
}
