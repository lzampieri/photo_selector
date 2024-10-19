package com.example.photoselector.data

import android.util.Log
import kotlinx.coroutines.flow.Flow

class ImagesRepositoryImplDatabase(
    private val folderDao: FolderDao,
    private val imageDao: ImageDao
) : ImagesRepositoryInterface {

    override fun getAllFolders(): Flow<List<Folder>> = folderDao.getAllFolders()
    override suspend fun deleteFolder(folder: Folder): Unit = folderDao.delete( folder )

    override suspend fun addFolderIfNotExists( path: String ): Boolean {
        return path.isNotEmpty() && !(folderDao.checkIfExists( path )) && folderDao.insert( Folder( path = path ) ) > 0
    }
}