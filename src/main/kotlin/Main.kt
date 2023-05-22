
import kotlin.math.ln
import kotlin.math.pow

data class Player(val name: String, var score: Double)

object PlayerList {

    val players = mutableListOf(
        Player("ANNE", 1200.0),
        Player("BOBBY", 1200.0),
        Player("CINDY", 1200.0),
        Player("DONNA", 1200.0),
        Player("ERICK", 1200.0),
        Player("FATIMA", 1200.0),
        Player("GRACE", 1200.0),
        Player("HEART", 1200.0),
        Player("IVAN", 1200.0)
    )

}

//initial players
var players = sortList(PlayerList.players)
val playedMatches = mutableSetOf<Pair<Player, Player>>()
val rounds = (ln(PlayerList.players.size.toDouble()) / ln(2.0)).toInt()

fun main() {

    for (round in 1..rounds) {

        println("Round $round Matches:")
        val matches = generateMatches(players)

        //Print Matches
        for ((player1, player2) in matches)
        { println("${player1.name} vs ${player2.name}") }
        println()

        updateRatings(matches)
        players = sortList(players)

        // TODO: Format Ranking

        //Print Results
        println()
        println(if (round == rounds ) "Final Result after $rounds rounds:"
                else "Partial Result after Round $round")
        players.forEachIndexed { index, player ->
            println("#${index+1} ${player.name} - ${player.score.toInt()}") }
        println()

    }
}

//matches for single round
fun generateMatches(
    players: MutableList<Player>,
): MutableList<Pair<Player, Player>> {

    val matches = mutableListOf<Pair<Player, Player>>()

    //Selecting Player1
    for (i in 0 until players.size - 1) {
        val player1 = players[i]
        //checks if player1 exist in any matches in this round, should only exist once
        if (matches.any { it.first == player1 || it.second == player1 }) {
            continue // Skip players who already have a match in this round
        }

        //Selecting Player2
        for (j in i + 1 until players.size) {
            val player2 = players[j]
            //checks if player2 exist in any matches in this round, should only exist once
            if (matches.any { it.first == player2 || it.second == player2 }) {
                continue // Skip players who already have a match in this round
            }

            //checks if playedMatches contains this single match, if not, then add it to matches and playedMatches,
            //if it exists, then do nothing
            if (!playedMatches.contains(player1 to player2)) {
                matches.add(player1 to player2)
                playedMatches.add(player1 to player2)
                break
            }
        }
    }
    return matches
}


fun case(): Int {
    var case: Int? = null
    while (case == null) {
        println("Choose:")
        val input = readlnOrNull()
        case = input?.toIntOrNull()
        if (case == null) {
            println("Invalid input. Please input 1, 2, or any#.")
        }
    }
    return case
}


fun updateRatings(
    matches: MutableList<Pair<Player, Player>>,
) {
    for (match in matches) {
        val (playerA, playerB) = match

        val expectedScoreA = 1.0 / (1 + 10.0.pow((playerB.score - playerA.score) / 400))
        val expectedScoreB = 1.0 - expectedScoreA

        val kFactor = 32 // Adjust this value based on sensitivity

        val battle = playedMatches.indexOf(match)+1

        val totalBattle = rounds * (players.size/2)

        println("Match #$battle of $totalBattle")
        println("[1]:${playerA.name} vs [2]:${playerB.name} or [any#]:Draw")
        val case = case()

        val (scoreA, scoreB) = when (case) {
            1 ->    playerA.score + kFactor * (1 - expectedScoreA) to
                    playerB.score + kFactor * (0 - expectedScoreB)
            2 ->    playerA.score + kFactor * (0 - expectedScoreA) to
                    playerB.score + kFactor * (1 - expectedScoreB)
            else -> playerA.score + kFactor * (0.5 - expectedScoreA) to
                    playerB.score - kFactor * (0.5 - expectedScoreA)
        }

        val playerToUpdateA = players.find { it.name == playerA.name }
        playerToUpdateA?.score = scoreA

        val playerToUpdateB = players.find { it.name == playerA.name }
        playerToUpdateB?.score = scoreB


        println (
            when (case) {
                1 -> "${playerA.name} wins and ${playerB.name} lose"
                2 -> "${playerA.name} lose and ${playerB.name} wins"
                else -> "${playerA.name} and ${playerB.name} have a draw"
            }
            )
        println()
    }

}

private fun sortList(players: MutableList<Player>): MutableList<Player> {
    return players.sortedWith(compareByDescending<Player> { it.score }.thenBy { it.name }).toMutableList()
}



