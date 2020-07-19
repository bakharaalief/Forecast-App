package com.example.forecastapp.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherService {

    @GET("weather?")
    fun getCurrentWeatherData(
        @Query("lon") lon: String,
        @Query("lat") lat: String,
        @Query("appid") app_id: String):Call<WeatherResponse>
}