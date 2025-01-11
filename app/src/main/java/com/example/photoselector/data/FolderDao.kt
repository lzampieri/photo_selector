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

    @Query("SELECT * from folders LEFT JOIN" +
            "( SELECT folder_id, Count(*) AS img_count FROM images GROUP BY folder_id ) AS grp_images ON folders.id = grp_images.folder_id  LEFT JOIN" +
            "( SELECT folder_id, Count(*) AS img_actdone_count FROM images WHERE action_done IS TRUE GROUP BY folder_id ) AS grp_actdone_images ON folders.id = grp_actdone_images.folder_id")
    fun getAllFolders(): Flow<List<FolderAndCounts>>

    @Query("SELECT * from folders LEFT JOIN" +
            "( SELECT folder_id, Count(*) AS img_count FROM images GROUP BY folder_id ) AS grp_images ON folders.id = grp_images.folder_id  LEFT JOIN" +
            "( SELECT folder_id, Count(*) AS img_actdone_count FROM images WHERE action_done IS TRUE GROUP BY folder_id ) AS grp_actdone_images ON folders.id = grp_actdone_images.folder_id " +
            "WHERE folders.id = :id")
    suspend fun getFolder( id: Int ): FolderAndCounts?

    @Query("SELECT * from folders WHERE id = :id")
    suspend fun getFolderWithoutCounts( id: Int ): Folder

    @Query("SELECT EXISTS(SELECT * FROM folders WHERE path = :path)")
    fun checkIfExists( path: String ): Boolean
}
