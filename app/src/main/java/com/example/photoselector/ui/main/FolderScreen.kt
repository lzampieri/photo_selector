package com.example.photoselector.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.photoselector.ui.components.AlertDialogComponent
import com.example.photoselector.ui.theme.Modifiers
import java.net.URLDecoder


@Composable
fun FolderScreen(viewModel: AppViewModel, folderId: Int, onBackClick: () -> Unit ) {
    viewModel.selectFolder( folderId )
    val loading by viewModel.loading.collectAsState()

    Column( Modifiers.mainColumn() ) {
        FolderTitle( viewModel, onBackClick )
        DeleteFolder( viewModel, onBackClick )
    }
}

@Composable
fun FolderTitle( viewModel: AppViewModel, onBackClick: () -> Unit  )  {
    val folder by viewModel.selectedFolder.collectAsState()

    ListItem(
        headlineContent = { Text( text = folder?.id.toString() ?: "" ) }, // TODO rimettere folder?.name
        supportingContent = { Text( text = URLDecoder.decode( folder?.path ?: "", "UTF-8" ),
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant  ) },
        leadingContent = {
            Icon( Icons.AutoMirrored.Outlined.KeyboardArrowLeft, "",
                modifier = Modifier.clickable { onBackClick() } ) },
    )
}

@Composable
fun DeleteFolder( viewModel: AppViewModel, onBackClick: () -> Unit ) {
    var ruSure by remember { mutableStateOf( false ) }
    val folder by viewModel.selectedFolder.collectAsState()

    ListItem(
        headlineContent = { Text( text = "Cancella" ) },
        leadingContent = { Icon( Icons.Outlined.Delete, "" ) },
        modifier = Modifier.clickable( onClick = { ruSure = true } )
    )

    if( ruSure ) {
        AlertDialogComponent(
            confirmText = "Sì, cancella",
            onConfirmation = { ruSure = false; onBackClick(); viewModel.deleteSelectedFolder() },
            dismissText = "Annulla",
            onDismissRequest = { ruSure = false },
            dialogTitle = "Sei sicuro?",
            dialogText = "Confermi di voler cancellare la cartella " + folder?.name,
            icon = Icons.Outlined.Delete
        )
    }
}

