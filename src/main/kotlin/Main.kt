import kotlin.math.pow

data class Player(val name: String, var score: Int)


object PlayerList {

    val players = mutableListOf(
        Player("Player 1", 1200),
        Player("Player 2", 1200),
        Player("Player 3", 1200),
        Player("Player 4", 1200),
        Player("Player 5", 1200),
        Player("Player 6", 1200),
        Player("Player 7", 1200),
        Player("Player 8", 1200),
        Player("Player 9", 1200)
    )

}


//matches for single round
fun generateMatches(
    players: MutableList<Player>,
    playedMatches: MutableSet<Pair<Player, Player>>
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

            //checks if overall playedMatches contains this single match, if not then, add it to matches and overall,
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

fun updateRatings(
    oldList: MutableList<Player>,
    matches: MutableList<Pair<Player, Player>>
): MutableList<Player> {

    val newList = mutableListOf<Player>()
    for (match in matches) {
        val (playerA, playerB) = match

        val expectedScoreA = 1.0 / (1 + 10.0.pow((playerB.score - playerA.score) / 400))
        val expectedScoreB = 1.0 - expectedScoreA

        val kFactor = 32 // Adjust this value based on desired sensitivity of rating changes

        //player A always wins
        //insert function here for WIN/LOST
        val updatedRatingA = playerA.score + kFactor * (1 - expectedScoreA)
        val updatedRatingB = playerB.score + kFactor * (0 - expectedScoreB)

        playerA.score = updatedRatingA.toInt()
        playerB.score = updatedRatingB.toInt()

        newList.add(playerA)
        newList.add(playerB)

    }
    return (oldList + newList).associateBy { it.name }.values.toMutableList()
}

private fun sortList(players: MutableList<Player>): MutableList<Player> {
    return players.sortedWith(compareByDescending<Player> { it.score }.thenBy { it.name }).toMutableList()
}

fun main() {

    //initial players
    var players = sortList(PlayerList.players)

    val playedMatches = mutableSetOf<Pair<Player, Player>>()
    val rounds = 4

    for (round in 1..rounds) {

        println("Round $round:")

        val matches = generateMatches(players, playedMatches)
        val updatedList = sortList(updateRatings(players, matches))
        players = updatedList


        //Print Matches and Partial Result per rounds
        for ((player1, player2) in matches) {
            println("${player1.name} vs ${player2.name}") }

        println("\nPartial Result for Round $round")
        players.forEach { println("${it.name} - ${it.score}") }
        println()


    }

}

