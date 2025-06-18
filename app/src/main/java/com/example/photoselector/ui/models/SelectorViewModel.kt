package com.example.photoselector.ui.models

import android.app.Application
import androidx.compose.foundation.pager.PagerState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoselector.PhotoselectorApplication
import com.example.photoselector.data.Action
import com.example.photoselector.data.Image
import com.example.photoselector.data.ImageAndAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

class SelectorViewModel (
    val folderId: Int,
    val startBy: Int = -1,
    app: Application,
) : AndroidViewModel(app) {

    private val appContainer = (app as PhotoselectorApplication).container

    val actions : Flow<List<Action>> = appContainer.repository.getAllActions()

    val images : Flow<List<ImageAndAction>> = appContainer.repository.getImagesAndActionsFromFolder( folderId )
    val loaded = MutableStateFlow( false )

    var pagerState: PagerState = PagerState( pageCount = { 0 } )
    var corutineScope: CoroutineScope? = null // TODO make initializer here

    init {
        viewModelScope.launch(Dispatchers.Main) {
            val imgs: List<ImageAndAction> = images.take(1).last()
            var cPage = 0

            run cPageInit@{
                // Prima prova ad inizializzare cPage con una immagine con l'ID uguale
                imgs.forEachIndexed{ i, img -> if( img.image.id == startBy ) { cPage = i; return@cPageInit } }

                // Se l'immagine di partenza non è stata assegnata, usa la prima immagine senza azione
                imgs.forEachIndexed{ i, img -> if( img.image.actionId == null ) { cPage = i; return@cPageInit } }
            }


            pagerState = PagerState(
                currentPage = cPage,
                pageCount = { imgs.count() } )


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

    fun smartNext() {
        if( pagerState.currentPage + 1 == pagerState.pageCount )
            return
        var goTo = pagerState.currentPage + 1

        viewModelScope.launch(Dispatchers.IO) {
            val imgs: List<ImageAndAction> = images.take(1).last()

            if (startBy < 0) { // Meaning that you're doing a "what I'm missing" scan
                while ( (goTo < pagerState.pageCount - 1) && ( imgs[goTo].image.actionId != null ) )
                    goTo++
            }

            corutineScope?.launch {
                pagerState.animateScrollToPage(goTo)
            }
        }
    }

    fun canSwipe( delta: Int ): Boolean {
        if( pagerState.currentPage + delta < 0 )
            return false
        if( pagerState.currentPage + delta >= pagerState.pageCount )
            return false
        return true
    }

    fun saveAction( actId: Int ) {
        val imagePos = pagerState.currentPage
        smartNext()
        viewModelScope.launch(Dispatchers.IO) {
            val image: Image = images.take(1).last()[ imagePos ].image
            if( image.actionId != actId ) {
                image.actionId = actId
                image.actionDone = false
                appContainer.repository.updateImage( image )
            }
        }
    }

}