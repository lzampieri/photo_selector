package com.example.photoselector.ui.main.settings

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import com.example.photoselector.R
import com.example.photoselector.data.Action
import com.example.photoselector.ui.components.AlertDialogComponent
import com.example.photoselector.ui.models.ActionsViewModel
import com.example.photoselector.ui.models.ActionIcons
import com.example.photoselector.ui.models.AppViewModel
import com.example.photoselector.ui.theme.Modifiers
import java.net.URLDecoder

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SettingsScreen(viewModel: ActionsViewModel, onNewAction: () -> Unit ) {
    val actionsList by viewModel.actions.collectAsState(listOf<Action>())
    Scaffold( floatingActionButton = {
        FloatingActionButton( onClick = onNewAction ) {
            Icon( Icons.Outlined.Add, "Add action" )
        }
    } ) {
        Column(Modifiers.mainColumn()) {
            Column(Modifiers.scrollableColumn()) {
                actionsList.forEach() { k -> ActionBanner(k, viewModel) }
            }
        }
    }
}

fun computeFolderName( path: String? ): String {
    if( path == null )
        return ""
    return " - " + URLDecoder.decode( path.substring( minOf( maxOf (
        path.indexOf( "%2F", path.indexOf("com.", path.indexOf("com.") + 1 ) + 1 ) + 3,
        path.indexOf("tree/primary") + 13,
        0 ), path.length )
    ), "UTF-8" )
}

@Composable
fun ActionBanner(action: Action, viewModel: ActionsViewModel) {
    ListItem(
        headlineContent = { Text( text = action.name ) },
        supportingContent = { Column {
            Text( text = ( if( action.copy ) "Copia" else "Sposta" ) + computeFolderName( action.path ))
        } },
        leadingContent = { Icon( painter = painterResource( if( action.icon in ActionIcons ) action.icon else R.drawable.outline_question_mark_24 ), "" ) },
        trailingContent = { DeleteButton( action, viewModel ) },
    )
}

@Composable
fun DeleteButton( action: Action, viewModel: ActionsViewModel ) {
    var ruSure by remember { mutableStateOf( false ) }

    IconButton( onClick = { ruSure = true } ) { Icon( Icons.Outlined.Delete , "" ) }

    if( ruSure ) {
        AlertDialogComponent(
            confirmText = "Sì, cancella",
            onConfirmation = { ruSure = false; viewModel.deleteAction( action ) },
            dismissText = "Annulla",
            onDismissRequest = { ruSure = false },
            dialogTitle = "Sei sicuro?",
            dialogText = "Confermi di voler cancellare l'azione " + action.name,
            icon = Icons.Outlined.Delete
        )
    }
}