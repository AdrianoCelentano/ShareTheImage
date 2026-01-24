package com.adriano.sharetheimage.ui.shared.modifier

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.adriano.sharetheimage.ui.shared.LocalSharedTransitionScope

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.sharedBoundsWithTransitionScope(
    key: Any,
    renderInOverlay: Boolean = false
): Modifier {
    if (LocalInspectionMode.current) return this
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedVisibilityScope = LocalNavAnimatedContentScope.current
    if (sharedTransitionScope == null || animatedVisibilityScope == null) return this
    return with(sharedTransitionScope) {
        this@sharedBoundsWithTransitionScope.sharedBounds(
            sharedContentState = rememberSharedContentState(key = key),
            animatedVisibilityScope = animatedVisibilityScope,
            renderInOverlayDuringTransition = renderInOverlay
        )
    }
}
