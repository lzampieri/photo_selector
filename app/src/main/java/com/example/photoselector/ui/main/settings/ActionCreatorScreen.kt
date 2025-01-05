package com.example.photoselector.ui.main.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedIconToggleButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.photoselector.R
import com.example.photoselector.ui.models.SettingsViewModel
import com.example.photoselector.ui.models.ActionIcons
import com.example.photoselector.ui.theme.Modifiers

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ActionCreatorScreen(viewModel: SettingsViewModel, navBack: () -> Unit ) {
    val name by viewModel.name.collectAsState()
    val copy by viewModel.copy.collectAsState()
    val path by viewModel.path.collectAsState()
    val icon by viewModel.icon.collectAsState()
    Column( modifier = Modifiers.mainColumn(), verticalArrangement = Arrangement.spacedBy( 30.dp ), horizontalAlignment = Alignment.CenterHorizontally ) {
        TextField(
            value = name,
            onValueChange = { viewModel.name.value = it },
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth()
        )
        SingleChoiceSegmentedButtonRow( modifier = Modifier.fillMaxWidth() ) {
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                onClick = { viewModel.copy.value = false },
                selected = !copy
            ) {
                Text("Sposta")
            }
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                onClick = { viewModel.copy.value = true },
                selected = copy
            ) {
                Text("Copia")
            }
        }
        FlowRow( horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth() ) {
            ActionIcons.forEach { ic ->
                OutlinedIconToggleButton (
                    checked = icon == ic,
                    onCheckedChange = { ck -> if( ck ) viewModel.icon.value = ic },
                    ) {
                    Icon( painter = painterResource( ic ), "" )
                }
            }
        }
        TextField(
            value = path,
            onValueChange = { },
            readOnly = true,
            label = { Text("Cartella") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = { OutlinedIconButton(
                onClick = { viewModel.appContainer.actionFolderIntentLauncher?.launch(null ) }
            )  {
                Icon( painter = painterResource( R.drawable.outline_create_new_folder_24 ), "Select folder" )
            }}
        )
        OutlinedButton (
            onClick = { viewModel.save(); navBack() },
            enabled = name.isNotEmpty() and path.isNotEmpty() and ( icon > 0 )
        ) {
            Icon( painter = painterResource( R.drawable.outline_save_24 ), "Save", modifier = Modifier.padding( end = 15.dp ) )
            Text("Salva")
        }
    }
}