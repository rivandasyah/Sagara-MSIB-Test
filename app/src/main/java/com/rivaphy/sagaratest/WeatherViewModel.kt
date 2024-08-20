package com.rivaphy.sagaratest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rivaphy.sagaratest.repository.WeatherRepository
import com.rivaphy.sagaratest.response.ForecastItem
import com.rivaphy.sagaratest.response.WeatherResponse
import kotlinx.coroutines.launch

class WeatherViewModel(private val weatherRepository: WeatherRepository) : ViewModel() {

    private val _weatherData = MutableLiveData<WeatherResponse>()
    val weatherData: LiveData<WeatherResponse> = _weatherData

    private val _forecastData = MutableLiveData<List<ForecastItem>>()
    val forecastData: LiveData<List<ForecastItem>> = _forecastData

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val response = weatherRepository.getWeather(lat, lon)
                _weatherData.postValue(response)
            } catch (e: Exception) {
                _error.postValue("Failed to fetch weather data: ${e.message}")
            }
        }
    }

    fun fetchForecast(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val response = weatherRepository.getForecast(lat, lon)
                _forecastData.postValue(response.list.take(3))
            } catch (e: Exception) {
                _error.postValue("Failed to fetch forecast data: ${e.message}")
            }
        }
    }
}
