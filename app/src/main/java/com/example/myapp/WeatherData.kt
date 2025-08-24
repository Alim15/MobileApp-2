package com.example.myapp
import com.example.myapp.Weather

data class Weather(
    val city: String = "",
    val temp: Double = 0.0,
    val humidity: Int = 0,
    val windSpeed: Double = 0.0,
    val windDir: String = "",
    val clouds: Int = 0,
    val condition: String = ""
)

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