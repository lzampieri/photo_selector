package com.example.photoselector

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.photoselector.ui.theme.PhotoSelectorTheme
import com.example.photoselector.ui.models.AppViewModel
import com.example.photoselector.ui.main.Navigator
import com.example.photoselector.ui.main.settings.SettingsScaffold
import com.example.photoselector.ui.models.ActionsViewModel

class MainActivity : ComponentActivity() {

    private val appViewModel: AppViewModel by viewModels()

    private val pickFolderIntentLauncher = registerForActivityResult( ActivityResultContracts.OpenDocumentTree() ) {
            uri: Uri? ->
        if( uri != null ) {
            appViewModel.addFolder( uri )
        }
    }

    private val actionFolderIntentLauncher = registerForActivityResult( ActivityResultContracts.OpenDocumentTree() ) {
            uri: Uri? ->
        if( uri != null ) {
            //ViewModelProvider( SettingsScaffold)
            val viewModel: ActionsViewModel by viewModels()
            viewModel.setFolder( uri )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Save activity result launcher in app container
        (application as PhotoselectorApplication).container.pickFolderIntentLauncher = pickFolderIntentLauncher
        (application as PhotoselectorApplication).container.actionFolderIntentLauncher = actionFolderIntentLauncher
        (application as PhotoselectorApplication).container.activity = this

        enableEdgeToEdge()
        setContent {
            PhotoSelectorTheme {
                Navigator( appViewModel )
            }
        }
    }
}

/*@Preview(showBackground = true)
@Composable
fun FoldersListPreview() {
    PhotoSelectorTheme {
        FoldersList()
    }
}*/