package com.example.photoselector.ui.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoAppBar( onRefresh: () -> Unit, onSettings: () -> Unit ) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        title = { Text("Photo selector") },
        actions = {
            IconButton( onClick = onRefresh ) {
                Icon( Icons.Outlined.Refresh, "Refresh" )
            }
            IconButton( onClick = onSettings ) {
                Icon( Icons.Outlined.Settings, "Settings" )
            }
        }
    )
}
