package com.example.photoselector.ui.main.selector

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedIconToggleButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.photoselector.R
import com.example.photoselector.data.Action
import com.example.photoselector.data.FolderAndCounts
import com.example.photoselector.data.Image
import com.example.photoselector.data.ImageAndAction
import com.example.photoselector.ui.components.CircledIcon
import com.example.photoselector.ui.models.AppViewModel
import com.example.photoselector.ui.models.SelectorViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SelectorScaffold( viewModel: AppViewModel, folderId: Int, onBackClick: () -> Unit ) {
    val selectorViewModel: SelectorViewModel = viewModel { SelectorViewModel( folderId, viewModel.app ) }
    selectorViewModel.corutineScope = rememberCoroutineScope()

    val loaded by selectorViewModel.loaded.collectAsState()

    Scaffold { innerPadding ->
        Column( modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()  ) {
            SelectorTop( onBackClick )
            Box( modifier = Modifier
                .fillMaxWidth()
                .weight(1f) ) {
                if( loaded )
                    SelectorImageScroller( selectorViewModel )
                else
                    LoadingBox()
            }
            SelectorChoices( selectorViewModel )
        }
    }

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SelectorChoices(selectorViewModel: SelectorViewModel ) {
    val actions by selectorViewModel.actions.collectAsState(listOf<Action>())

    FlowRow( horizontalArrangement = Arrangement.Center, modifier =  Modifier.fillMaxWidth() ) {
        OutlinedIconButton ( onClick = { selectorViewModel.swipe( -1 ) }, enabled = selectorViewModel.canSwipe( -1 )
        ) {
            Icon( painter = painterResource( R.drawable.outline_chevron_left_24 ), "" )
        }
        actions.forEach { act ->
            OutlinedIconButton ( onClick = { selectorViewModel.saveAction( act.id ) }
            ) {
                Icon( painter = painterResource( act.icon ), "" )
            }
        }
        OutlinedIconButton ( onClick = { selectorViewModel.swipe( +1 ) }, enabled = selectorViewModel.canSwipe( +1 )
        ) {
            Icon( painter = painterResource( R.drawable.outline_chevron_right_24 ), "" )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SelectorTop( onBackClick: () -> Unit ) {

    FlowRow( horizontalArrangement = Arrangement.End, modifier =  Modifier.fillMaxWidth() ) {
        OutlinedIconButton ( onClick = onBackClick ) {
            Icon( painter = painterResource( R.drawable.baseline_close_24 ), "" )
        }
    }
}

@Composable
fun SelectorImageScroller( selectorViewModel: SelectorViewModel ) {
    val images by selectorViewModel.images.collectAsState(listOf<ImageAndAction>())

    HorizontalPager(state = selectorViewModel.pagerState, modifier = Modifier.fillMaxSize()) { page ->
        if( page < images.count() )
            Box( Modifier.fillMaxSize() ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data( images[ page ].image.path )
                        .crossfade(true)
                        .build(),
                    contentDescription = images[ page ].image.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
                if( images[ page ].action != null ) {
                    CircledIcon( images[ page ].action!!.icon, active = false, modifier = Modifier.align( Alignment.TopCenter ) )
                }
            }
        else
            LoadingBox()
    }

}

@Composable
fun LoadingBox() {
    Box( Modifier.fillMaxSize(), contentAlignment = Alignment.Center ) {
        CircularProgressIndicator()
    }
}