package com.example.photoselector.data

import kotlinx.coroutines.flow.Flow

interface ImagesRepositoryInterface {
    fun getAllFolders(): Flow<List<FolderAndCounts>>
    suspend fun getFolder( id: Int ): Folder?
    suspend fun deleteFolder( folder: Folder )
    suspend fun addFolderIfNotExists( path: String ): Boolean
    suspend fun addImageIfNotExists( folderId: Int, path: String ): Boolean
}