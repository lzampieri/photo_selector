package com.example.photoselector.data
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// This class represent a possible action for images
// It is a Room class, connected therefore to a Room table
@Entity(
    tableName = "actions")
data class Action(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: String,
    val icon: Int,
    val path: String?,
    val hidden: Boolean
)