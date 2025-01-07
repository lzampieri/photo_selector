package com.example.photoselector.data

import androidx.room.Embedded
import androidx.room.Relation

data class ImageAndAction(
    @Embedded val image: Image,
    @Relation(
        parentColumn = "action_id",
        entityColumn = "id"
    )
    val action: Action?
)