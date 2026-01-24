package com.adriano.sharetheimage.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
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
import com.adriano.sharetheimage.ui.navigation.NavEvent.Back
import com.adriano.sharetheimage.ui.navigation.NavEvent.DetailsNavEntry
import com.adriano.sharetheimage.ui.navigation.NavEvent.HomeNavEntry
import com.adriano.sharetheimage.ui.shared.composables.SharedTransitionLayoutWithScopeProvider

@Composable
fun ShareTheImageNavDisplay(
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack(HomeNavEntry)

    SharedTransitionLayoutWithScopeProvider {
        CompositionLocalProvider(
            LocalNavigationListener provides { navEvent -> onNavigate(navEvent, backStack) }
        ) {
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
                        HomeScreen()
                    }
                    entry<DetailsNavEntry> { key ->
                        DetailScreen(photoId = key.photoId)
                    }
                }
            )
        }
    }
}

fun onNavigate(navEvent: NavEvent, backStack: NavBackStack<NavKey>) {
    when (navEvent) {
        Back -> backStack.removeLastOrNull()
        is DetailsNavEntry -> backStack.add(navEvent)
        HomeNavEntry -> backStack.add(HomeNavEntry)
    }
}

val LocalNavigationListener = compositionLocalOf<(NavEvent) -> Unit> { {} }