package com.udacity.asteroidradar.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.udacity.asteroidradar.models.Asteroid

@Entity(tableName = "Asteroids")
data class AsteroidEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val closeApproachDate: String,
    val absoluteMagnitude: String,
    val estimatedDiameter: String,
    val relativeVelocity: String,
    val distanceFromEarth: String,
    val isPotentiallyHazardous: String
)

fun List<AsteroidEntity>.asAsteroids(): List<Asteroid> {
    return map {
        Asteroid(
            id = it.id.toLong(),
            codename = it.name,
            closeApproachDate = it.closeApproachDate,
            absoluteMagnitude = it.absoluteMagnitude.toDouble(),
            estimatedDiameter = it.estimatedDiameter.toDouble(),
            relativeVelocity = it.relativeVelocity.toDouble(),
            distanceFromEarth = it.distanceFromEarth.toDouble(),
            isPotentiallyHazardous = it.isPotentiallyHazardous.toBoolean()
        )
    }
}