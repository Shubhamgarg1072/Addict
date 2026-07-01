package com.time.applauncher.addict.core.presentation

import com.time.applauncher.addict.core.domain.util.DataError

fun DataError.toUiText(): UiText = when (this) {
    DataError.Local.PERMISSION_DENIED -> UiText.DynamicString("Permission required to read this data.")
    DataError.Local.NOT_FOUND -> UiText.DynamicString("Nothing here yet.")
    DataError.Local.DISK_FULL -> UiText.DynamicString("Storage is full.")
    DataError.Local.UNKNOWN -> UiText.DynamicString("Something went wrong.")
    DataError.Network.NO_INTERNET -> UiText.DynamicString("You're offline.")
    DataError.Network.REQUEST_TIMEOUT -> UiText.DynamicString("The request timed out.")
    DataError.Network.SERVER_ERROR -> UiText.DynamicString("Server error. Try again later.")
    DataError.Network.SERIALIZATION -> UiText.DynamicString("Couldn't read the response.")
    DataError.Network.UNKNOWN -> UiText.DynamicString("Something went wrong.")
}
