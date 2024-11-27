package com.example.photoselector.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database( entities = [ Folder::class, Image::class ], version = 5, exportSchema = false)
abstract class TheDatabase : RoomDatabase() {

    abstract fun folderDao(): FolderDao
    abstract fun imageDao(): ImageDao

    companion object {
        @Volatile private var instance: TheDatabase? = null
        fun getDatabase(context: Context): TheDatabase {
            return instance ?: synchronized(this) {
                Room.databaseBuilder( context, TheDatabase::class.java, "photoselector-database" )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
        }

    }
}