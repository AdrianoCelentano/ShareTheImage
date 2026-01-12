package com.adriano.sharetheimage.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.adriano.sharetheimage.ui.detail.DetailScreen
import com.adriano.sharetheimage.ui.home.HomeScreen

@Composable
fun ShareImageNavDisplay(
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack(HomeNavEntry)

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
                    onPhotoClick = { photoId -> backStack.add(DetailsNavEntry(photoId)) }
                )
            }
            entry<DetailsNavEntry> { key ->
                DetailScreen(
                    photoId = key.photoId,
                    onBackClick = { backStack.removeLastOrNull() }
                )
            }
        }
    )
}