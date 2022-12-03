fun main() {
    fun part1(input: List<String>): Int {
        return input.map { line ->
            val individualChars = line.split("")
            val first = individualChars.subList(0, individualChars.size / 2)
            val second = individualChars.subList(individualChars.size / 2, individualChars.size - 1)
            first.intersect(second.toSet()).first()
        }.sumOf {
            letterValue(it)
        }
    }

    fun part2(input: List<String>): Int {
        return input.chunked(3)
            .map { group ->
                val first = group[0].split("").distinct().drop(1)
                val second = group[1].split("").distinct().drop(1)
                val third = group[2].split("").distinct().drop(1)
                first.intersect(second.toSet()).intersect(third.toSet()).first()
            }.sumOf {
                letterValue(it)
            }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 157)
    check(part2(testInput) == 70)

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}

fun letterValue(letter: String): Int {
    val letterAsChar = letter.single()
    return if (letterAsChar.isUpperCase()) {
        letterAsChar - 'A' + 27
    } else {
        letterAsChar - 'a' + 1
    }
}
