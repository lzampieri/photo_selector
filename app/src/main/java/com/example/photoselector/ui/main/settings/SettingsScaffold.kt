package com.example.photoselector.ui.main.settings

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.photoselector.ui.models.ActionsViewModel
import com.example.photoselector.ui.models.AppViewModel
import kotlinx.serialization.Serializable

@Serializable
object SettingsDestination {

    @Serializable
    object AllSettings

    @Serializable
    object ActionCreator
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScaffold( upperNavController: NavHostController, appViewModel: AppViewModel ) {
    val actionsViewModel: ActionsViewModel = viewModel<ActionsViewModel>( viewModelStoreOwner = appViewModel.appContainer.activity!! )

    Scaffold(
        topBar = { TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            title = { Text("Azioni disponibili") },
            actions = {
                IconButton( onClick = { upperNavController.popBackStack() } ) {
                    Icon( Icons.Outlined.Close, "Close" )
                }
            }
        ) }
    ) {  innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            NavHost(
                navController = navController,
                startDestination = SettingsDestination.AllSettings,
                ) {
                composable<SettingsDestination.AllSettings> {
                    SettingsScreen( actionsViewModel ) { navController.navigate(SettingsDestination.ActionCreator) }
                }
                composable<SettingsDestination.ActionCreator>(
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Start,
                            tween(300)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.End,
                            tween(300)
                        )
                    }
                ) {
                    ActionCreatorScreen( actionsViewModel ) { navController.navigate(SettingsDestination.AllSettings) }
                }
            }
        }

    }
}