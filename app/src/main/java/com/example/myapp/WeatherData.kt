package com.example.myapp

data class Weather(
    val city: String,
    val temp: Double,
    val humidity: Int,
    val windSpeed: Double,
    val windDir: String,
    val clouds: Int,
    val condition: String
)