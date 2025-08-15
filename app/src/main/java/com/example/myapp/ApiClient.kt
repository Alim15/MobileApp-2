package com.example.myapp
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.android.gms.common.api.Response

object ApiClient {
    private const val BASE_URL = "https://api.weatherapi.com/v1/"
    private const val API_KEY = "3ba42c4971284b86a12220114251308"
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(WeatherApi::class.java)

    suspend fun getWeather(location: String): Weather {
        val response = service.getWeather(API_KEY, location)
        val data = response.body()!!
        return Weather(
            city = data.location.name,
            temp = data.current.temp_c,
            humidity = data.current.humidity,
            windSpeed = data.current.wind_kph,
            windDir = data.current.wind_dir,
            clouds = data.current.cloud,
            condition = data.current.condition.text
        )
    }
}


