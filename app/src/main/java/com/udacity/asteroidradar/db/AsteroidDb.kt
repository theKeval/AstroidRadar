package com.udacity.asteroidradar.db

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.models.Asteroid

@Database(entities = [AsteroidEntity::class], version = 1)
abstract class AsteroidDb: RoomDatabase() {
    abstract val asteroidDao: AsteroidDao
}

@Dao
interface AsteroidDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroid: AsteroidEntity)

    @Query("select * from Asteroids order by datetime(closeApproachDate)")
    fun getAsteroids(): List<AsteroidEntity>
}

private lateinit var INSTANCE: AsteroidDb

fun getDatabase(context: Context) : AsteroidDb {
    synchronized(AsteroidDb::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                AsteroidDb::class.java,
                "asteroid").build()
        }
    }

    return INSTANCE
}