package com.udacity.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.db.getDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import retrofit2.HttpException

class RefreshAsteroidWorker(context: Context, parameters: WorkerParameters): CoroutineWorker(context, parameters) {

    companion object{
        const val WORK_NAME = "RefreshAsteroidsWorker"
    }

    override suspend fun doWork(): Result {

        val database = getDatabase(applicationContext)
        val repository = AsteroidRepository(database)

        return try {
            repository.refreshAsteroids()
            repository.refreshPod()
            Result.success()
        }
        catch (exception: HttpException) {
            Result.retry()
        }

    }
}