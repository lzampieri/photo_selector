package com.example.photoselector.ui.models

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoselector.PhotoselectorApplication
import com.example.photoselector.data.FolderAndCounts
import com.example.photoselector.data.ImageAndAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import java.io.FileInputStream



class AppViewModel (
    val app: Application,
) : AndroidViewModel(app) {

    val appContainer = (app as PhotoselectorApplication).container

    val folders : Flow<List<FolderAndCounts>> = appContainer.repository.getAllFolders()
    val selectedFolder: MutableStateFlow<FolderAndCounts?> = MutableStateFlow( null )
    val loading = MutableStateFlow( 0 )
    val imagesDbLoading = MutableStateFlow( 0 )

    val images: Flow<List<ImageAndAction>>
        get() = this.appContainer.repository.getImagesAndActionsFromFolder( selectedFolder.value?.id ?: -1 )

    init {
        viewModelScope.launch(Dispatchers.IO) {
            refreshFoldersContent() // TODO re-enable
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

                // Check that all registered images still exists
                val imgs = appContainer.repository.getImagesFromFolder( ff.id ).take(1).last()
                imgs.forEach { img ->
                    val df = DocumentFile.fromTreeUri( appContainer.appContext, Uri.parse( img.path ) )
                    if( df == null || !df.exists() )
                        appContainer.repository.deleteImage( img )
                }

            } catch ( e: IllegalArgumentException ) {
                // appContainer.imagesRepository.deleteFolder( ff ) // TODO gestire
            }
        }

        imagesDbLoading.value -= 1
    }

    public fun runActions() {
        viewModelScope.launch(Dispatchers.IO) {
            loading.value += 1

            val imgs = images.take(1).last()

            imgs.forEach { img ->
                if( img.image.actionDone ) return@forEach
                if( img.action == null ) return@forEach

                if( img.action.type == "skip" ) {
                    setActionDone( img )
                    return@forEach
                }


                if( img.action.type == "copy" ) {
                    filesCopy( img.image.path, img.action.path!! )
                    setActionDone( img )
                    return@forEach
                }


                if( img.action.type == "move" ) {
                    filesCopy( img.image.path, img.action.path!! )
                    fileDelete( img.image.path )
                    appContainer.repository.deleteImage( img.image )
                    return@forEach
                }
            }

            refreshFoldersContent()

            loading.value -= 1
        }
    }

    private suspend fun filesCopy( from: String, to: String ) {
        val fromFile : DocumentFile = DocumentFile.fromTreeUri( appContainer.appContext, Uri.parse( from ) )!!
        val toDir : DocumentFile = DocumentFile.fromTreeUri( appContainer.appContext, Uri.parse( to ) )!!

        val newFile: DocumentFile = toDir.createFile( fromFile.type!!, fromFile.name!! )!!

        val out = appContainer.appContext.contentResolver.openOutputStream( newFile.uri )
        val inp = appContainer.appContext.contentResolver.openInputStream( fromFile.uri )

        val buffer = ByteArray(1024)
        var read: Int
        while ( (inp?.read(buffer).also { read = it!! }) != -1) {
            out?.write(buffer, 0, read)
        }
        inp?.close()

        // write the output file (You have now copied the file)
        out?.flush()
        out?.close()
    }

    private suspend fun fileDelete( from: String ) {
        val fromFile: DocumentFile =
            DocumentFile.fromTreeUri(appContainer.appContext, Uri.parse(from))!!
        fromFile.delete()
    }

    private suspend fun setActionDone( iaa: ImageAndAction ) {
        iaa.image.actionDone = true
        appContainer.repository.updateImage( iaa.image )
    }
}