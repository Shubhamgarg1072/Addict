package com.time.applauncher.addict.feature.notes.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * [weekMask] packs the last seven days of completion into the low seven bits,
 * where bit 6 is today (matching [com.time.applauncher.addict.feature.notes.domain.Challenge.week]).
 */
@Entity(tableName = "challenges")
data class ChallengeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val shortLabel: String,
    val streak: Int,
    val targetDays: Int,
    val weekMask: Int,
    val reminder: String?,
    val position: Int
)
