package com.example.photoselector.ui.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
object NavDestination {

    @Serializable
    object FoldersIndex

    @Serializable
    data class FolderContent( val folderId: Int )
}

@Composable
fun Navigator(
    viewModel: AppViewModel,
    navController: NavHostController = rememberNavController()
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp)
        ) {
            NavHost(
                navController = navController,
                startDestination = NavDestination.FoldersIndex,
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None }
            ) {
                composable<NavDestination.FoldersIndex> {
                    FoldersIndexScreen(viewModel, onFolderSelect = { folderId -> navController.navigate( NavDestination.FolderContent( folderId ) ) })
                }
                composable<NavDestination.FolderContent>(
                    enterTransition = { slideIntoContainer( AnimatedContentTransitionScope.SlideDirection.Start, tween(300) ) },
                    exitTransition = { slideOutOfContainer( AnimatedContentTransitionScope.SlideDirection.End, tween(300) ) }
                ) { backStackEntry ->
                    val destination: NavDestination.FolderContent = backStackEntry.toRoute()
                    FolderScreen(viewModel, destination.folderId, onBackClick = { navController.popBackStack() } )
                }
            }
        }
    }
}