package com.udacity.asteroidradar.db

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.models.Asteroid

@Database(entities = [AsteroidEntity::class, PodEntity::class], version = 1, exportSchema = false)
abstract class AsteroidDb: RoomDatabase() {
    abstract val asteroidDao: AsteroidDao
}

@Dao
interface AsteroidDao {

    // Asteroid operations

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllAsteroids(vararg asteroid: AsteroidEntity)

    @Query("select * from Asteroids order by datetime(closeApproachDate)")
    fun getAsteroids(): LiveData<List<AsteroidEntity>>



    // Pod operations

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPod(pod: PodEntity)

    @Query("select * from PictureOfDay order by picId desc limit 1")
    fun getTodayPod(): LiveData<PodEntity>

}

private lateinit var INSTANCE: AsteroidDb

fun getDatabase(context: Context) : AsteroidDb {
    synchronized(AsteroidDb::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                AsteroidDb::class.java,
                "asteroid")
                .fallbackToDestructiveMigration()
                .build()
        }
    }

    return INSTANCE
}