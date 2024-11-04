package com.example.photoselector.data

import kotlinx.coroutines.flow.Flow
import java.net.URLDecoder

class ImagesRepositoryImplDatabase(
    private val folderDao: FolderDao,
    private val imageDao: ImageDao
) : ImagesRepositoryInterface {

    override fun getAllFolders(): Flow<List<FolderAndCounts>> = folderDao.getAllFolders()

    override suspend fun getFolder(id: Int): Folder? {
        return folderDao.getFolder( id )
    }

    override suspend fun deleteFolder(folder: Folder): Unit {
        imageDao.deleteByFolder( folder.id )
        folderDao.delete( folder )
    }

    override suspend fun addFolderIfNotExists( path: String ): Boolean {
        if( path.isEmpty() )
            return false; // TODO Snackbar "Seleziona qualcosa"
        if( (folderDao.checkIfExists( path )) )
            return false; // TODO Snackbar "Già esiste"

        val name = URLDecoder.decode( path.substring( maxOf (
            path.indexOf( "%2F", path.indexOf("com.", path.indexOf("com.") + 1 ) + 1 ) + 3,
            path.indexOf("tree/primary") + 13,
            0
        ) ), "UTF-8" )

        return folderDao.insert( Folder( path = path, name = name ) ) > 0
    }

    override suspend fun addImageIfNotExists(folderId: Int, path: String): Boolean {
        if( path.isEmpty() )
            return false; // TODO Snackbar "Seleziona qualcosa"
        if( folderDao.getFolder( folderId ) == null )
            return false; // TODO Snackbar "La cartella non esiste"
        if( (imageDao.checkIfExists( path )) )
            return false;

        return imageDao.insert( Image( folderId = folderId, path = path, action = "" ) ) > 0
    }
}