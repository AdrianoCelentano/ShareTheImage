package com.adriano.sharetheimage.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.adriano.sharetheimage.ui.detail.DetailScreen
import com.adriano.sharetheimage.ui.home.HomeScreen
import com.adriano.sharetheimage.ui.shared.composables.SharedTransitionLayoutWithScopeProvider

@Composable
fun ShareImageNavDisplay(
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack(HomeNavEntry)

    SharedTransitionLayoutWithScopeProvider {
        NavDisplay(
            modifier = modifier,
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                entry<HomeNavEntry> {
                    HomeScreen(
                        onPhotoClick = { photoId -> backStack.add(DetailsNavEntry(photoId)) },
                    )
                }
                entry<DetailsNavEntry> { key ->
                    DetailScreen(
                        photoId = key.photoId,
                        onBackClick = { onBackClick(backStack) }
                    )
                }
            }
        )
    }
}

private fun onBackClick(backStack: NavBackStack<NavKey>) {
    // Ensure there is at least one entry left in the backstack after popping.
    // In Navigation3, an empty backstack means NavDisplay has nothing to render,
    // which would result in an Exception being thrown.
    // This could happen if the user is clicking back fast two times in a row
    if (backStack.size > 1) backStack.removeLastOrNull()
}
