package com.example.photoselector.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun CircledIcon( painterId: Int, active: Boolean = false, modifier: Modifier = Modifier ) {
    Box( modifier = modifier.clip( CircleShape ).background(
        if( active ) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
    ).padding( 5.dp ) ) {
        Icon(
            painter = painterResource( painterId ), "",
            tint = (
                    if( active ) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
                    ),
            modifier = Modifier.size( 30.dp )
        )
    }
}