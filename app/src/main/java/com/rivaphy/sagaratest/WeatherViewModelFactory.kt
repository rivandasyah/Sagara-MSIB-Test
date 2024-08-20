package com.rivaphy.sagaratest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rivaphy.sagaratest.repository.WeatherRepository

class WeatherViewModelFactory(private val weatherRepository: WeatherRepository) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            return WeatherViewModel(weatherRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}