package com.example.forecastapp.Room.Dao

import androidx.room.*
import com.example.forecastapp.Room.Entity.CurrentLocation

@Dao
interface LocationDao {

    @Query("SELECT * FROM currentlocation")
    fun getAll(): List<CurrentLocation>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(currentLocation: CurrentLocation)

    @Update
    fun update(currentLocation: CurrentLocation)
}