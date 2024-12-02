package com.example.photoselector.ui.models

import android.content.Intent
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoselector.data.Action
import com.example.photoselector.data.AppContainer
import com.example.photoselector.data.Folder
import com.example.photoselector.data.FolderAndCounts
import com.example.photoselector.data.Image
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

class AppViewModel (
    public val appContainer: AppContainer,
) : ViewModel() {

    val folders : Flow<List<FolderAndCounts>> = appContainer.repository.getAllFolders()
    val selectedFolder: MutableStateFlow<Folder?> = MutableStateFlow( null )
    val loading = MutableStateFlow( 0 )
    val imagesDbLoading = MutableStateFlow( 0 )

    // val images : MutableStateFlow<List<Image>> = MutableStateFlow( emptyList() )
    val images: Flow<List<Image>>
        get() = this.appContainer.repository.getImagesFromFolder( selectedFolder.value?.id ?: -1 )

    val actions : Flow<List<Action>> = appContainer.repository.getAllActions()
    val actionsViewModel: ActionsViewModel = ActionsViewModel( appContainer )

    init {
        viewModelScope.launch(Dispatchers.IO) {
            //refreshFoldersContent() // TODO re-enable
        }
    }

    fun selectFolder( folderId: Int ) {
        if( folderId == selectedFolder.value?.id ) return
        loading.value += 1
        viewModelScope.launch(Dispatchers.IO) {
            selectedFolder.value = appContainer.repository.getFolder( folderId )
            loading.value -= 1
        }
    }

    fun deleteSelectedFolder() {
        if( selectedFolder.value == null ) return
        loading.value += 1
        viewModelScope.launch(Dispatchers.IO) {
            appContainer.repository.deleteFolder( selectedFolder.value!! )
            loading.value -= 1
        }
    }

    fun addFolder( uri: Uri ) {
        viewModelScope.launch(Dispatchers.IO) {
            addFolderAct( uri )
        }
    }

    private suspend fun addFolderAct(uri: Uri ) {
        appContainer.appContext.contentResolver.takePersistableUriPermission(
            uri, Intent.FLAG_GRANT_READ_URI_PERMISSION + Intent.FLAG_GRANT_WRITE_URI_PERMISSION )

        appContainer.repository.addFolderIfNotExists(
            uri.toString()
        )
        refreshFoldersContent()
    }

    private suspend fun refreshFoldersContent() {
        imagesDbLoading.value += 1

        val fl: List<FolderAndCounts> = appContainer.repository.getAllFolders().take(1).last()

        fl.forEach { ff ->
            try {
                val dt = DocumentFile.fromTreeUri( appContainer.appContext, Uri.parse( ff.path ) )
                dt?.listFiles()?.forEach { fff ->
                    if(fff.type?.startsWith("image") == true)
                        appContainer.repository.addImageIfNotExists( ff.id, fff )
                }

            } catch ( e: IllegalArgumentException ) {
                // appContainer.imagesRepository.deleteFolder( ff ) // TODO gestire
            }
        }

        imagesDbLoading.value -= 1
    }

}