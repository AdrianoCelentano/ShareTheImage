package com.adriano.sharetheimage.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed class NavEvent {
    data object Back : NavEvent()

    @Serializable
    data object HomeNavEntry : NavKey, NavEvent()

    @Serializable
    data class DetailsNavEntry(val photoId: String) : NavKey, NavEvent()
}