package com.example.photoselector.data

import androidx.room.ColumnInfo

// This class represent a folder in the user phone
// It is a Room class, connected therefore to a Room table
data class FolderAndCounts(
    val id: Int,
    val path: String,
    val name: String,
    @ColumnInfo(name = "img_count") val imgCount: Int,
    @ColumnInfo(name = "img_actdone_count") val imgActDoneCount: Int // TODO questo non funziona, ma non capisco perché
)