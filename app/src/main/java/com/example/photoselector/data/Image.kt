package com.example.photoselector.data
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// This class represent an image in the user phone
// It is a Room class, connected therefore to a Room table
@Entity(
    tableName = "images",
    foreignKeys = [ForeignKey(
        entity = Folder::class,
        parentColumns = ["id"],
        childColumns = ["folder_id"]
    )],
    indices = [
        Index("folder_id")
    ])
data class Image(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "folder_id") val folder: Int,
    val filename: String,
    val action: String?
)