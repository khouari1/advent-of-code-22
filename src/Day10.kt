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
                    } else if (cycle % 40 == 19 && cycle <= 220) {
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
        return cycleValues.sum()
    }

    fun part2(input: List<String>) {
        var cycle = 1
        var x = 1
        fun charToDraw(): String {
            return if (cycle - 1 >= x - 1 && cycle - 1 <= x + 1) {
                "#"
            } else {
                "."
            }
        }
        input.flatMap { line ->
            val charsToDraw = mutableListOf<String>()
            if (cycle == 41) {
                cycle = 1
            }
            charsToDraw.add(charToDraw())
            val instruction = line.substring(0, 4)
            when (instruction) {
                "addx" -> {
                    val (_, number) = line.split(" ")
                    cycle++
                    if (cycle == 41) {
                        cycle = 1
                    }
                    charsToDraw.add(charToDraw())
                    x += number.toInt()
                    cycle++
                }
                "noop" -> cycle++
            }
            charsToDraw
        }.chunked(40).forEach { println(it.joinToString("")) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
    check(part1(testInput) == 13_140)
    part2(testInput)

    val input = readInput("Day10")
    println(part1(input))
    part2(input)
}