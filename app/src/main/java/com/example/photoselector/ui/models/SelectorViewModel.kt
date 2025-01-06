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
import com.example.photoselector.data.FolderAndCounts
import com.example.photoselector.data.Image
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

class SelectorViewModel (
    val folderId: Int,
    val app: Application,
) : AndroidViewModel(app) {

    private val appContainer = (app as PhotoselectorApplication).container

    val actions : Flow<List<Action>> = appContainer.repository.getAllActions()

    val images : Flow<List<Image>> = appContainer.repository.getImagesFromFolder( folderId )
    val loaded = MutableStateFlow( false )
    //val imagesCount: Flow<Int> = appContainer.repository.countImagesFromFolder( folderId )
    //val imagesCount: Int
    //    get() {  }

    var pagerState: PagerState = PagerState( pageCount = { 0 } )
    var corutineScope: CoroutineScope? = null // TODO make initializer here

    init {
        viewModelScope.launch(Dispatchers.Main) {
            val imgCount: Int = images.take(1).last().count()
            pagerState = PagerState( pageCount = { imgCount } )
            loaded.value = true
        }
    }

    fun swipe( delta: Int ) {
        if( !canSwipe( delta ) )
            return
        corutineScope?.launch {
            pagerState.animateScrollToPage( pagerState.currentPage + delta )
        }
    }

    fun canSwipe( delta: Int ): Boolean {
        if( pagerState.currentPage + delta < 0 )
            return false
        if( pagerState.currentPage + delta >= pagerState.pageCount )
            return false
        return true
    }

}