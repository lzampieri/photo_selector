package com.example.photoselector.ui.main

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.photoselector.data.Folder
import com.example.photoselector.ui.ViewModelProvider

@Composable
fun MainScreen(
    getFolderIntentLauncher: ActivityResultLauncher<Uri?>,
    mainViewModel: MainViewModel = viewModel( factory = ViewModelProvider.Factory ) ) {
    FoldersList( mainViewModel, getFolderIntentLauncher )
}

@Composable
private fun FoldersList(mainViewModel: MainViewModel, getFolderIntentLauncher: ActivityResultLauncher<Uri?>) {
    val foldersList by mainViewModel.folders.collectAsState(listOf<Folder>())
    Column {
        foldersList.forEach() { k -> FolderBanner( k.path ) }
        RandomFolderButton( onClick = {
            getFolderIntentLauncher.launch(null )
        } )
    }
}

@Composable
private fun FolderBanner(name: String) {
    Text(
        text = "Folder $name!"
    )
}

@Composable
private fun RandomFolderButton( onClick: () -> Unit ) {
    Button ( onClick = onClick ) {
        Text(text = "Pick a random folder")
    }
}