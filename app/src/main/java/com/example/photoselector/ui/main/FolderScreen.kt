package com.example.photoselector.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Delete
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.photoselector.R
import com.example.photoselector.data.Image
import com.example.photoselector.ui.components.AlertDialogComponent
import com.example.photoselector.ui.models.AppViewModel
import com.example.photoselector.ui.theme.Modifiers
import java.net.URLDecoder


@Composable
fun FolderScreen(viewModel: AppViewModel, folderId: Int, startScan: (Int) -> Unit, onBackClick: () -> Unit ) {
    viewModel.selectFolder( folderId )
    val loading by viewModel.loading.collectAsState()

    Column( Modifiers.mainColumn() ) {
        FolderTitle( viewModel, onBackClick )
        DeleteFolder( viewModel, onBackClick )
        StartScan( viewModel, startScan )
        ImagesList( viewModel )
    }
}

@Composable
fun FolderTitle(viewModel: AppViewModel, onBackClick: () -> Unit  )  {
    val folder by viewModel.selectedFolder.collectAsState()

    ListItem(
        headlineContent = { Text( text = folder?.name ?: "" ) },
        supportingContent = { Text( text = URLDecoder.decode( folder?.path ?: "", "UTF-8" ),
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant  ) },
        leadingContent = {
            Icon( Icons.AutoMirrored.Outlined.KeyboardArrowLeft, "",
                modifier = Modifier.clickable { onBackClick() } ) },
    )
}

@Composable
fun DeleteFolder(viewModel: AppViewModel, onBackClick: () -> Unit ) {
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

@Composable
fun StartScan(viewModel: AppViewModel, startScan: (Int) -> Unit ) {
    val folder by viewModel.selectedFolder.collectAsState()

    if( folder != null )
        ListItem(
            headlineContent = { Text(text = "Avvia scansione") },
            leadingContent = { Icon( painter = painterResource( R.drawable.outline_play_circle_outline_24 ), "" ) },
            modifier = Modifier.clickable(onClick = { startScan( folder!!.id ) })
        )
}

@Composable
fun ImagesList( viewModel: AppViewModel) {
    val imagesList by viewModel.images.collectAsState(listOf<Image>())
    LazyColumn ( ) {
        items( imagesList ) { k -> ImageBanner( k ) }
    }
}

@Composable
fun ImageBanner( image: Image ) {
    ListItem(
        headlineContent = { Text( text = image.name ) },
        leadingContent = { AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data( image.path )
                .crossfade(true)
                .build(),
            contentDescription = image.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.height( 70.dp ).width( 70.dp )
        )
        },
    )

}

