package com.udacity.asteroidradar.models

import android.os.Parcelable
import com.udacity.asteroidradar.db.AsteroidEntity
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Asteroid(
    val id: Long,
    val codename: String,
    val closeApproachDate: String,
    val absoluteMagnitude: Double,
    val estimatedDiameter: Double,
    val relativeVelocity: Double,
    val distanceFromEarth: Double,
    val isPotentiallyHazardous: Boolean
) : Parcelable

fun List<Asteroid>.asAsteroidEntity() : List<AsteroidEntity> {
    return map {
        AsteroidEntity(
            id = it.id.toString(),
            name = it.codename,
            closeApproachDate = it.closeApproachDate,
            absoluteMagnitude = it.absoluteMagnitude.toString(),
            estimatedDiameter = it.estimatedDiameter.toString(),
            relativeVelocity = it.relativeVelocity.toString(),
            distanceFromEarth = it.distanceFromEarth.toString(),
            isPotentiallyHazardous = it.isPotentiallyHazardous.toString()
        )
    }
}