fun main() {
    fun part1(input: List<String>): Int {
        val cycleValues = mutableListOf<Int>()
        var cycle = 0
        var x = 1
        input.forEach { line ->
            val instruction = line.substring(0, 4)
            when (instruction) {
                "addx" -> {
                    val (_, number) = line.split(" ")
                    if (cycle % 40 == 18 && cycle <= 220) {
                        cycleValues.add(x * (cycle + 2))
                    }
                    if (cycle % 40 == 19 && cycle <= 220) {
                        cycleValues.add(x * (cycle + 1))
                    }
                    x += number.toInt()
                    cycle += 2
                }
                "noop" -> {
                    if (cycle % 40 == 19 && cycle <= 220) {
                        cycleValues.add(x * (cycle + 1))
                    }
                    cycle++
                }
            }
        }
        val sum = cycleValues.sum()
        println(sum)
        return sum
    }

    fun part2(input: List<String>): Int = 0

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
    check(part1(testInput) == 13_140)
//    check(part2(testInput) == 36)

    val input = readInput("Day10")
    println(part1(input))
//    println(part2(input))
}