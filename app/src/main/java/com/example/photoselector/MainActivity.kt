package com.example.photoselector

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.photoselector.ui.theme.PhotoSelectorTheme
import androidx.compose.material3.Button
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PhotoSelectorTheme {
                FoldersList()
            }
        }
    }
}

@Composable
fun FoldersList( ) {
    var folderId by remember { mutableIntStateOf(1) }
    Column {
        FolderBanner("Prima cartella")
        FolderBanner("Seconda cartella")
        FolderBanner("Terza cartella")
        Text( "Folder picked $folderId" )
        RandomFolderButton( onClick = { folderId = (1..6).random() } )
    }
}

@Composable
fun FolderBanner(name: String) {
    Text(
        text = "Folder $name!",
    )
}

@Composable
fun RandomFolderButton( onClick: () -> Unit ) {
    Button ( onClick = onClick ) {
        Text(text = "Pick a random folder")
    }
}

@Preview(showBackground = true)
@Composable
fun FoldersListPreview() {
    PhotoSelectorTheme {
        FoldersList()
    }
}