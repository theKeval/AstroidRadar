package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.db.AsteroidDb
import com.udacity.asteroidradar.db.asAsteroids
import com.udacity.asteroidradar.models.Asteroid
import com.udacity.asteroidradar.models.asAsteroidEntity
import com.udacity.asteroidradar.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class AsteroidRepository(private val database: AsteroidDb) {

    // val asteroids: LiveData<List<Asteroid>> = MutableLiveData(database.asteroidDao.getAsteroids().asAsteroids())
    val asteroids: LiveData<List<Asteroid>> = Transformations.map(database.asteroidDao.getAsteroids()) {
        it.asAsteroids()
    }

    suspend fun refreshAsteroids() {
        val datetime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        val today = dateFormat.format(datetime)

        withContext(Dispatchers.IO) {
            val response = AsteroidApi.retrofitService.getAsteroids(
                startDate = today,
                endDate = today
                // add apiKey value here
            ).await()

            val asteroids = parseAsteroidsJsonResult(JSONObject(response))
            database.asteroidDao.insertAll(*asteroids.asAsteroidEntity().toTypedArray())
        }
    }

}