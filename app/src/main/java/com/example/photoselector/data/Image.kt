package com.example.photoselector.data
import androidx.room.Entity
import androidx.room.PrimaryKey

// This class represent an image in the user phone
// It is a Room class, connected therefore to a Room table
@Entity(tableName = "images")
data class Image(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val folder: Folder,
    val filename: String,
    val action: String?
)