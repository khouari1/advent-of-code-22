fun main() {
    fun part1(input: List<String>): Int {
        val pathTotalPressures = getTotalPressureForAllPaths(input, 30)
        return pathTotalPressures.maxOf { (_, totalPressure) -> totalPressure }
    }

    fun part2(input: List<String>): Int {
        val pathTotalPressures = getTotalPressureForAllPaths(input, 26)
        val totalPressureByPath = pathTotalPressures.toMap()

        val allPaths = pathTotalPressures.map { it.first }.map { it.toSet() }
        var highestTotalPressure = 0
        allPaths.forEach { path1 ->
            allPaths.filter { path2 ->
                val pathIntersection = (path1 - "AA").intersect(path2 - "AA")
                pathIntersection.isEmpty()
            }.forEach { path2 ->
                val path1List = path1.toList()
                val path1Total = totalPressureByPath[path1List]!!
                val path2List = path2.toList()
                val path2Total = totalPressureByPath[path2List]!!
                if (path1Total + path2Total > highestTotalPressure) {
                    highestTotalPressure = path1Total + path2Total
                }
            }
        }
        return highestTotalPressure
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day16_test")
    check(part1(testInput) == 1651)
    check(part2(testInput) == 1707)

    val input = readInput("Day16")
    println(part1(input))
    println(part2(input))
}

private fun valvesById(input: List<String>) = input.associate { line ->
    val split = line.split(" ")
    val valve = Valve(
        id = split[1],
        flowRate = split[4].substringAfter("=").substringBefore(";").toInt(),
        leadToValves = if (line.contains("valves")) {
            line.substringAfter("valves").filterNot { it.isWhitespace() }.split(",")
        } else {
            listOf(line.substringAfter("valve").filterNot { it.isWhitespace() })
        }
    )
    valve.id to valve
}

private fun getTotalPressureForAllPaths(input: List<String>, totalMinutes: Int): MutableList<Pair<List<String>, Int>> {
    val valveDistanceMatrix = mutableMapOf<String, MutableMap<String, Int>>()
    val valvesById = valvesById(input)
    val valveIds = valvesById.keys

    // DFS to find distance between each node
    valveIds.forEach { valveId ->
        val leadToValves = mutableMapOf<String, Int>()
        valveDistanceMatrix[valveId] = leadToValves
        val visited = mutableSetOf<String>()
        val queue = ArrayDeque<Pair<String, Int>>()
        queue.add(valveId to 0)
        while (queue.isNotEmpty()) {
            val (valveId, count) = queue.removeFirst()
            visited.add(valveId)
            leadToValves[valveId] = count
            val valve = valvesById[valveId]!!
            val remainingValves = valve.leadToValves.filter { v -> v !in visited }
            remainingValves.forEach { v ->
                queue.add(v to count + 1)
            }
        }
    }

    // Find highest node each time, remembering some nodes have 0 flow rate
    val visitedValveIds = mutableSetOf<String>()
    visitedValveIds.add("AA")
    val nonZeroFlowRateValvesById = valveIds.map { valvesById[it]!! }.filter { it.flowRate != 0 }.associateBy { it.id }

    val allPaths = mutableListOf<Pair<List<String>, Int>>()
    getTotalPressureForAllPaths(
        currentNode = "AA",
        valvesToVisit = nonZeroFlowRateValvesById.keys,
        valvesById = nonZeroFlowRateValvesById,
        matrix = valveDistanceMatrix,
        minute = 1,
        total = 0,
        allPaths = allPaths,
        path = listOf("AA"),
        totalMinutes = totalMinutes,
    )
    return allPaths
}

private fun getTotalPressureForAllPaths(
    currentNode: String,
    valvesToVisit: Set<String>,
    valvesById: Map<String, Valve>,
    matrix: Map<String, Map<String, Int>>,
    minute: Int,
    total: Int,
    allPaths: MutableList<Pair<List<String>, Int>>,
    path: List<String>,
    totalMinutes: Int,
) {
    allPaths.add(path to total)
    val otherValveDistancesById = matrix[currentNode]!!
    if (minute > totalMinutes || valvesToVisit.isEmpty()) {
        return
    } else {
        valvesToVisit.forEach { otherValve ->
            val otherValves = valvesToVisit - otherValve
            val newValve = valvesById[otherValve]!!
            // travel to other node and open it
            var newMinute = minute + otherValveDistancesById[otherValve]!!
            if (newMinute > totalMinutes) {
                return@forEach
            }
            val newTotal = total + (newValve.flowRate * (totalMinutes - newMinute))
            newMinute++
            getTotalPressureForAllPaths(
                currentNode = otherValve,
                valvesToVisit = otherValves,
                valvesById = valvesById,
                matrix =matrix,
                minute = newMinute,
                total = newTotal,
                allPaths = allPaths,
                path = path + otherValve,
                totalMinutes = totalMinutes,
            )
            if (otherValves.isEmpty()) {
                return@forEach
            }
        }
    }
}

data class Valve(
    val id: String,
    val flowRate: Int,
    val leadToValves: List<String>,
)
