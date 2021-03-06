package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.db.getDatabase
import com.udacity.asteroidradar.models.Asteroid
import com.udacity.asteroidradar.repository.AsteroidFilter
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getDatabase(application)
    private val repository = AsteroidRepository(database)

    private var _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    val pod = repository.pod

    private var _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()
    val navigateToSelectedAsteroid: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid

    init {
        _asteroids.value = emptyList()

        viewModelScope.launch {
            _asteroids.value = repository.getAsteroids(AsteroidFilter.SHOW_TODAY)

            try {
                repository.refreshAsteroids()
                repository.refreshPod()
            } catch (exce: Exception) {
                Log.i(
                    "MainViewModel",
                    "Unable to refresh the data. Mostly because of no internet conection."
                )
            }
        }

    }

    fun showAsteroidDetail(asteroid: Asteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }

    fun doneShowingAsteroidDetail() {
        _navigateToSelectedAsteroid.value = null
    }

    fun updateFilter(filter: AsteroidFilter) {
        viewModelScope.launch {
            _asteroids.value = repository.getAsteroids(filter)
        }
    }


    class Factory(private val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }

            throw IllegalArgumentException("Unable to construct ViewModel")
        }

    }

}