package com.example.myapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private val LOCATION_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_main)

            if (isOnline()) {
                requestLocation()
            } else {
                Toast.makeText(this, "Нет интернет-соединения", Toast.LENGTH_LONG).show()
                showDefaultWeather()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun requestLocation() {
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
        if (requestCode == LOCATION_CODE && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            getLocation()
        } else {
            showDefaultWeather()
        }
    }

    private fun getLocation() {
        val locationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            showDefaultWeather()
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
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.Main) {
                    if (!isDestroyed) {
                        findViewById<TextView>(R.id.tvLoading)?.visibility = TextView.VISIBLE
                    }
                }

                val weather = ApiClient.getWeather(location)

                withContext(Dispatchers.Main) {
                    if (!isDestroyed) {
                        findViewById<TextView>(R.id.tvLoading)?.visibility = TextView.GONE
                        showWeather(weather)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    if (!isDestroyed) {
                        findViewById<TextView>(R.id.tvLoading)?.visibility = TextView.GONE
                        Toast.makeText(
                            this@MainActivity,
                            "Ошибка: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        showDefaultWeather()
                    }
                }
            }
        }
    }

    private fun showDefaultWeather() {
        val defaultWeather = Weather(
            city = "Moscow",
            temp = 0.0,
            humidity = 0,
            windSpeed = 0.0,
            windDir = "",
            clouds = 0,
            condition = "Нет данных"
        )
        showWeather(defaultWeather)
    }

    private fun showWeather(weather: Weather) {
        findViewById<TextView>(R.id.tvCity)?.text = weather.city
        findViewById<TextView>(R.id.tvClouds)?.text = "Облачность: ${weather.clouds}%"
        findViewById<TextView>(R.id.tvCondition)?.text = weather.condition
        findViewById<TextView>(R.id.tvTemperature)?.text = "Температура: ${weather.temp}°C"
        findViewById<TextView>(R.id.tvHumidity)?.text = "Влажность: ${weather.humidity}%"
        findViewById<TextView>(R.id.tvWind)?.text = "Ветер: ${weather.windSpeed} км/ч, ${weather.windDir}"
    }

    private fun isOnline(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}