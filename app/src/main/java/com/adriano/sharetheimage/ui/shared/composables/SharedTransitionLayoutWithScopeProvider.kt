package com.adriano.sharetheimage.ui.shared.composables

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.adriano.sharetheimage.ui.shared.LocalSharedTransitionScope

@Composable
fun SharedTransitionLayoutWithScopeProvider(
    content: @Composable SharedTransitionScope.() -> Unit
) {
    SharedTransitionLayout {
        CompositionLocalProvider(LocalSharedTransitionScope provides this) {
            content()
        }
    }
}