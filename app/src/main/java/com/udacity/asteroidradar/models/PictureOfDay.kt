package com.udacity.asteroidradar.models

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.udacity.asteroidradar.db.PodEntity
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PictureOfDay(
    @Json(name = "media_type")
    val mediaType: String,
    val title: String,
    val url: String
): Parcelable


fun PictureOfDay.toPodEntity(): PodEntity {
    return PodEntity(
        url = this.url,
        title = this.title,
        mediaType = this.mediaType
    )
}