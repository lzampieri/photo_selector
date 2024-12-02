package com.example.photoselector.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

object Modifiers {

    @Composable
    fun mainColumn(): Modifier { return Modifier.fillMaxSize().background( MaterialTheme.colorScheme.surface ).padding( 20.dp ) }

    @Composable
    fun scrollableColumn(): Modifier { return Modifier.fillMaxSize().verticalScroll( rememberScrollState() ) }
}

object Arrangements {

    @Composable
    fun mainColumn(): Arrangement.HorizontalOrVertical { return Arrangement.spacedBy( 10.dp ) }
}