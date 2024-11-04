package com.example.photoselector.data

import android.content.Context
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher

interface AppContainer {
    val imagesRepository: ImagesRepositoryInterface
    var pickFolderIntentLauncher: ActivityResultLauncher<Uri?>?
    val appContext: Context
}

class ActualAppContainer(private val context: Context) : AppContainer {
    override val imagesRepository: ImagesRepositoryInterface by lazy {
        ImagesRepositoryImplDatabase(
            TheDatabase.getDatabase(context).folderDao(),
            TheDatabase.getDatabase(context).imageDao()
        )
    }
    override var pickFolderIntentLauncher: ActivityResultLauncher<Uri?>? = null
    override val appContext: Context = context
}