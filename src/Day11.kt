fun main() {
    fun part1(input: List<String>): Long {
        val monkeys = buildMonkeys(input)
        return run(20, monkeys) { monkey, itemWorryLevel ->
            val operation = monkey.operation(itemWorryLevel)
            operation / 3
        }
    }

    fun part2(input: List<String>): Long {
        val monkeys = buildMonkeys(input)
        val lowestCommonMultiple = monkeys.map { it.testDivisibleBy }.reduce { acc, i -> acc * i }
        return run(10_000, monkeys) { monkey, itemWorryLevel ->
            val operation = monkey.operation(itemWorryLevel)
            operation % lowestCommonMultiple
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    check(part1(testInput) == 10_605L)
    check(part2(testInput) == 2_713_310_158)

    val input = readInput("Day11")
    println(part1(input))
    println(part2(input))
}

private fun buildMonkeys(input: List<String>) =
    input.filterNot { it == "" }
        .chunked(6)
        .map { buildMonkey(it) }
        .toTypedArray()

private fun buildMonkey(lines: List<String>): Monkey {
    val (operand1, operator, operand2) = lines[2].split(" = ")[1].split(" ")
    val op1 = if (operand1 == "old") { old: Long -> old } else { _ -> operand1.toLong() }
    val op2 = if (operand2 == "old") { old: Long -> old } else { _ -> operand2.toLong() }
    val op = operator(operator)
    return Monkey(
        id = lines[0].split(" ")[1].dropLast(1).toInt(),
        startingItems = lines[1].split(":")[1]
            .filterNot { it.isWhitespace() }
            .split(",")
            .map { it.toLong() }
            .toMutableList(),
        operation = { old: Long -> op(op1(old), op2(old)) },
        testDivisibleBy = lines[3].split(" ").last().toInt(),
        monkeyIfTrue = lines[4].split(" ").last().toInt(),
        monkeyIfFalse = lines[5].split(" ").last().toInt()
    )
}

private fun run(
    rounds: Int,
    monkeys: Array<Monkey>,
    worryLevelCalc: (monkey: Monkey, currentItemWorryLevel: Long) -> Long,
): Long {
    for (round in 1..rounds) {
        monkeys.forEach { monkey ->
            monkey.startingItems.forEach { itemWorryLevel ->
                monkey.itemsInspected++
                val newWorryLevel = worryLevelCalc(monkey, itemWorryLevel)
                val b = newWorryLevel % monkey.testDivisibleBy == 0L
                val monkeyToThrowTo = when (b) {
                    true -> monkey.monkeyIfTrue
                    false -> monkey.monkeyIfFalse
                }
                monkeys[monkeyToThrowTo].startingItems.add(newWorryLevel)
            }
            monkey.startingItems.clear()
        }
    }
    return monkeys.sortedByDescending { it.itemsInspected }
        .take(2)
        .map { it.itemsInspected }
        .reduce { acc, i -> acc * i }
}

data class Monkey(
    val id: Int,
    val startingItems: MutableList<Long>,
    val operation: (old: Long) -> Long,
    val testDivisibleBy: Int,
    val monkeyIfTrue: Int,
    val monkeyIfFalse: Int,
) {
    var itemsInspected: Long = 0
}

private fun operator(operatorString: String): (Long, Long) -> Long = when (operatorString) {
    "*" -> { op1: Long, op2: Long -> op1 * op2 }
    "+" -> { op1: Long, op2: Long -> op1 + op2 }
    else -> throw UnsupportedOperationException("Unsupported operation = $operatorString")
}
