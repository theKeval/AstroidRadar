package com.udacity.asteroidradar.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.models.PictureOfDay
import com.udacity.asteroidradar.utils.Constants.BASE_URL
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()



interface AsteroidService {

    @GET("feed")
    suspend fun getAsteroids(
        @Query("start_date")
        startDate: String,

        @Query("end_date")
        endDate: String,

        @Query("api_key")
        apiKey: String

    ): String

}

interface PodService {

    @GET("apod")
    suspend fun getPod(

        @Query("api_key")
        apiKey: String = "DEMO_KEY"

    ) : Deferred<PictureOfDay>

}

object AsteroidApi {

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL+"neo/rest/v1/")
        .addConverterFactory(ScalarsConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

//    private val retrofitForPOD = Retrofit.Builder()
//        .baseUrl(BASE_URL+"planetary")
//        .addConverterFactory(MoshiConverterFactory.create(moshi))
//        .addCallAdapterFactory(CoroutineCallAdapterFactory())
//        .build()

    val retrofitService: AsteroidService by lazy {
        retrofit.create(AsteroidService::class.java)
    }

//    val retrofitForPodService: PodService by lazy {
//        retrofitForPOD.create(PodService::class.java)
//    }
}