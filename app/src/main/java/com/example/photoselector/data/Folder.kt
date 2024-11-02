package com.example.photoselector.data
import androidx.room.Entity
import androidx.room.PrimaryKey

// This class represent a folder in the user phone
// It is a Room class, connected therefore to a Room table
@Entity(tableName = "folders")
data class Folder(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val path: String,
    val name: String
)