package com.example.photoselector.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.photoselector.R
import com.example.photoselector.data.Folder
import com.example.photoselector.ui.theme.Modifiers
import java.net.URLDecoder

@Composable
fun FoldersIndexScreen(viewModel: AppViewModel, onFolderSelect: (Int) -> Unit ) {
    val foldersList by viewModel.folders.collectAsState(listOf<Folder>())
    Column( Modifiers.mainColumn() ) {
        foldersList.forEach() { k -> FolderBanner( k, onFolderSelect ) }
        PickFolderButton( onClick = {
            viewModel.appContainer.pickFolderIntentLauncher?.launch(null )
        } )
    }
}

@Composable
private fun FolderBanner(folder: Folder, onFolderSelect: (Int) -> Unit ) {
    ListItem(
        headlineContent = { Text( text = folder.name ) },
        supportingContent = { Text( text = URLDecoder.decode( folder.path, "UTF-8" ),
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant  ) },
        leadingContent = { Icon( painter = painterResource( R.drawable.outline_folder_open_24 ), "" ) },
        trailingContent = { Icon( Icons.AutoMirrored.Outlined.KeyboardArrowRight, "" )},
        modifier = Modifier.clickable { onFolderSelect( folder.id )},
    )
}

@Composable
private fun PickFolderButton(onClick: () -> Unit ) {
    Button ( onClick = onClick ) {
        Text(text = "Pick a random folder")
    }
}