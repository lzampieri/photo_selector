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
interface ActionDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(action: Action): Long

    @Update
    suspend fun update(action: Action)

    @Query("SELECT * from actions WHERE NOT hidden")
    fun getAllActions(): Flow<List<Action>>
}
