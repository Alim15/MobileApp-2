package com.example.myapp

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("current.json")
    suspend fun getWeather(
        @Query("key") apiKey: String,
        @Query("q") location: String
    ): Response<WeatherResponse>
}

annotation class GET(val value: String)

// Простой ответ от API
data class WeatherResponse(
    val location: Location,
    val current: Current
)

data class Location(
    val name: String
)

data class Current(
    val temp_c: Double,
    val humidity: Int,
    val wind_kph: Double,
    val wind_dir: String,
    val cloud: Int,
    val condition: Condition
)

data class Condition(
    val text: String
)
