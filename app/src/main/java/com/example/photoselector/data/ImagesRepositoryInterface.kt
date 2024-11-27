package com.example.photoselector.data

import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.flow.Flow

interface ImagesRepositoryInterface {
    fun getAllFolders(): Flow<List<FolderAndCounts>>
    suspend fun getFolder( id: Int ): Folder?
    fun getImagesFromFolder( id: Int ): Flow<List<Image>>
    suspend fun deleteFolder( folder: Folder )
    suspend fun addFolderIfNotExists( path: String ): Boolean
    suspend fun addImageIfNotExists( folderId: Int, documentFile: DocumentFile ): Boolean
}