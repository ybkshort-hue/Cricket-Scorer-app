package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey val id: String, // format: "playerName_team"
    val name: String,
    val team: String,
    val matches: Int = 0,
    val runs: Int = 0,
    val balls: Int = 0,
    val fours: Int = 0,
    val sixes: Int = 0,
    val wickets: Int = 0,
    val runsConceded: Int = 0,
    val ballsBowled: Int = 0
) {
    val strikeRate: Double get() = if (balls > 0) (runs.toDouble() / balls) * 100.0 else 0.0
    val economy: Double get() = if (ballsBowled > 0) (runsConceded.toDouble() / ballsBowled) * 6.0 else 0.0
}

@Entity(tableName = "matches")
data class MatchEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val teamAName: String,
    val teamBName: String,
    val totalOvers: Int,
    val battingFirstTeam: String, // teamAName or teamBName
    val currentInnings: Int = 1, // 1 or 2
    val status: String = "SETUP", // "SETUP", "LIVE", "INNINGS_BREAK", "FINISHED"
    val date: Long = System.currentTimeMillis(),
    
    // Innings 1 Scores
    val runsIn1: Int = 0,
    val wicketsIn1: Int = 0,
    val ballsIn1: Int = 0,
    val extrasIn1: Int = 0,
    
    // Innings 2 Scores
    val runsIn2: Int = 0,
    val wicketsIn2: Int = 0,
    val ballsIn2: Int = 0,
    val extrasIn2: Int = 0,
    
    val target: Int = 0,
    val winnerMessage: String = "",
    val playerOfMatchName: String = "",
    val playerOfMatchReason: String = ""
)

@Entity(tableName = "match_players")
data class MatchPlayerEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val matchId: Int,
    val playerName: String,
    val teamName: String,
    val isCurrentBatsman: Boolean = false,
    val isStriker: Boolean = false,
    val isOut: Boolean = false,
    val wicketDescription: String = "", // e.g., "bowled by Shami"
    val runsScored: Int = 0,
    val ballsFaced: Int = 0,
    val fours: Int = 0,
    val sixes: Int = 0,
    
    val isCurrentBowler: Boolean = false,
    val runsConceded: Int = 0,
    val ballsBowled: Int = 0,
    val wicketsTaken: Int = 0,
    val battingOrder: Int = -1 // tracks when the player came out to bat
)

@Entity(tableName = "balls")
data class BallEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val matchId: Int,
    val inningsNum: Int,
    val overNum: Int,
    val ballNum: Int,
    val batsmanName: String,
    val bowlerName: String,
    val runs: Int, // runs scored by the batsman
    val isWide: Boolean,
    val isNoBall: Boolean,
    val isWicket: Boolean,
    val wicketType: String = "", // e.g., "Bowled", "Caught", "Run Out"
    val dismissedPlayer: String = "",
    val commentary: String,
    val extras: Int = 0, // extras logic (runs awarded to the team but not to the batsman)
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface CricketDao {
    @Query("SELECT * FROM players ORDER BY runs DESC")
    fun getLeaderboardByRuns(): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players ORDER BY wickets DESC")
    fun getLeaderboardByWickets(): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players")
    suspend fun getAllPlayers(): List<PlayerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayers(players: List<PlayerEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: PlayerEntity)

    // Matches
    @Query("SELECT * FROM matches ORDER BY date DESC")
    fun getAllMatchesFlow(): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches WHERE id = :matchId")
    suspend fun getMatchById(matchId: Int): MatchEntity?

    @Query("SELECT * FROM matches WHERE id = :matchId")
    fun getMatchByIdFlow(matchId: Int): Flow<MatchEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: MatchEntity): Long

    @Update
    suspend fun updateMatch(match: MatchEntity)

    // Match Players
    @Query("SELECT * FROM match_players WHERE matchId = :matchId")
    suspend fun getMatchPlayers(matchId: Int): List<MatchPlayerEntity>

    @Query("SELECT * FROM match_players WHERE matchId = :matchId")
    fun getMatchPlayersFlow(matchId: Int): Flow<List<MatchPlayerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatchPlayers(players: List<MatchPlayerEntity>)

    @Update
    suspend fun updateMatchPlayer(player: MatchPlayerEntity)

    // Balls
    @Query("SELECT * FROM balls WHERE matchId = :matchId ORDER BY timestamp DESC")
    fun getCommentaryFlow(matchId: Int): Flow<List<BallEntity>>

    @Query("SELECT * FROM balls WHERE matchId = :matchId")
    suspend fun getBallsForMatch(matchId: Int): List<BallEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBall(ball: BallEntity)

    @Query("DELETE FROM balls WHERE id = :ballId")
    suspend fun deleteBallById(ballId: Int)

    @Query("SELECT * FROM balls WHERE matchId = :matchId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastBall(matchId: Int): BallEntity?
}

@Database(
    entities = [
        PlayerEntity::class,
        MatchEntity::class,
        MatchPlayerEntity::class,
        BallEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CricketDatabase : RoomDatabase() {
    abstract fun cricketDao(): CricketDao

    companion object {
        @Volatile
        private var INSTANCE: CricketDatabase? = null

        fun getDatabase(context: Context): CricketDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CricketDatabase::class.java,
                    "cricket_scorer_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
