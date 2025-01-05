package com.example.photoselector.ui.models

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.photoselector.PhotoselectorApplication
import com.example.photoselector.R
import com.example.photoselector.data.Action
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SelectorViewModel (
    val folderId: Int,
    val app: Application
) : AndroidViewModel(app) {

    private val appContainer = (app as PhotoselectorApplication).container

    val actions : Flow<List<Action>> = appContainer.repository.getAllActions()

    var pagerState: PagerState? = null
    var corutineScope: CoroutineScope? = null

    fun swipe( delta: Int ) {
        if( !canSwipe( delta ) )
            return
        corutineScope?.launch {
            pagerState?.scrollToPage( pagerState!!.currentPage + delta )
        }
    }

    fun canSwipe( delta: Int ): Boolean {
        if( pagerState == null )
            return false
        if( pagerState!!.currentPage + delta < 0 )
            return false
        if( pagerState!!.currentPage + delta >= pagerState!!.pageCount )
            return false
        return true
    }

}