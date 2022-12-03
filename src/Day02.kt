import java.lang.IllegalStateException

fun main() {
    fun part1(input: List<String>): Int {
        return input.sumOf {
            val (opponentChoice, myChoice) = it.split(" ")
            myRoundScore(opponentChoice, myChoice)
        }
    }

    fun part2(input: List<String>): Int {
        return input.sumOf {
            val (opponentChoice, myChoice) = it.split(" ")
            myRoundScore2(opponentChoice, myChoice)
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 15)
    check(part2(testInput) == 12)

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}


private fun myRoundScore(opponentChoice: String, myChoice: String): Int {
    return when {
        opponentChoice == "A" && myChoice == "X" -> 4
        opponentChoice == "A" && myChoice == "Y" -> 8
        opponentChoice == "A" && myChoice == "Z" -> 3
        opponentChoice == "B" && myChoice == "X" -> 1
        opponentChoice == "B" && myChoice == "Y" -> 5
        opponentChoice == "B" && myChoice == "Z" -> 9
        opponentChoice == "C" && myChoice == "X" -> 7
        opponentChoice == "C" && myChoice == "Y" -> 2
        opponentChoice == "C" && myChoice == "Z" -> 6
        else -> throw IllegalStateException("Should not be possible. Opponent = $opponentChoice, Me = $myChoice")
    }
}

private fun myRoundScore2(opponentChoice: String, myChoice: String): Int {
    return when {
        opponentChoice == "A" && myChoice == "X" -> 3
        opponentChoice == "A" && myChoice == "Y" -> 4
        opponentChoice == "A" && myChoice == "Z" -> 8
        opponentChoice == "B" && myChoice == "X" -> 1
        opponentChoice == "B" && myChoice == "Y" -> 5
        opponentChoice == "B" && myChoice == "Z" -> 9
        opponentChoice == "C" && myChoice == "X" -> 2
        opponentChoice == "C" && myChoice == "Y" -> 6
        opponentChoice == "C" && myChoice == "Z" -> 7
        else -> throw IllegalStateException("Should not be possible. Opponent = $opponentChoice, Me = $myChoice")
    }
}
