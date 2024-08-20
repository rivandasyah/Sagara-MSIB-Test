package com.rivaphy.sagaratest

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import android.Manifest
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.rivaphy.sagaratest.api.ApiConfig
import com.rivaphy.sagaratest.databinding.ActivityMainBinding
import com.rivaphy.sagaratest.repository.WeatherRepository
import com.rivaphy.sagaratest.response.ForecastItem
import com.rivaphy.sagaratest.response.WeatherResponse

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val apiService = ApiConfig().getApiService()
        val repository = WeatherRepository(apiService)
        val factory = WeatherViewModelFactory(repository)
        weatherViewModel = ViewModelProvider(this, factory)[WeatherViewModel::class.java]

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkLocationPermission()

        observeViewModel()
    }

    private fun observeViewModel() {
        showLoading(true)

        weatherViewModel.weatherData.observe(this) { weatherResponse ->
            showLoading(false)
            Log.d("MainActivity", "WeatherResponse: $weatherResponse")
            updateUI(weatherResponse)
        }

        weatherViewModel.forecastData.observe(this) { forecastResponse ->
            updateForecastUI(forecastResponse)
        }

        weatherViewModel.error.observe(this) { errorMessage ->
            showLoading(false)
            Log.e("MainActivity", "Error: $errorMessage")
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }


    private fun updateUI(weatherResponse: WeatherResponse) {
        binding.apply {
            val currentWeather = weatherResponse.main
            val weatherIconCode = weatherResponse.weather[0].icon
            val weatherIconUrl = "https://openweathermap.org/img/wn/${weatherIconCode}@2x.png"

            tvLocation.text = weatherResponse.name
            tvCelcius.text = "${currentWeather.temp.toInt()}°C"

            Glide.with(this@MainActivity)
                .load(weatherIconUrl)
                .into(ivProfile)
        }
    }

    private fun updateForecastUI(forecastResponse: List<ForecastItem>) {
        val weatherAdapter = WeatherAdapter(forecastResponse)
        binding.rvWeather.adapter = weatherAdapter
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvWeather.layoutManager = layoutManager

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastKnownLocation()
            } else {
                Toast.makeText(
                    this,
                    "Location permission required to fetch weather data",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            getLastKnownLocation()
        }
    }

    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                weatherViewModel.fetchWeather(location.latitude, location.longitude)
                weatherViewModel.fetchForecast(location.latitude, location.longitude)
            } else {
                Toast.makeText(this, "Lokasi tidak tersedia", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.pgWeather.visibility = View.VISIBLE
        } else {
            binding.pgWeather.visibility = View.GONE
        }
    }

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}