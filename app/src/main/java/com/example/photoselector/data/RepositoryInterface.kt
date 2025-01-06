package com.example.photoselector.data

import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.flow.Flow

interface RepositoryInterface {
    fun getAllFolders(): Flow<List<FolderAndCounts>>
    suspend fun getFolder( id: Int ): Folder?
    fun getImagesFromFolder( id: Int ): Flow<List<Image>>
    fun countImagesFromFolder( id: Int ): Flow<Int>
    suspend fun deleteFolder( folder: Folder )
    suspend fun addFolderIfNotExists( path: String ): Boolean
    suspend fun addImageIfNotExists( folderId: Int, documentFile: DocumentFile ): Boolean
    fun getAllActions(): Flow<List<Action>>
    suspend fun addAction( name: String, copy: Boolean, icon: Int, path: String ): Long
    suspend fun hideAction( action: Action )
}