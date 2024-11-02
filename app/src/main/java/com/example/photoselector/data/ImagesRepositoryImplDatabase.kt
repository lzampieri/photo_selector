package com.example.photoselector.data

import kotlinx.coroutines.flow.Flow
import java.net.URLDecoder

class ImagesRepositoryImplDatabase(
    private val folderDao: FolderDao,
    private val imageDao: ImageDao
) : ImagesRepositoryInterface {

    override fun getAllFolders(): Flow<List<Folder>> = folderDao.getAllFolders()

    override suspend fun getFolder(id: Int): Folder {
        return folderDao.getFolder( id )
    }

    override suspend fun deleteFolder(folder: Folder): Unit = folderDao.delete( folder )

    override suspend fun addFolderIfNotExists( path: String ): Boolean {
        if( path.isEmpty() || (folderDao.checkIfExists( path )) )
            return false;

        val name = URLDecoder.decode( path.substring( maxOf (
            path.indexOf( "%2F", path.indexOf("com.", path.indexOf("com.") + 1 ) + 1 ) + 3,
            path.indexOf("tree/primary") + 13,
            0
        ) ), "UTF-8" )

        return folderDao.insert( Folder( path = path, name = name ) ) > 0

        // TODO gestire le eccezzioni in questa funzione
    }
}