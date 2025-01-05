package com.example.photoselector.ui.main

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.photoselector.ui.main.selector.SelectorScaffold
import com.example.photoselector.ui.main.settings.SettingsScaffold
import com.example.photoselector.ui.models.AppViewModel
import kotlinx.serialization.Serializable

@Serializable
object MainDestinations {

    @Serializable
    object FoldersIndex

    @Serializable
    data class FolderContent(val folderId: Int)

    @Serializable
    data class Selector(val folderId: Int)

    @Serializable
    object Settings
}

// TODO reinserire
// topBar = { PhotoAppBar( onSettings = { navController.navigate( NavDestination.Settings )} ) }

@Composable
fun Navigator(
    viewModel: AppViewModel,
    navController: NavHostController = rememberNavController()
) {
    val loading by viewModel.loading.collectAsState()
    val imagesDbLoading by viewModel.imagesDbLoading.collectAsState()

    Scaffold( topBar = { PhotoAppBar( onSettings = { navController.navigate( MainDestinations.Settings )} ) } ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            NavHost(
                navController = navController,
                startDestination = MainDestinations.FoldersIndex,
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None }
            ) {
                composable<MainDestinations.FoldersIndex> {
                    FoldersIndexScreen(
                        viewModel,
                        onFolderSelect = { folderId ->
                            navController.navigate(
                                MainDestinations.FolderContent(folderId)
                            )
                        })
                }
                composable<MainDestinations.FolderContent>(
                ) { backStackEntry ->
                    val destination: MainDestinations.FolderContent = backStackEntry.toRoute()
                    FolderScreen(
                        viewModel,
                        destination.folderId,
                        startScan = { folderId -> navController.navigate( MainDestinations.Selector( folderId ) ) },
                        onBackClick = { navController.popBackStack() })
                }
                composable<MainDestinations.Selector>(
                ) { backStackEntry ->
                    val destination: MainDestinations.FolderContent = backStackEntry.toRoute()
                    SelectorScaffold(
                        viewModel,
                        destination.folderId,
                        onBackClick = { navController.popBackStack() })
                }
                composable<MainDestinations.Settings>(
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Up,
                            tween(300)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Down,
                            tween(300)
                        )
                    }
                ) {
                    SettingsScaffold( navController, viewModel )
                }
            }
            if( loading + imagesDbLoading > 0 ) {
                Box(
                    Modifier
                        .matchParentSize()
                        .background(MaterialTheme.colorScheme.surfaceDim),
                    contentAlignment = Alignment.Center
                ) {
                    Column {
                        CircularProgressIndicator()
                        if( imagesDbLoading > 0 ) {
                            Text( text = "Scansione delle cartelle in corso....")
                        }
                    }

                }
            }
        }
    }
}