package com.example.myapp


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val LOCATION_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Исправлено: R.layout.activity_main
        requestLocation()
    }

    private fun requestLocation() {
        // Исправлено: правильная проверка разрешений
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getLocation()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocation()
        } else {
            loadWeather("Moscow")
        }
    }

    private fun getLocation() {
        val locationClient = LocationServices.getFusedLocationProviderClient(this)

        // Проверяем разрешение еще раз
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            loadWeather("Moscow")
            return
        }

        locationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                loadWeather("${location.latitude},${location.longitude}")
            } else {
                loadWeather("Moscow")
            }
        }.addOnFailureListener {
            loadWeather("Moscow")
        }
    }

    private fun loadWeather(location: String) {
        lifecycleScope.launch {
            try {
                val weather = ApiClient.getWeather(location)
                showWeather(weather)
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Ошибка: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showWeather(weather: Weather) {
        findViewById<TextView>(R.id.tvCity).text = weather.city
        findViewById<TextView>(R.id.tvTemp).text = "Температура: ${weather.temp}°C" // Исправлено: правильный текст
        findViewById<TextView>(R.id.tvHumidity).text = "Влажность: ${weather.humidity}%" // Исправлено: правильный символ %
        findViewById<TextView>(R.id.tvWind).text = "Ветер: ${weather.windSpeed} км/ч, ${weather.windDir}"
        findViewById<TextView>(R.id.tvClouds).text = "Облачность: ${weather.clouds}%" // Исправлено: закрыта кавычка
        findViewById<TextView>(R.id.tvCondition).text = weather.condition
    }
}