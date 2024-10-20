package com.example.photoselector

import android.content.Intent
import android.content.UriPermission
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.photoselector.ui.theme.PhotoSelectorTheme
import androidx.compose.material3.Button
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.lifecycleScope
import com.example.photoselector.data.Folder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.photoselector.ui.main.MainViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.photoselector.ui.main.MainScreen

class MainActivity : ComponentActivity() {

    private val getFolderPath = registerForActivityResult( ActivityResultContracts.OpenDocumentTree() ) {
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
        enableEdgeToEdge()
        setContent {
            PhotoSelectorTheme {
                MainScreen( getFolderPath )
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