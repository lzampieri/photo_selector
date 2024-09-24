package com.example.photoselector.data

import android.content.Context

interface AppContainer {
    val imagesRepository: ImagesRepository
}

class ActualAppContainer(private val context: Context) : AppContainer {
    override val imagesRepository: ImagesRepository by lazy {
        DatabaseImagesRepository(
            TheDatabase.getDatabase(context).folderDao(),
            TheDatabase.getDatabase(context).imageDao()
        )
    }
}