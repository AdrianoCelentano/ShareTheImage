package com.adriano.sharetheimage.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object HomeNavEntry : NavKey

@Serializable
data class DetailsNavEntry(val photoId: String) : NavKey