package com.example.photoselector.ui.main.selector

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.photoselector.R
import com.example.photoselector.data.ImageAndAction
import com.example.photoselector.ui.components.AlertDialogComponent
import com.example.photoselector.ui.components.CircledIcon
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
        StartExecution( viewModel )
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
fun StartExecution(viewModel: AppViewModel ) {
    val images by viewModel.images.collectAsState( listOf<ImageAndAction>() )
    if( images.isEmpty() )
        return
    val count = images.fold( 0 ) { sum, image -> sum + ( if( !image.image.actionDone and (image.action != null) ) 1 else 0 ) }

    if( count > 0 )
        ListItem(
            headlineContent = { Text(text = "Esegui le $count azioni in coda") },
            leadingContent = { Icon( painter = painterResource( R.drawable.outline_skip_next_24 ), "" ) },
            modifier = Modifier.clickable(onClick = { viewModel.runActions( ) })
        )
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
    val imagesList by viewModel.images.collectAsState(listOf<ImageAndAction>())
    LazyColumn ( ) {
        items( imagesList ) { k -> ImageBanner( k ) }
    }
}

@Composable
fun ImageBanner( iaa: ImageAndAction ) {
    ListItem(
        headlineContent = { Text( text = iaa.image.name ) },
        leadingContent = {
            Box(
                Modifier
                    .width(70.dp)
                    .height(70.dp) ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(iaa.image.path)
                        .crossfade(true)
                        .build(),
                    contentDescription = iaa.image.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                )
                if( iaa.image.actionId != null )
                    CircledIcon( getActionIcon( iaa ), iaa.image.actionDone, modifier = Modifier.align( Alignment.Center ) )
            }
        },
    )
}

fun getActionIcon( iia: ImageAndAction ): Int {
    if( iia.action == null )
        return R.drawable.outline_question_mark_24
    return iia.action.icon
}

