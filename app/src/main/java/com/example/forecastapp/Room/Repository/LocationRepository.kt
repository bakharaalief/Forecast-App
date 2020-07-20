package com.example.forecastapp.Room.Repository

import com.example.forecastapp.Room.Dao.LocationDao
import com.example.forecastapp.Room.Entity.CurrentLocation

class LocationRepository (val locationDao: LocationDao){

    val allLocation = locationDao.getAll()

    suspend fun insertLocation(currentLocation: CurrentLocation){
        locationDao.insert(currentLocation)
    }

    suspend fun updateLocation(currentLocation: CurrentLocation){
        locationDao.update(currentLocation)
    }
}