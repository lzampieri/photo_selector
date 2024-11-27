package com.example.photoselector.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

object Modifiers {

    @Composable
    fun mainColumn(): Modifier { return Modifier.fillMaxSize().background( MaterialTheme.colorScheme.surface ) }

    @Composable
    fun scrollableColumn(): Modifier { return Modifier.fillMaxSize().verticalScroll( rememberScrollState() ) }
}