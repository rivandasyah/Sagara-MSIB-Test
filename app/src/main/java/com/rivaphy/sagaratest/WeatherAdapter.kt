package com.rivaphy.sagaratest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rivaphy.sagaratest.response.ForecastItem

class WeatherAdapter(private val dailyWeather: List<ForecastItem>) :
    RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {

    inner class WeatherViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivWeatherIcon: ImageView = view.findViewById(R.id.iv_item_weather)
        val tvTemperature: TextView = view.findViewById(R.id.tv_item_celcius)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_weather, parent, false)
        return WeatherViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val weather = dailyWeather[position]
        val weatherIconCode = weather.weather[0].icon
        val weatherIconUrl = "https://openweathermap.org/img/wn/${weatherIconCode}@2x.png"

        holder.tvTemperature.text = "${weather.main.temp.toInt()}°C"

        Glide.with(holder.itemView.context)
            .load(weatherIconUrl)
            .into(holder.ivWeatherIcon)
    }

    override fun getItemCount(): Int = dailyWeather.size
}
