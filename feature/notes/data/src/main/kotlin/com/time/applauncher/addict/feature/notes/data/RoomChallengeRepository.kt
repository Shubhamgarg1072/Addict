package com.time.applauncher.addict.feature.notes.data

import com.time.applauncher.addict.feature.notes.data.db.ChallengeDao
import com.time.applauncher.addict.feature.notes.data.db.ChallengeEntity
import com.time.applauncher.addict.feature.notes.domain.Challenge
import com.time.applauncher.addict.feature.notes.domain.ChallengeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomChallengeRepository(
    private val dao: ChallengeDao
) : ChallengeRepository {

    override fun observeChallenges(): Flow<List<Challenge>> =
        dao.observeChallenges().map { list -> list.map { it.toChallenge() } }

    override suspend fun setDoneToday(id: Long, done: Boolean) {
        val entity = dao.getById(id) ?: return
        val wasDone = entity.weekMask.bit(Challenge.LAST_DAY)
        if (wasDone == done) return
        val newMask =
            if (done) entity.weekMask or (1 shl Challenge.LAST_DAY)
            else entity.weekMask and (1 shl Challenge.LAST_DAY).inv()
        val newStreak = (entity.streak + if (done) 1 else -1).coerceAtLeast(0)
        dao.updateProgress(id, newStreak, newMask)
    }

    override suspend fun cycleReminder(id: Long) {
        val entity = dao.getById(id) ?: return
        val idx = Challenge.REMINDERS.indexOf(entity.reminder)
        val next = Challenge.REMINDERS[(idx + 1) % Challenge.REMINDERS.size]
        dao.updateReminder(id, next)
    }

    override suspend fun addChallenge(title: String, shortLabel: String, targetDays: Int): Long =
        dao.insert(
            ChallengeEntity(
                title = title,
                shortLabel = shortLabel,
                streak = 0,
                targetDays = targetDays,
                weekMask = 0,
                reminder = null,
                position = dao.nextPosition()
            )
        )

    override suspend fun deleteChallenge(id: Long) = dao.delete(id)
}

private fun Int.bit(index: Int): Boolean = (this shr index) and 1 == 1

private fun ChallengeEntity.toChallenge(): Challenge = Challenge(
    id = id,
    title = title,
    shortLabel = shortLabel,
    streak = streak,
    targetDays = targetDays,
    week = (0 until Challenge.WEEK_DAYS).map { weekMask.bit(it) },
    reminder = reminder
)
