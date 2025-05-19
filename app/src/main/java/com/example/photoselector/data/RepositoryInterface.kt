package com.example.photoselector.data

import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.flow.Flow

interface RepositoryInterface {
    fun getAllFolders(): Flow<List<FolderAndCounts>>
    suspend fun getFolder( id: Int ): FolderAndCounts?
    fun getImagesFromFolder( id: Int ): Flow<List<Image>>
    fun getImagesAndActionsFromFolder( id: Int ): Flow<List<ImageAndAction>>
    suspend fun deleteFolder( folder: Folder )
    suspend fun deleteFolder( folder: FolderAndCounts )
    suspend fun addFolderIfNotExists( path: String ): Boolean
    suspend fun addImageIfNotExists( folderId: Int, documentFile: DocumentFile ): Boolean
    suspend fun addImageIfNotExists( folderId: Int, path: String, name: String ): Boolean
    suspend fun addImage( folderId: Int, documentFile: DocumentFile ): Boolean
    suspend fun addImage( folderId: Int, path: String, name: String ): Boolean
    suspend fun addImages( images: List<Image> ): Boolean
    suspend fun updateImage( image: Image ): Unit
    suspend fun deleteImage( image: Image )
    suspend fun deleteImages( images: List<Int> )
    suspend fun deleteAllImages()
    suspend fun deleteUnactionedImages()
    fun getAllActions(): Flow<List<Action>>
    suspend fun addAction( name: String, type: String, icon: Int, path: String? ): Long
    suspend fun hideAction( action: Action )
}