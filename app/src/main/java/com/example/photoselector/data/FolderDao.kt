package com.example.photoselector.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.photoselector.data.Folder
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(folder: Folder): Long

    @Update
    suspend fun update(folder: Folder)

    @Delete
    suspend fun delete(folder: Folder)

    @Query("SELECT * from folders")
    fun getAllFolders(): Flow<List<Folder>>

    @Query("SELECT EXISTS(SELECT * FROM folders WHERE path = :path)")
    fun checkIfExists( path: String ): Boolean
}
