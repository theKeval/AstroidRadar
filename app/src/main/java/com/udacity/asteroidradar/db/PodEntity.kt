package com.udacity.asteroidradar.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.udacity.asteroidradar.models.PictureOfDay

@Entity(tableName = "PictureOfDay")
data class PodEntity(
    @PrimaryKey(autoGenerate = true)
    val picId: Long = 0L,
    val url: String,
    val title: String,
    @ColumnInfo(name = "media_type")
    val mediaType: String
)


fun PodEntity.toPictureOfDay(): PictureOfDay {
    return PictureOfDay(
        mediaType = this.mediaType,
        title = this.title,
        url = this.url
    )
}