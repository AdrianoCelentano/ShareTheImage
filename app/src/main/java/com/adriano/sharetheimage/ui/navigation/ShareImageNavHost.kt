package com.adriano.sharetheimage.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.adriano.sharetheimage.ui.detail.DetailScreen
import com.adriano.sharetheimage.ui.home.HomeScreen

@Composable
fun ShareImageNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = modifier
    ) {
        composable<HomeRoute> {
            HomeScreen(
                onPhotoClick = { photoId ->
                    navController.navigate(DetailRoute(photoId))
                }
            )
        }
        composable<DetailRoute> {
            DetailScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
