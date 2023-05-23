import kotlin.math.ln
import kotlin.math.pow

data class Player(val name: String, var score: Double, var rank: Int = 0)

object PlayerList {

    val players = mutableListOf(
        Player("ANNE", 1500.0),
        Player("BOBBY", 1500.0),
        Player("CINDY", 1500.0),
        Player("DONNA", 1500.0),
        Player("ERICK", 1500.0),
        Player("FATIMA", 1500.0),
        Player("GRACE", 1500.0),
        Player("HEART", 1500.0),
        Player("IVAN", 1500.0),
        Player("JOHN", 1500.0),
        Player("KEVIN", 1500.0),
        Player("LEAH", 1500.0),
        Player("MARIE", 1500.0),
        Player("NATHAN", 1500.0),
        Player("OLIVE", 1500.0),
        Player("PETER", 1500.0),
        Player("QUEEN", 1500.0),
        Player("RYAN", 1500.0),
        Player("SOFIA", 1500.0),
        Player("TOBY", 1500.0),
        Player("USHER", 1500.0),
        Player("VIVIAN", 1500.0),
        Player("WENDY", 1500.0),
        Player("XAVIER", 1500.0),
        Player("YVONNE", 1500.0),
        Player("ZANE", 1500.0),
        Player("ZANE2", 1500.0)
    )
}
object Color {
    const val BLUE = "\u001B[34m"
    const val RED = "\u001B[31m"
    const val GREEN = "\u001B[32m"

    const val MAGENTA = "\u001B[35m"
    const val CYAN = "\u001B[36m"
    const val YELLOW = "\u001B[33m"

    const val RESET = "\u001B[0m"
}


var players = PlayerList.players.sortedByDescending { it.score }.toMutableList()
val playedMatches = mutableSetOf<Pair<Player, Player>>()
val rounds = (ln(PlayerList.players.size.toDouble()) / ln(2.0)).toInt() + 1

fun main() {

    println("____________________________________________________________________")
    pressEnterToProceed("Press ${Color.GREEN}[Enter]${Color.RESET} to start:")

    for (round in 1..rounds) {
        val matches = generateMatches(players)
        val totalMatches = playedMatches.size + matches.size*(rounds - round + 1)

        updateRatings(matches, totalMatches)
        players = players.sortedByDescending { it.score }.toMutableList()

    }
    println("${Color.CYAN}Progress: 100%${Color.RESET}")
    println("____________________________________________________________________")
    pressEnterToProceed("Press ${Color.GREEN}[Enter]${Color.RESET} to see the results: ")
    println("Final Result after $rounds rounds:")
    calculateRank()

    players.forEach{
        println("${Color.MAGENTA}Rank #${it.rank}${Color.RESET} ${it.name}")
    }

}
fun calculateRank() {
    var rank = 1
    var previousScore = Double.MAX_VALUE

    for( (index, player) in players.withIndex() ) {
        if (player.score < previousScore) {
            rank = index +1
        }
        player.rank = rank
        previousScore = player.score

    }
}


//matches for single round
fun generateMatches(
    players: MutableList<Player>,
): MutableList<Pair<Player, Player>> {

    val matches = mutableListOf<Pair<Player, Player>>()

    //Selecting Player1
    for (i in 0 until players.size - 1) {
        val playerA = players[i]
        //checks if player1 exist in any matches in this round, should only exist once
        if (matches.any { it.first == playerA || it.second == playerA }) {
            continue // Skip players who already have a match in this round
        }
        //Selecting Player2
        for (j in i + 1 until players.size) {
            val playerB = players[j]
            //checks if player2 exist in any matches in this round, should only exist once
            if (matches.any { it.first == playerB || it.second == playerB }) {
                continue // Skip players who already have a match in this round
            }

            //checks if playedMatches contains this single match, if not, then add it to matches and playedMatches,
            //if it exists, then continue looping
            if (playedMatches.none { (a, b) ->
                    (a.name == playerA.name && b.name == playerB.name) ||
                            (a.name == playerB.name && b.name == playerA.name)
                }) {
                matches.add(playerA to playerB)
            }
            break
        }
    }
    return matches
}
fun pressEnterToProceed(text: String) {
    println(text)
    var input = readlnOrNull()
    while (!input.isNullOrEmpty()) {
        println("${Color.RED}Invalid input. Press Enter or leave it empty to proceed.${Color.RESET}")
        input = readlnOrNull()
    }
}

fun userInput(): Int {
    var case: Int? = null
    while (case == null || case !in 1..3) {
        println("Type your answer:")
        val input = readlnOrNull()
        case = input?.toIntOrNull()
        if (case !in 1..3) {
            println("${Color.RED}Invalid input. Please input 1, 2, or 3.${Color.RESET}")
        }
    }
    return case
}

fun updateRatings(
    matches: MutableList<Pair<Player, Player>>,
    totalMatches: Int
) {


    for (match in matches) {
        val (playerA, playerB) = match

        val expectedScoreA = 1.0 / (1 + 10.0.pow((playerB.score - playerA.score) / 400))
        val expectedScoreB = 1.0 - expectedScoreA

        val kFactor = 40 // Adjust this value based on sensitivity

        playedMatches.add(playerA to playerB)
        val battle = playedMatches.indexOf(match) + 1

        val progress = (((battle-1).toDouble() / totalMatches.toDouble()) * 100).toInt()
        println("${Color.CYAN}Progress: $progress%${Color.RESET}")

        println("Match #$battle: ${Color.GREEN}${playerA.name} vs ${playerB.name}${Color.RESET}")

        println("${Color.GREEN}[1]${Color.RESET} ${playerA.name} | " +
                "${Color.GREEN}[2]${Color.RESET} ${playerB.name} | " +
                "${Color.GREEN}[3]${Color.RESET} Draw")
        val case = userInput()

        val (scoreA, scoreB) = when (case) {
            1 -> playerA.score + kFactor * (1 - expectedScoreA) to
                    playerB.score + kFactor * (0 - expectedScoreB)

            2 -> playerA.score + kFactor * (0 - expectedScoreA) to
                    playerB.score + kFactor * (1 - expectedScoreB)

            else -> playerA.score + kFactor * (0.5 - expectedScoreA) to
                    playerB.score - kFactor * (0.5 - expectedScoreA)
        }

        val playerToUpdateA = players.find { it.name == playerA.name }
        playerToUpdateA?.score = scoreA
        val playerToUpdateB = players.find { it.name == playerB.name }
        playerToUpdateB?.score = scoreB


        println(
            when (case) {
                1 -> "${Color.BLUE}${playerA.name} wins and ${playerB.name} lose${Color.RESET}"
                2 -> "${Color.BLUE}${playerA.name} lose and ${playerB.name} wins${Color.RESET}"
                else -> "${Color.BLUE}${playerA.name} and ${playerB.name} have a draw${Color.RESET}"
            }
        )
        println()
    }
}





