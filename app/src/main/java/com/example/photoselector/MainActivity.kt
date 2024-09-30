package com.example.photoselector

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import com.example.photoselector.data.Folder
import com.example.photoselector.data.ImagesRepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val getFolderPath = registerForActivityResult( ActivityResultContracts.OpenDocumentTree() ) {
        uri: Uri? ->
        if( uri != null ) {
            lifecycleScope.launch( Dispatchers.IO ) {
                (application as PhotoselectorApplication).container.imagesRepository.addFolderIfNotExists(
                    uri ?. path ?: ""
                )
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PhotoSelectorTheme {
                FoldersList( (application as PhotoselectorApplication).container.imagesRepository, getFolderPath )
            }
        }
    }
}

@Composable
fun FoldersList(imagesRepository: ImagesRepositoryInterface, getFolderIntentLauncher: ActivityResultLauncher<Uri?> ) {
    val foldersList by imagesRepository.getAllFolders().collectAsState(listOf<Folder>())
    Column {
        foldersList.forEach() { k -> FolderBanner( k.path ) }
        RandomFolderButton( onClick = {
            getFolderIntentLauncher.launch(null )
        } )
    }
}

@Composable
fun FolderBanner(name: String) {
    Text(
        text = "Folder $name!"
    )
}

@Composable
fun RandomFolderButton( onClick: () -> Unit ) {
    Button ( onClick = onClick ) {
        Text(text = "Pick a random folder")
    }
}

/*@Preview(showBackground = true)
@Composable
fun FoldersListPreview() {
    PhotoSelectorTheme {
        FoldersList()
    }
}*/