package com.example.photoselector.data

import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.flow.Flow
import java.net.URLDecoder

class RepositoryImplDatabase(
    private val folderDao: FolderDao,
    private val imageDao: ImageDao,
    private val actionDao: ActionDao
) : RepositoryInterface {

    override fun getAllFolders(): Flow<List<FolderAndCounts>> = folderDao.getAllFolders()

    override suspend fun getFolder(id: Int): Folder? {
        return folderDao.getFolder( id )
    }

    override fun getImagesFromFolder(id: Int): Flow<List<Image>> {
        return imageDao.getImagesFromFolder( id )
    }

    override fun getImagesAndActionsFromFolder(id: Int): Flow<List<ImageAndAction>> {
        return imageDao.getImagesAndActionsFromFolder( id )
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

    override suspend fun addImageIfNotExists(folderId: Int, documentFile: DocumentFile ): Boolean {
        if( folderDao.getFolder( folderId ) == null )
            return false; // TODO Snackbar "La cartella non esiste"
        val path: String = documentFile.uri.toString()
        if( (imageDao.checkIfExists( path )) )
            return false;

        return imageDao.insert( Image( folderId = folderId, path = path, name = documentFile.name ?: "Unnamed", actionId = null ) ) > 0
    }

    override suspend fun updateImage(image: Image): Unit = imageDao.update( image )

    override fun getAllActions(): Flow<List<Action>> = actionDao.getAllActions()

    override suspend fun addAction(name: String, type: String, icon: Int, path: String?): Long {
        return actionDao.insert( Action( name = name, type = type, icon = icon, path = path, hidden = false ) )
    }

    override suspend fun hideAction(action: Action) {
        actionDao.update( Action(
            id = action.id,
            name = action.name,
            type = action.type,
            path = action.path,
            icon = action.icon,
            hidden = true
        ) )
    }
}