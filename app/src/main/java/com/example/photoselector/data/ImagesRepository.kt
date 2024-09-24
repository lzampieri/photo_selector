package com.example.photoselector.data

import kotlinx.coroutines.flow.Flow

interface ImagesRepository {
    fun getAllFolders(): Flow<List<Folder>>
}