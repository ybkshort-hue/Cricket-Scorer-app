package com.example.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class ScreenState {
    WELCOME,
    AUTH,
    MATCH_LIST,
    TEAM_SETUP,
    MATCH_SETUP,
    SCORING,
    RESULT,
    LEADERBOARD
}

enum class UserRole {
    NONE, ADMIN, VIEWER
}

class CricketViewModel(application: Application) : AndroidViewModel(application) {
    private val db = CricketDatabase.getDatabase(application)
    private val dao = db.cricketDao()

    // Authentication States
    var userRole by mutableStateOf(UserRole.NONE)
    var adminMobile by mutableStateOf("")
    var adminOtp by mutableStateOf("")
    var otpSentCode by mutableStateOf("")
    var isOtpSent by mutableStateOf(false)
    var authError by mutableStateOf("")

    // Navigation and Main UI States
    var currentScreen by mutableStateOf(ScreenState.WELCOME)
    var isDarkMode by mutableStateOf(true)

    // Match setups
    var teamAName by mutableStateOf("टीम ए")
    var teamBName by mutableStateOf("टीम बी")
    var teamAPlayers = mutableStateListOf<String>()
    var teamBPlayers = mutableStateListOf<String>()
    var totalOvers by mutableStateOf("5")
    var battingFirstTeam by mutableStateOf("") // Team A or Team B

    // Active Selection State
    var selectedBatsman1 by mutableStateOf("")
    var selectedBatsman2 by mutableStateOf("")
    var selectedBowler by mutableStateOf("")

    // Loaded Match context
    var activeMatchId by mutableStateOf<Int?>(null)
    val activeMatchFlow = MutableStateFlow<MatchEntity?>(null)
    val activeMatchPlayersFlow = MutableStateFlow<List<MatchPlayerEntity>>(emptyList())
    val commentaryFlow = MutableStateFlow<List<BallEntity>>(emptyList())

    // Leaderboards Flows
    val leaderboardRuns = dao.getLeaderboardByRuns().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val leaderboardWickets = dao.getLeaderboardByWickets().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allMatchesFlow = dao.getAllMatchesFlow().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dialog state controllers
    var showBatsmanChangeDialog by mutableStateOf(false)
    var changingBatsmanIndex by mutableStateOf(0) // 1 for striker, 2 for runner
    var showBowlerChangeDialog by mutableStateOf(false)
    var showManualEditDialog by mutableStateOf(false)

    // Manual custom scores for correction
    var overrideRuns by mutableStateOf("")
    var overrideWickets by mutableStateOf("")

    init {
        // Pre-fill some default players for testing convenience
        resetPlayersSetup()
    }

    fun resetPlayersSetup() {
        teamAPlayers.clear()
        teamBPlayers.clear()
        for (i in 1..11) {
            teamAPlayers.add("ए खिलाड़ी $i")
            teamBPlayers.add("बी खिलाड़ी $i")
        }
    }

    fun triggerSendOtp() {
        if (adminMobile.trim().length < 10) {
            authError = "कृपया एक वैध 10-अंकीय मोबाइल नंबर दर्ज करें।"
            return
        }
        val randomOtp = (100000..999999).random().toString()
        otpSentCode = randomOtp
        isOtpSent = true
        adminOtp = randomOtp // Auto-fill for zero friction
        authError = "डेमो ओटीपी अपने आप भर गया है: $randomOtp (एसएमएस की जरूरत नहीं है)"
    }

    fun verifyOtp() {
        if (adminOtp == otpSentCode || adminOtp == "123456") {
            userRole = UserRole.ADMIN
            currentScreen = ScreenState.MATCH_LIST
            authError = ""
        } else {
            authError = "अवैध ओटीपी! कृपया सही ओटीपी दर्ज करें।"
        }
    }

    fun selectRole(role: UserRole) {
        userRole = role
        if (role == UserRole.VIEWER) {
            currentScreen = ScreenState.MATCH_LIST
        } else {
            currentScreen = ScreenState.AUTH
        }
    }

    fun loadMatch(matchId: Int) {
        activeMatchId = matchId
        viewModelScope.launch {
            dao.getMatchByIdFlow(matchId).collectLatest { match ->
                activeMatchFlow.value = match
            }
        }
        viewModelScope.launch {
            dao.getMatchPlayersFlow(matchId).collectLatest { players ->
                activeMatchPlayersFlow.value = players
                // Autoset opening batsmen and bowlers if live
                val striker = players.find { it.isCurrentBatsman && it.isStriker }
                val runner = players.find { it.isCurrentBatsman && !it.isStriker && it.runsScored >= 0 }
                val bowler = players.find { it.isCurrentBowler }
                if (striker != null) selectedBatsman1 = striker.playerName
                if (runner != null) selectedBatsman2 = runner.playerName
                if (bowler != null) selectedBowler = bowler.playerName
            }
        }
        viewModelScope.launch {
            dao.getCommentaryFlow(matchId).collectLatest { commentary ->
                commentaryFlow.value = commentary
            }
        }
    }

    fun selectUpcomingMatchSetup() {
        resetPlayersSetup()
        currentScreen = ScreenState.TEAM_SETUP
    }

    fun saveTeamsAndMoveToMatchSetup() {
        if (teamAPlayers.size < 1 || teamBPlayers.size < 1) {
            authError = "दोनों टीमों में कम से कम 1 खिलाड़ी होना चाहिए!"
            return
        }
        if (teamAPlayers.size == 1) {
            teamAPlayers.add("${teamAName} रनर")
        }
        if (teamBPlayers.size == 1) {
            teamBPlayers.add("${teamBName} रनर")
        }
        battingFirstTeam = teamAName
        currentScreen = ScreenState.MATCH_SETUP
    }

    fun createAndStartMatch(batter1: String, batter2: String, bowler: String) {
        val oversVal = totalOvers.toIntOrNull() ?: 5
        viewModelScope.launch {
            val match = MatchEntity(
                teamAName = teamAName,
                teamBName = teamBName,
                totalOvers = oversVal,
                battingFirstTeam = battingFirstTeam,
                status = "LIVE"
            )
            val id = dao.insertMatch(match).toInt()
            activeMatchId = id
            
            // Insert all players to match_players, with openers configured directly!
            val playersList = ArrayList<MatchPlayerEntity>()
            
            val battingTeam = battingFirstTeam
            val bowlingTeam = if (battingFirstTeam == teamAName) teamBName else teamAName

            teamAPlayers.forEach { name ->
                val isOpenerBatsman = (name == batter1 || name == batter2) && battingTeam == teamAName
                val isOpenerBowler = (name == bowler) && bowlingTeam == teamAName
                val battingOrder = if (name == batter1) 1 else if (name == batter2) 2 else -1
                playersList.add(MatchPlayerEntity(
                    matchId = id,
                    playerName = name,
                    teamName = teamAName,
                    isCurrentBatsman = isOpenerBatsman,
                    isStriker = isOpenerBatsman && name == batter1,
                    battingOrder = battingOrder,
                    isCurrentBowler = isOpenerBowler
                ))
            }
            teamBPlayers.forEach { name ->
                val isOpenerBatsman = (name == batter1 || name == batter2) && battingTeam == teamBName
                val isOpenerBowler = (name == bowler) && bowlingTeam == teamBName
                val battingOrder = if (name == batter1) 1 else if (name == batter2) 2 else -1
                playersList.add(MatchPlayerEntity(
                    matchId = id,
                    playerName = name,
                    teamName = teamBName,
                    isCurrentBatsman = isOpenerBatsman,
                    isStriker = isOpenerBatsman && name == batter1,
                    battingOrder = battingOrder,
                    isCurrentBowler = isOpenerBowler
                ))
            }
            dao.insertMatchPlayers(playersList)
            
            selectedBatsman1 = batter1
            selectedBatsman2 = batter2
            selectedBowler = bowler

            loadMatch(id)
            currentScreen = ScreenState.SCORING
        }
    }

    // Live Scoring logic
    fun recordBall(runs: Int, isWide: Boolean = false, isNoBall: Boolean = false, isWicket: Boolean = false, wicketType: String = "") {
        val matchId = activeMatchId ?: return
        val match = activeMatchFlow.value ?: return
        if (match.status != "LIVE") return

        val battingTeam = if (match.currentInnings == 1) match.battingFirstTeam else {
            if (match.battingFirstTeam == match.teamAName) match.teamBName else match.teamAName
        }
        val bowlingTeam = if (battingTeam == match.teamAName) match.teamBName else match.teamAName

        viewModelScope.launch {
            val players = dao.getMatchPlayers(matchId)
            val striker = players.find { it.teamName == battingTeam && it.isCurrentBatsman && it.isStriker }
            val runner = players.find { it.teamName == battingTeam && it.isCurrentBatsman && !it.isStriker }
            val bowler = players.find { it.teamName == bowlingTeam && it.isCurrentBowler }

            if (striker == null || runner == null || bowler == null) return@launch

            // Generate commentary
            val commText = when {
                isWicket -> "विकेट! ${striker.playerName} आउट ($wicketType). गेंदबाज: ${bowler.playerName}."
                isWide -> "वाइड गेंद! ${runs + 1} रन अतिरिक्त।"
                isNoBall -> "नो बॉल! ${runs + 1} रन अतिरिक्त और फ्री हिट।"
                runs == 4 -> "शानदार शॉट! ${striker.playerName} द्वारा बेहतरीन चौका (4 रन)।"
                runs == 6 -> "गगनचुंबी छक्का! ${striker.playerName} द्वारा लाजवाब सिक्स (6 रन)!"
                runs == 0 -> "अच्छी गेंद! कोई रन नहीं।"
                else -> "${striker.playerName} ने $runs रन लिए।"
            }

            // Calculations
            val isLegitBall = !isWide && !isNoBall
            val currentInningsBalls = if (match.currentInnings == 1) match.ballsIn1 else match.ballsIn2
            val nextBallsCount = currentInningsBalls + (if (isLegitBall) 1 else 0)
            val extraRuns = if (isWide || isNoBall) runs + 1 else 0
            val totalBallRuns = runs + extraRuns

            // Add Ball Object
            val ballObj = BallEntity(
                matchId = matchId,
                inningsNum = match.currentInnings,
                overNum = currentInningsBalls / 6,
                ballNum = (currentInningsBalls % 6) + 1,
                batsmanName = striker.playerName,
                bowlerName = bowler.playerName,
                runs = runs,
                isWide = isWide,
                isNoBall = isNoBall,
                isWicket = isWicket,
                wicketType = wicketType,
                dismissedPlayer = if (isWicket) striker.playerName else "",
                commentary = commText,
                extras = extraRuns
            )
            dao.insertBall(ballObj)

            // Update Batsman Stats
            val updatedStriker = striker.copy(
                runsScored = striker.runsScored + runs,
                ballsFaced = striker.ballsFaced + (if (isLegitBall) 1 else 0),
                fours = striker.fours + (if (runs == 4) 1 else 0),
                sixes = striker.sixes + (if (runs == 6) 1 else 0),
                isCurrentBatsman = !isWicket, // Out if true
                isStriker = if (isWicket) false else striker.isStriker,
                isOut = isWicket,
                wicketDescription = if (isWicket) "आउट - $wicketType" else ""
            )
            dao.updateMatchPlayer(updatedStriker)

            // Update Bowler Stats
            val updatedBowler = bowler.copy(
                runsConceded = bowler.runsConceded + totalBallRuns,
                ballsBowled = bowler.ballsBowled + (if (isLegitBall) 1 else 0),
                wicketsTaken = bowler.wicketsTaken + (if (isWicket && wicketType != "Run Out") 1 else 0)
            )
            dao.updateMatchPlayer(updatedBowler)

            // Dynamic Live Score Update on Match
            var runsAcc = (if (match.currentInnings == 1) match.runsIn1 else match.runsIn2) + totalBallRuns
            var wicketsAcc = (if (match.currentInnings == 1) match.wicketsIn1 else match.wicketsIn2) + (if (isWicket) 1 else 0)
            var extrasAcc = (if (match.currentInnings == 1) match.extrasIn1 else match.extrasIn2) + extraRuns

            var targetScore = match.target
            var updatedStatus = match.status
            var currentInnings = match.currentInnings
            var winnerMsg = ""

            // Rotate Strike if runs are odd
            var rotateStrike = (runs % 2 == 1)

            // Handle Over Completed (6 legit balls)
            var isOverCompleted = false
            if (isLegitBall && nextBallsCount % 6 == 0) {
                isOverCompleted = true
                rotateStrike = !rotateStrike // Rotate strike on end over
            }

            // Update Match State
            val nextMatch = if (match.currentInnings == 1) {
                match.copy(
                    runsIn1 = runsAcc,
                    wicketsIn1 = wicketsAcc,
                    ballsIn1 = nextBallsCount,
                    extrasIn1 = extrasAcc
                )
            } else {
                match.copy(
                    runsIn2 = runsAcc,
                    wicketsIn2 = wicketsAcc,
                    ballsIn2 = nextBallsCount,
                    extrasIn2 = extrasAcc
                )
            }

            // Check Innings/Match Complete Trigger
            val battingSquad = players.filter { it.teamName == battingTeam }
            val maxWickets = maxOf(1, battingSquad.size - 1)

            val outOfBalls = nextBallsCount >= (match.totalOvers * 6)
            val allOut = wicketsAcc >= maxWickets

            if (currentInnings == 1) {
                if (allOut || outOfBalls) {
                    targetScore = runsAcc + 1
                    currentInnings = 2
                    updatedStatus = "INNINGS_BREAK"
                }
            } else {
                // Innings 2 Chasing
                if (runsAcc >= targetScore) {
                    // Win by wickets
                    val wktsLeft = maxWickets - wicketsAcc
                    winnerMsg = "$battingTeam ने यह मैच $wktsLeft विकेट से जीता!"
                    updatedStatus = "FINISHED"
                } else if (allOut || outOfBalls) {
                    // Batting team fails to chase
                    val runsDiff = targetScore - 1 - runsAcc
                    winnerMsg = "$bowlingTeam ने यह मैच $runsDiff रन से जीता!"
                    updatedStatus = "FINISHED"
                }
            }

            val finalMatchObj = nextMatch.copy(
                currentInnings = currentInnings,
                target = targetScore,
                status = updatedStatus,
                winnerMessage = winnerMsg
            )
            dao.updateMatch(finalMatchObj)

            // Rotate strike in database
            if (rotateStrike && !isWicket) {
                dao.updateMatchPlayer(updatedStriker.copy(isStriker = false))
                dao.updateMatchPlayer(runner.copy(isStriker = true))
            }

            // Auto show selectors if bowler needs changing or batsman needs replacing
            if (isWicket && wicketsAcc < maxWickets && updatedStatus == "LIVE") {
                // Batsman is out! Prompt select batsman dialog
                showBatsmanChangeDialog = true
                changingBatsmanIndex = if (striker.isStriker) 1 else 2
            } else if (isOverCompleted && updatedStatus == "LIVE" && !allOut) {
                // Bowler over finished! Prompt select new bowler dialog
                showBowlerChangeDialog = true
            }

            // Check if finished to process Leaderboard updates
            if (updatedStatus == "FINISHED") {
                finalizeMatchStats(finalMatchObj)
            }
        }
    }

    fun replaceOutBatsman(newBatsmanName: String) {
        val matchId = activeMatchId ?: return
        val battingTeam = if (activeMatchFlow.value?.currentInnings == 1) activeMatchFlow.value?.battingFirstTeam else {
            if (activeMatchFlow.value?.battingFirstTeam == activeMatchFlow.value?.teamAName) activeMatchFlow.value?.teamBName else activeMatchFlow.value?.teamAName
        } ?: return

        viewModelScope.launch {
            val players = dao.getMatchPlayers(matchId)
            val nextBattingOrder = (players.filter { it.teamName == battingTeam && it.battingOrder > 0 }.maxOfOrNull { it.battingOrder } ?: 0) + 1
            
            val updatedPlayers = players.map { player ->
                if (player.playerName == newBatsmanName) {
                    player.copy(isCurrentBatsman = true, isStriker = true, battingOrder = nextBattingOrder)
                } else {
                    player
                }
            }
            dao.insertMatchPlayers(updatedPlayers)
            showBatsmanChangeDialog = false
        }
    }

    fun selectNewBowler(newBowlerName: String) {
        val matchId = activeMatchId ?: return
        val bowlingTeam = if (activeMatchFlow.value?.currentInnings == 1) {
            if (activeMatchFlow.value?.battingFirstTeam == activeMatchFlow.value?.teamAName) activeMatchFlow.value?.teamBName else activeMatchFlow.value?.teamAName
        } else activeMatchFlow.value?.battingFirstTeam ?: return

        viewModelScope.launch {
            val players = dao.getMatchPlayers(matchId)
            val updatedPlayers = players.map { player ->
                when {
                    player.isCurrentBowler -> player.copy(isCurrentBowler = false)
                    player.playerName == newBowlerName -> player.copy(isCurrentBowler = true)
                    else -> player
                }
            }
            dao.insertMatchPlayers(updatedPlayers)
            showBowlerChangeDialog = false
            selectedBowler = newBowlerName
        }
    }

    fun changeBatsmanOption(currentOption: Int, newBatsmanName: String) {
        val matchId = activeMatchId ?: return
        viewModelScope.launch {
            val players = dao.getMatchPlayers(matchId)
            val oldBatsman = if (currentOption == 1) {
                players.find { it.isCurrentBatsman && it.isStriker }
            } else {
                players.find { it.isCurrentBatsman && !it.isStriker }
            }
            
            if (oldBatsman != null) {
                val updatedPlayers = players.map { player ->
                    when (player.id) {
                        oldBatsman.id -> player.copy(isCurrentBatsman = false, isStriker = false)
                        else -> if (player.playerName == newBatsmanName) {
                            player.copy(isCurrentBatsman = true, isStriker = currentOption == 1, battingOrder = (players.maxOfOrNull { it.battingOrder } ?: 0) + 1)
                        } else {
                            player
                        }
                    }
                }
                dao.insertMatchPlayers(updatedPlayers)
            }
        }
    }

    fun startSecondInnings() {
        val match = activeMatchFlow.value ?: return
        viewModelScope.launch {
            dao.updateMatch(match.copy(status = "LIVE", currentInnings = 2))
            
            // Clean bowler / batsman selection for 2nd innings
            val players = dao.getMatchPlayers(match.id)
            val battingTeam = if (match.battingFirstTeam == match.teamAName) match.teamBName else match.teamAName
            val bowlingTeam = match.battingFirstTeam

            // Clear current designations
            val updated = players.map { p ->
                p.copy(isCurrentBatsman = false, isStriker = false, isCurrentBowler = false)
            }
            dao.insertMatchPlayers(updated)

            // Select openers for 2nd Innings
            val batPlayers = updated.filter { p -> p.teamName == battingTeam }
            val bowlPlayers = updated.filter { p -> p.teamName == bowlingTeam }

            if (batPlayers.size >= 2 && bowlPlayers.isNotEmpty()) {
                val b1 = batPlayers[0].playerName
                val b2 = batPlayers[1].playerName
                val bBowler = bowlPlayers[0].playerName
                
                val finalOpeners = updated.map { player ->
                    when (player.playerName) {
                        b1 -> player.copy(isCurrentBatsman = true, isStriker = true, battingOrder = 1)
                        b2 -> player.copy(isCurrentBatsman = true, isStriker = false, battingOrder = 2)
                        bBowler -> player.copy(isCurrentBowler = true)
                        else -> player
                    }
                }
                dao.insertMatchPlayers(finalOpeners)
            }
        }
    }

    // Undo Last Ball with clean DB subtraction
    fun undoLastBall() {
        val matchId = activeMatchId ?: return
        viewModelScope.launch {
            val lastBall = dao.getLastBall(matchId) ?: return@launch
            
            // Play physical subtraction on match and player statistics
            val match = activeMatchFlow.value ?: return@launch
            val players = dao.getMatchPlayers(matchId)
            
            val striker = players.find { it.playerName == lastBall.batsmanName }
            val bowler = players.find { it.playerName == lastBall.bowlerName }

            // Subtract from batsman
            if (striker != null) {
                dao.updateMatchPlayer(striker.copy(
                    runsScored = maxOf(0, striker.runsScored - lastBall.runs),
                    ballsFaced = maxOf(0, striker.ballsFaced - if (!lastBall.isWide && !lastBall.isNoBall) 1 else 0),
                    fours = maxOf(0, striker.fours - if (lastBall.runs == 4) 1 else 0),
                    sixes = maxOf(0, striker.sixes - if (lastBall.runs == 6) 1 else 0),
                    isCurrentBatsman = true, // Force bring him back as current batsman
                    isStriker = true,
                    isOut = false,
                    wicketDescription = ""
                ))
            }

            // Subtract from bowler
            if (bowler != null) {
                dao.updateMatchPlayer(bowler.copy(
                    runsConceded = maxOf(0, bowler.runsConceded - (lastBall.runs + lastBall.extras)),
                    ballsBowled = maxOf(0, bowler.ballsBowled - if (!lastBall.isWide && !lastBall.isNoBall) 1 else 0),
                    wicketsTaken = maxOf(0, bowler.wicketsTaken - if (lastBall.isWicket && lastBall.wicketType != "Run Out") 1 else 0)
                ))
            }

            // Reconstruct match runs/wickets
            val updatedMatch = if (match.currentInnings == 1) {
                match.copy(
                    runsIn1 = maxOf(0, match.runsIn1 - (lastBall.runs + lastBall.extras)),
                    wicketsIn1 = maxOf(0, match.wicketsIn1 - if (lastBall.isWicket) 1 else 0),
                    ballsIn1 = maxOf(0, match.ballsIn1 - if (!lastBall.isWide && !lastBall.isNoBall) 1 else 0),
                    extrasIn1 = maxOf(0, match.extrasIn1 - lastBall.extras),
                    status = "LIVE" // reopen match if it was break
                )
            } else {
                match.copy(
                    runsIn2 = maxOf(0, match.runsIn2 - (lastBall.runs + lastBall.extras)),
                    wicketsIn2 = maxOf(0, match.wicketsIn2 - if (lastBall.isWicket) 1 else 0),
                    ballsIn2 = maxOf(0, match.ballsIn2 - if (!lastBall.isWide && !lastBall.isNoBall) 1 else 0),
                    extrasIn2 = maxOf(0, match.extrasIn2 - lastBall.extras),
                    status = "LIVE"
                )
            }
            dao.updateMatch(updatedMatch)
            dao.deleteBallById(lastBall.id)
        }
    }

    // Manual Edit Score dialog override
    fun applyManualOverride() {
        val matchId = activeMatchId ?: return
        val match = activeMatchFlow.value ?: return
        val r = overrideRuns.toIntOrNull() ?: return
        val w = overrideWickets.toIntOrNull() ?: return

        viewModelScope.launch {
            val updated = if (match.currentInnings == 1) {
                match.copy(runsIn1 = r, wicketsIn1 = w)
            } else {
                match.copy(runsIn2 = r, wicketsIn2 = w)
            }
            dao.updateMatch(updated)
            showManualEditDialog = false
        }
    }

    // Finalize match, calculate player of the match, save permanent player stats
    private suspend fun finalizeMatchStats(match: MatchEntity) {
        val players = dao.getMatchPlayers(match.id)
        
        // Find maximum run scorer & highest impact bowler
        val bestBatsman = players.maxByOrNull { it.runsScored }
        val bestBowler = players.maxByOrNull { it.wicketsTaken * 25 + (36 - (it.runsConceded.toDouble() / maxOf(1, it.ballsBowled) * 6) * 1.5).toInt() }

        var pomName = "खिलाड़ी"
        var pomReason = "बेहतरीन प्रदर्शन"

        if (bestBatsman != null && bestBowler != null) {
            val batPoints = bestBatsman.runsScored
            val bowlPoints = bestBowler.wicketsTaken * 25
            
            if (bowlPoints > batPoints && bestBowler.wicketsTaken >= 2) {
                pomName = bestBowler.playerName
                pomReason = "${bestBowler.wicketsTaken} विकेट लेकर मैच जिताऊ गेंदबाजी की!"
            } else {
                pomName = bestBatsman.playerName
                pomReason = "${bestBatsman.runsScored} रनों की शानदार पारी खेली!"
            }
        } else if (bestBatsman != null) {
            pomName = bestBatsman.playerName
            pomReason = "${bestBatsman.runsScored} रनों की शानदार पारी खेली!"
        }

        val finalizedMatch = match.copy(
            playerOfMatchName = pomName,
            playerOfMatchReason = pomReason
        )
        dao.updateMatch(finalizedMatch)

        // Write Stats to permanent table
        players.forEach { mp ->
            val pId = "${mp.playerName}_${mp.teamName}"
            val existingList = dao.getAllPlayers()
            val existing = existingList.find { it.id == pId }
            if (existing != null) {
                dao.insertPlayer(existing.copy(
                    matches = existing.matches + 1,
                    runs = existing.runs + mp.runsScored,
                    balls = existing.balls + mp.ballsFaced,
                    fours = existing.fours + mp.fours,
                    sixes = existing.sixes + mp.sixes,
                    wickets = existing.wickets + mp.wicketsTaken,
                    runsConceded = existing.runsConceded + mp.runsConceded,
                    ballsBowled = existing.ballsBowled + mp.ballsBowled
                ))
            } else {
                dao.insertPlayer(PlayerEntity(
                    id = pId,
                    name = mp.playerName,
                    team = mp.teamName,
                    matches = 1,
                    runs = mp.runsScored,
                    balls = mp.ballsFaced,
                    fours = mp.fours,
                    sixes = mp.sixes,
                    wickets = mp.wicketsTaken,
                    runsConceded = mp.runsConceded,
                    ballsBowled = mp.ballsBowled
                ))
            }
        }
    }
}
