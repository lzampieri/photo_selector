package com.example.photoselector.ui.main

import android.content.ContentResolver
import android.content.Context
import android.content.UriPermission
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoselector.data.Folder
import com.example.photoselector.data.ImagesRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch


class MainViewModel(
    private val context: Context,
    private val contentResolver: ContentResolver, // todo REMOVE
    private val imagesRepositoryInterface: ImagesRepositoryInterface
) : ViewModel() {

    val folders : Flow<List<Folder>> = imagesRepositoryInterface.getAllFolders()

    init {
        viewModelScope.launch {
            updateFoldersList()
        }
    }

    suspend fun updateFoldersList() {
        val list: List<UriPermission> = contentResolver.persistedUriPermissions
        Log.d("Directory permission", list.count().toString() )
        list.forEach { uri ->
            Log.d("Directory Permission", uri.toString() )
        }


        val fl: List<Folder> = imagesRepositoryInterface.getAllFolders().take(1).last()

        fl.forEach { ff ->
            try {
                var dt = DocumentFile.fromTreeUri( context, Uri.parse( ff.path ) )
                // todo ADD CHECK FOR dt PERMISSIONS EXPIRED
                Log.d( "Directory", "===" )
                Log.d( "Directory", Uri.parse( ff.path ).toString() )
                dt?.listFiles()?.forEach { fff ->
                    Log.d( "Directory", fff.toString() )
                }
                /*(application as PhotoselectorApplication).container.imagesRepository.addFolderIfNotExists(
                    uri.toString()
                )*/

            } catch ( e: IllegalArgumentException ) {
                Log.d("Directory", "Directory ${ff.toString()} should be deleted since it returns an illegal argument exception" )
                imagesRepositoryInterface.deleteFolder( ff )
            }
        }
    }

}