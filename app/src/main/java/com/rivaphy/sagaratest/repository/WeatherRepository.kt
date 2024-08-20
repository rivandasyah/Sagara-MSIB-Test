package com.rivaphy.sagaratest.repository

import com.rivaphy.sagaratest.BuildConfig
import com.rivaphy.sagaratest.api.ApiService
import com.rivaphy.sagaratest.response.ForecastResponse
import com.rivaphy.sagaratest.response.WeatherResponse

class WeatherRepository(private val apiService: ApiService) {

    suspend fun getWeather(lat: Double, lon: Double): WeatherResponse {
        return apiService.getWeather(lat, lon, "metric", BuildConfig.WEATHER_API_KEY)
    }

    suspend fun getForecast(lat: Double, lon: Double): ForecastResponse {
        return apiService.getForecast(lat, lon, "metric", BuildConfig.WEATHER_API_KEY)
    }
}
