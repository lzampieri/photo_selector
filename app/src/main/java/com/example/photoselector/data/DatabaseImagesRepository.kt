package com.example.photoselector.data

import kotlinx.coroutines.flow.Flow

class DatabaseImagesRepository(
    private val folderDao: FolderDao,
    private val imageDao: ImageDao
) : ImagesRepository {

    override fun getAllFolders(): Flow<List<Folder>> = folderDao.getAllFolders()


}