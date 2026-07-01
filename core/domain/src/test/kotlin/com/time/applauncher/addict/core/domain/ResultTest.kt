package com.time.applauncher.addict.core.domain

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.time.applauncher.addict.core.domain.util.DataError
import com.time.applauncher.addict.core.domain.util.Result
import com.time.applauncher.addict.core.domain.util.getOrNull
import com.time.applauncher.addict.core.domain.util.map
import com.time.applauncher.addict.core.domain.util.onFailure
import com.time.applauncher.addict.core.domain.util.onSuccess
import org.junit.jupiter.api.Test

class ResultTest {

    @Test
    fun `map transforms success and preserves error`() {
        val success: Result<Int, DataError.Local> = Result.Success(2)
        assertThat(success.map { it * 3 }.getOrNull()).isEqualTo(6)

        val error: Result<Int, DataError.Local> = Result.Error(DataError.Local.UNKNOWN)
        assertThat(error.map { it * 3 }.getOrNull()).isEqualTo(null)
    }

    @Test
    fun `onSuccess and onFailure fire on the matching branch only`() {
        var successHits = 0
        var failureHits = 0

        Result.Success(1).onSuccess { successHits++ }.onFailure { failureHits++ }
        Result.Error(DataError.Local.UNKNOWN).onSuccess { successHits++ }.onFailure { failureHits++ }

        assertThat(successHits).isEqualTo(1)
        assertThat(failureHits).isEqualTo(1)
    }
}
