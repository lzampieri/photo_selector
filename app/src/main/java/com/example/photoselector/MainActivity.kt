package com.example.photoselector

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import com.example.photoselector.ui.theme.PhotoSelectorTheme
import com.example.photoselector.ui.models.AppViewModel
import com.example.photoselector.ui.main.Navigator

class MainActivity : ComponentActivity() {

    private lateinit var appViewModel: AppViewModel

    private val pickFolderIntentLauncher = registerForActivityResult( ActivityResultContracts.OpenDocumentTree() ) {
            uri: Uri? ->
        if( uri != null ) {
            appViewModel.addFolder( uri )
        }
    }

    private val actionFolderIntentLauncher = registerForActivityResult( ActivityResultContracts.OpenDocumentTree() ) {
            uri: Uri? ->
        if( uri != null ) {
            appViewModel.actionsViewModel.setFolder( uri )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Save activity result launcher in app container
        (application as PhotoselectorApplication).container.pickFolderIntentLauncher = pickFolderIntentLauncher
        (application as PhotoselectorApplication).container.actionFolderIntentLauncher = actionFolderIntentLauncher

        // Start viewmodel
        appViewModel = AppViewModel( (application as PhotoselectorApplication).container )

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