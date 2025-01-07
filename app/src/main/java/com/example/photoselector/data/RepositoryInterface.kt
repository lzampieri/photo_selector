package com.example.photoselector.data

import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.flow.Flow

interface RepositoryInterface {
    fun getAllFolders(): Flow<List<FolderAndCounts>>
    suspend fun getFolder( id: Int ): Folder?
    fun getImagesFromFolder( id: Int ): Flow<List<Image>>
    fun getImagesAndActionsFromFolder( id: Int ): Flow<List<ImageAndAction>>
    suspend fun deleteFolder( folder: Folder )
    suspend fun addFolderIfNotExists( path: String ): Boolean
    suspend fun addImageIfNotExists( folderId: Int, documentFile: DocumentFile ): Boolean
    suspend fun updateImage( image: Image ): Unit
    fun getAllActions(): Flow<List<Action>>
    suspend fun addAction( name: String, type: String, icon: Int, path: String? ): Long
    suspend fun hideAction( action: Action )
}