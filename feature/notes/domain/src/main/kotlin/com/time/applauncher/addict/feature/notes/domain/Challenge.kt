package com.time.applauncher.addict.feature.notes.domain

import kotlinx.coroutines.flow.Flow

/**
 * A habit the user is building, set up from the Notes screen. [week] holds the last
 * seven days of completion, oldest first, so `week[6]` is today. [streak] is the current
 * run length in days and [targetDays] the goal the progress bar fills toward.
 */
data class Challenge(
    val id: Long,
    val title: String,
    val shortLabel: String,
    val streak: Int,
    val targetDays: Int,
    val week: List<Boolean>,
    val reminder: String?
) {
    val doneToday: Boolean get() = week.getOrElse(LAST_DAY) { false }

    val progress: Float
        get() = if (targetDays <= 0) 0f else (streak.toFloat() / targetDays).coerceIn(0f, 1f)

    companion object {
        const val WEEK_DAYS = 7
        const val LAST_DAY = WEEK_DAYS - 1

        /** Reminder times cycled through by [ChallengeRepository.cycleReminder]; `null` is "off". */
        val REMINDERS: List<String?> = listOf(null, "07:00", "09:00", "12:00", "18:00", "21:00")
    }
}

interface ChallengeRepository {
    fun observeChallenges(): Flow<List<Challenge>>

    /** Toggle today's completion for [id], adjusting the streak by ±1. */
    suspend fun setDoneToday(id: Long, done: Boolean)

    /** Advance the reminder to the next value in [Challenge.REMINDERS]. */
    suspend fun cycleReminder(id: Long)

    /** @return the id of the created challenge. */
    suspend fun addChallenge(title: String, shortLabel: String, targetDays: Int): Long

    suspend fun deleteChallenge(id: Long)
}
