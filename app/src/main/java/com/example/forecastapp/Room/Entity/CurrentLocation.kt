package com.example.forecastapp.Room.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CurrentLocation (
    @PrimaryKey val uid : Int,
    @ColumnInfo(name = "lon") val lon : String,
    @ColumnInfo(name = "lat") val lat : String
)