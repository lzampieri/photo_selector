package com.example.photoselector.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.photoselector.data.Folder
import com.example.photoselector.data.Image
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(image: Image): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(image: List<Image>): List<Long>

    @Update
    suspend fun update(image: Image)

    @Delete
    suspend fun delete(image: Image)

    @Query("DELETE from images WHERE id in (:images)")
    suspend fun delete(images: List<Int>)

    @Query("DELETE from images WHERE folder_id = :folderId")
    suspend fun deleteByFolder( folderId: Int )

    @Query("DELETE from images")
    suspend fun deleteAll()

    @Query("DELETE from images WHERE action_id IS NULL AND action_done == 0")
    suspend fun deleteUnactioned()

    @Query("SELECT * from images")
    fun getAllImages(): Flow<List<Image>>

    @Query("SELECT * from images WHERE folder_id = :folderId" )
    fun getImagesFromFolder( folderId: Int ): Flow<List<Image>>

    @Transaction
    @Query("SELECT * from images WHERE folder_id = :folderId")
    fun getImagesAndActionsFromFolder( folderId: Int ): Flow<List<ImageAndAction>>

    @Query("SELECT EXISTS(SELECT * FROM images WHERE path = :path)")
    fun checkIfExists( path: String ): Boolean
}
