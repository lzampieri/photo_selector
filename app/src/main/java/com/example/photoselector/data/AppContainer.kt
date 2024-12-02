package com.example.photoselector.data

import android.content.Context
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher

interface AppContainer {
    val repository: RepositoryInterface
    var pickFolderIntentLauncher: ActivityResultLauncher<Uri?>?
    var actionFolderIntentLauncher: ActivityResultLauncher<Uri?>?
    val appContext: Context
}

class ActualAppContainer(private val context: Context) : AppContainer {
    override val repository: RepositoryInterface by lazy {
        RepositoryImplDatabase(
            TheDatabase.getDatabase(context).folderDao(),
            TheDatabase.getDatabase(context).imageDao(),
            TheDatabase.getDatabase(context).actionDao()
        )
    }
    override var pickFolderIntentLauncher: ActivityResultLauncher<Uri?>? = null
    override var actionFolderIntentLauncher: ActivityResultLauncher<Uri?>? = null
    override val appContext: Context = context
}