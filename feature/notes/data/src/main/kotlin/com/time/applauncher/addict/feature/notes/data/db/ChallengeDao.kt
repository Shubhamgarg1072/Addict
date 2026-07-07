package com.time.applauncher.addict.feature.notes.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChallengeDao {

    @Query("SELECT * FROM challenges ORDER BY position ASC")
    fun observeChallenges(): Flow<List<ChallengeEntity>>

    @Query("SELECT * FROM challenges WHERE id = :id")
    suspend fun getById(id: Long): ChallengeEntity?

    @Insert
    suspend fun insert(challenge: ChallengeEntity): Long

    @Query("UPDATE challenges SET streak = :streak, weekMask = :weekMask WHERE id = :id")
    suspend fun updateProgress(id: Long, streak: Int, weekMask: Int)

    @Query("UPDATE challenges SET reminder = :reminder WHERE id = :id")
    suspend fun updateReminder(id: Long, reminder: String?)

    @Query("DELETE FROM challenges WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT COALESCE(MAX(position), -1) + 1 FROM challenges")
    suspend fun nextPosition(): Int
}
