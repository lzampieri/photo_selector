package com.example.photoselector.data

import kotlinx.coroutines.flow.Flow

interface ImagesRepositoryInterface {
    fun getAllFolders(): Flow<List<Folder>>
    suspend fun addFolderIfNotExists( path: String ): Boolean
}