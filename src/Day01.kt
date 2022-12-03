fun main() {
    fun part1(input: List<String>): Int {
        var leaderCalories = 0
        var runningTotal = 0
        input.forEach { line ->
            if (line == "") {
                if (runningTotal > leaderCalories) {
                    leaderCalories = runningTotal
                }
                runningTotal = 0
            } else {
                runningTotal += line.toInt()
            }
        }
        return leaderCalories
    }

    fun part2(input: List<String>): Int {
        val allElves = mutableListOf<Int>()
        var runningTotal = 0
        input.forEach { line ->
            if (line == "") {
                allElves += runningTotal
                runningTotal = 0
            } else {
                runningTotal += line.toInt()
            }
        }
        return allElves.sortedDescending().take(3).sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 24000)

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
