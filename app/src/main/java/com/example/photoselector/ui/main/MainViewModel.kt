package com.example.photoselector.ui.main

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoselector.PhotoselectorApplication
import com.example.photoselector.data.Folder
import com.example.photoselector.data.ImagesRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

class MainViewModel(
    private val context: Context,
    private val imagesRepositoryInterface: ImagesRepositoryInterface
) : ViewModel() {

    val folders : Flow<List<Folder>> = imagesRepositoryInterface.getAllFolders()

    init {
        viewModelScope.launch {
            updateFoldersList()
        }
    }

    suspend fun updateFoldersList() {
        val fl: List<Folder> = imagesRepositoryInterface.getAllFolders().take(1).last()

        fl.forEach { ff ->
            try {
                var dt = DocumentFile.fromTreeUri( context, Uri.parse( ff.path ) )
                if( dt == null ) throw IllegalArgumentException()

                Log.d( "Directory", ff.path )
                dt.listFiles().forEach { fff ->
                    Log.d( "Directory", fff.toString() )
                }

            } catch ( e: IllegalArgumentException ) {
                Log.d("Directory", "Directory ${ff.toString()} should be deleted since it returns an illegal argument exception" )
                imagesRepositoryInterface.deleteFolder( ff )
            }
        }
    }

}