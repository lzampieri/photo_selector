package com.example.photoselector.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.photoselector.ui.theme.Modifiers
import java.net.URLDecoder


@Composable
fun FolderScreen(viewModel: AppViewModel, folderId: Int, onBackClick: () -> Unit ) {
    viewModel.selectFolder( folderId )
    val loading by viewModel.loading.collectAsState()

    Column( Modifiers.mainColumn() ) {
        if( loading ) {
            Text( "Loading..." )
        } else {
            FolderTitle( viewModel, onBackClick )
        }
    }
}

@Composable
fun FolderTitle( viewModel: AppViewModel, onBackClick: () -> Unit  )  {
    val folder by viewModel.selectedFolder.collectAsState()

    ListItem(
        headlineContent = { Text( text = folder?.id.toString() ?: "" ) },
        supportingContent = { Text( text = URLDecoder.decode( folder?.path ?: "", "UTF-8" ),
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant  ) },
        leadingContent = {
            Icon( Icons.AutoMirrored.Outlined.KeyboardArrowLeft, "",
                modifier = Modifier.clickable { onBackClick() } ) },
    )
}

