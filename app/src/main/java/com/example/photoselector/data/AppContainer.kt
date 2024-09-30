package com.example.photoselector.data

import android.content.Context

interface AppContainer {
    val imagesRepository: ImagesRepositoryInterface
}

class ActualAppContainer(private val context: Context) : AppContainer {
    override val imagesRepository: ImagesRepositoryInterface =
        ImagesRepositoryImplDatabase(
            TheDatabase.getDatabase(context).folderDao(),
            TheDatabase.getDatabase(context).imageDao()
        )
}