package com.example.photoselector.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.photoselector.data.Folder
import com.example.photoselector.data.Image
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(image: Image): Long

    @Update
    suspend fun update(image: Image)

    @Delete
    suspend fun delete(image: Image)

    @Query("DELETE from images WHERE folder_id = :folderId")
    suspend fun deleteByFolder( folderId: Int )

    @Query("SELECT * from images")
    fun getAllImages(): Flow<List<Image>>

    @Query("SELECT * from images WHERE folder_id = :folderId" )
    fun getImagesFromFolder( folderId: Int ): Flow<List<Image>>

    @Query("SELECT COUNT(*) from images WHERE folder_id = :folderId")
    fun countImagesFromFolder( folderId: Int ): Flow<Int>

    @Query("SELECT EXISTS(SELECT * FROM images WHERE path = :path)")
    fun checkIfExists( path: String ): Boolean
}
