package com.example.photoselector

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import com.example.photoselector.ui.theme.PhotoSelectorTheme
import androidx.lifecycle.lifecycleScope
import com.example.photoselector.ui.main.AppViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.photoselector.ui.main.Navigator
import java.net.URLDecoder

class MainActivity : ComponentActivity() {

    private lateinit var appViewModel: AppViewModel

    private val pickFolderIntentLauncher = registerForActivityResult( ActivityResultContracts.OpenDocumentTree() ) {
            uri: Uri? ->
        if( uri != null ) {
            lifecycleScope.launch( Dispatchers.IO ) {
                contentResolver.takePersistableUriPermission( uri, Intent.FLAG_GRANT_READ_URI_PERMISSION + Intent.FLAG_GRANT_WRITE_URI_PERMISSION )

                (application as PhotoselectorApplication).container.imagesRepository.addFolderIfNotExists(
                    uri.toString()
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Save activity result launcher in app container
        (application as PhotoselectorApplication).container.pickFolderIntentLauncher = pickFolderIntentLauncher

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