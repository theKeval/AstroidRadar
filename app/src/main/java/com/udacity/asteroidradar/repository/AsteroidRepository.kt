package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.getNextSevenDaysFormattedDates
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.db.AsteroidDb
import com.udacity.asteroidradar.db.asAsteroids
import com.udacity.asteroidradar.db.toPictureOfDay
import com.udacity.asteroidradar.models.Asteroid
import com.udacity.asteroidradar.models.PictureOfDay
import com.udacity.asteroidradar.models.asAsteroidEntity
import com.udacity.asteroidradar.models.toPodEntity
import com.udacity.asteroidradar.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

enum class AsteroidFilter {
    SHOW_WEEK, SHOW_TODAY, SHOW_ALL_SAVED
}

class AsteroidRepository(private val database: AsteroidDb) {

    // Asteroids Operations

    private val week = getNextSevenDaysFormattedDates()

    val allSavedAsteroids: LiveData<List<Asteroid>> = Transformations.map(database.asteroidDao.getAllAsteroids()) {
        it.asAsteroids()
    }

    val todayAsteroids: LiveData<List<Asteroid>> = Transformations.map(database.asteroidDao.getTodayAsteroids(week[0])) {
        it.asAsteroids()
    }

    val weekAsteroids: LiveData<List<Asteroid>> = Transformations.map(database.asteroidDao.getWeekAsteroids(week[0], week[week.size-1])) {
        it.asAsteroids()
    }

    fun getAsteroids(filter: AsteroidFilter): LiveData<List<Asteroid>> {

        return when (filter) {
            AsteroidFilter.SHOW_TODAY -> {
                Transformations.map(database.asteroidDao.getTodayAsteroids(week[0])) {
                    it.asAsteroids()
                }
            }

            AsteroidFilter.SHOW_WEEK -> {
                Transformations.map(
                    database.asteroidDao.getWeekAsteroids(
                        week[0],
                        week[week.size - 1]
                    )
                ) {
                    it.asAsteroids()
                }
            }

            AsteroidFilter.SHOW_ALL_SAVED -> {
                Transformations.map(database.asteroidDao.getAllAsteroids()) {
                    it.asAsteroids()
                }
            }

        }

    }

    suspend fun refreshAsteroids() {
        val datetime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        val today = dateFormat.format(datetime)

        withContext(Dispatchers.IO) {
            val response = AsteroidApi.retrofitService.getAsteroids(
                startDate = week[0],
                endDate = week[week.size - 1],
                apiKey = Constants.API_KEY
            )

            val asteroids = parseAsteroidsJsonResult(JSONObject(response))
            database.asteroidDao.insertAllAsteroids(*asteroids.asAsteroidEntity().toTypedArray())
        }
    }


    // Pod Operations

    val pod: LiveData<PictureOfDay> = Transformations.map(database.asteroidDao.getTodayPod()) {
        it?.toPictureOfDay()
    }

    suspend fun refreshPod() {
        withContext(Dispatchers.IO) {
            val response = AsteroidApi.retrofitForPodService.getPod(Constants.API_KEY)
            database.asteroidDao.insertPod(response.toPodEntity())
        }
    }

}