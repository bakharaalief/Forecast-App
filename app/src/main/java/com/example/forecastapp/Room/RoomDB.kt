package com.example.forecastapp.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.forecastapp.Room.Dao.LocationDao
import com.example.forecastapp.Room.Entity.CurrentLocation

@Database(entities = [CurrentLocation::class], version = 1)

abstract class RoomDB : RoomDatabase(){

    abstract fun locationDao() : LocationDao

    companion object{

        /*singleton method*/
        @Volatile
        private var INSTANCE: RoomDB? = null

        fun getDB(context: Context) : RoomDB? {

            if(INSTANCE == null){

                synchronized(RoomDB::class.java){
                    INSTANCE =
                        Room
                            .databaseBuilder(context.applicationContext, RoomDB::class.java, "Room_DB")
                            .allowMainThreadQueries() //allow room data in main
                            .build()
                }

            }

            return INSTANCE

        }

    }
}