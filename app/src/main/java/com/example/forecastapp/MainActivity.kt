package com.example.forecastapp

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.example.forecastapp.api.WeatherResponse
import com.example.forecastapp.api.WeatherService
import com.google.android.gms.location.LocationServices
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.weather_detail.*
import kotlinx.android.synthetic.main.weather_detail.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    var lon = "106.85" //default location
    var lat = "-6.21"
    val appid = "b075fc1f8e35b9bf314107ce9fae987e"

    var sudah : Boolean = false

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //make taskbar transparent
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
        }
        if (Build.VERSION.SDK_INT >= 19) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
        }

        //get location
        currentLocation()

        //call api
        currentData()

        //refresh
        refresh_layout.setOnRefreshListener {
            Toast.makeText(this, "Refresh Data", Toast.LENGTH_SHORT).show()
            currentData()
            refresh_layout.isRefreshing = false
        }

        //configuration for slider
        sliding_layout.addPanelSlideListener(slideListener())

        //get location with click
        get_location.setOnClickListener {
            Toast.makeText(this, "get location", Toast.LENGTH_SHORT).show()
            currentLocation()
            currentData()
        }
    }

    //make taskbar transparent
    private fun setWindowFlag(bits : Int, on : Boolean){
        val winParams = window.attributes

        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        window.attributes = winParams
    }

    private fun slideListener() : SlidingUpPanelLayout.PanelSlideListener {
        return object : SlidingUpPanelLayout.PanelSlideListener{

            override fun onPanelSlide(panel: View?, slideOffset: Float) {
            }

            override fun onPanelStateChanged(
                panel: View?,
                previousState: SlidingUpPanelLayout.PanelState?,
                newState: SlidingUpPanelLayout.PanelState?
            ) {

                if(sudah){

                    if(newState == SlidingUpPanelLayout.PanelState.COLLAPSED){

                        panel!!.sunrise_time.background?.alpha = 255 //full alpha
                        panel.sunset_time.background?.alpha = 255
                        panel.pressure_status.background?.alpha = 255
                        panel.wind_status.background?.alpha = 255
                        panel.humidity_status.background?.alpha = 255
                        panel.max_temp_status.background?.alpha = 255
                        panel.min_temp_status.background?.alpha = 255
                        panel.clouds_status.background?.alpha = 255
                    }

                    if(newState == SlidingUpPanelLayout.PanelState.EXPANDED){

                        panel!!.sunrise_time.background?.alpha = 255 //full alpha
                        panel.sunset_time.background?.alpha = 255
                        panel.pressure_status.background?.alpha = 255
                        panel.wind_status.background?.alpha = 255
                        panel.humidity_status.background?.alpha = 255
                        panel.max_temp_status.background?.alpha = 255
                        panel.min_temp_status.background?.alpha = 255
                        panel.clouds_status.background?.alpha = 255

                    }
                }

                else{

                    /*set for the first time*/
                    panel!!.sunrise_time.background?.alpha = 0
                    panel.sunset_time.background?.alpha = 0
                    panel.pressure_status.background?.alpha = 0
                    panel.wind_status.background?.alpha = 0
                    panel.humidity_status.background?.alpha = 0
                    panel.max_temp_status.background?.alpha = 0
                    panel.min_temp_status.background?.alpha = 0
                    panel.clouds_status.background?.alpha = 0

                    if(newState == SlidingUpPanelLayout.PanelState.DRAGGING){

                        val animation = AnimationUtils.loadAnimation(panel.context, R.anim.vertical_fade_in)

                        panel.sunrise_time.startAnimation(animation)
                        panel.sunset_time.startAnimation(animation)
                        panel.pressure_status.startAnimation(animation)
                        panel.wind_status.startAnimation(animation)
                        panel.humidity_status.startAnimation(animation)
                        panel.max_temp_status.startAnimation(animation)
                        panel.min_temp_status.startAnimation(animation)
                        panel.clouds_status.startAnimation(animation)
                    }

                    if(newState == SlidingUpPanelLayout.PanelState.EXPANDED){

                        panel.sunrise_time.background?.alpha = 255 //full alpha
                        panel.sunset_time.background?.alpha = 255
                        panel.pressure_status.background?.alpha = 255
                        panel.wind_status.background?.alpha = 255
                        panel.humidity_status.background?.alpha = 255
                        panel.max_temp_status.background?.alpha = 255
                        panel.min_temp_status.background?.alpha = 255
                        panel.clouds_status.background?.alpha = 255

                    }

                    sudah = true
                }



            }

        }
    }

    private fun currentData(){

        //loading
        weather_content.visibility = View.GONE
        weather_loading.visibility = View.VISIBLE
        weather_error.visibility = View.GONE
        city_location_detail.setText("loading")
        time_detail.setText("loading")

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(WeatherService::class.java)
        val call = service.getCurrentWeatherData(lon, lat, appid)
        call.enqueue(object : Callback<WeatherResponse> {

            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                if(response.code() == 200){
                    val weatherResponse = response.body()!!

                    weather_content.visibility = View.VISIBLE
                    weather_loading.visibility = View.GONE
                    weather_error.visibility = View.GONE

                    val iconWeather = weatherResponse.weather!![0]!!.icon
                    val weather = weatherResponse.weather[0]!!.main
                    val weatherDetail = weatherResponse.weather[0]!!.description
                    val city = weatherResponse.name
                    val country = weatherResponse.sys!!.country
                    val temp = weatherResponse.main!!.temp
                    val sunriseTime = weatherResponse.sys.sunrise
                    val sunsetTime = weatherResponse.sys.sunset
                    val pressure = weatherResponse.main.pressure
                    val windSpeed = weatherResponse.wind!!.speed
                    val humidity = weatherResponse.main.humidity
                    val maxTemp = weatherResponse.main.tempMax
                    val minTemp = weatherResponse.main.tempMin
                    val allClouds = weatherResponse.clouds!!.all

                    sudah = false

                    //weather
                    getIconBackgroundWeather(weather!!)
                    weather_description.setText(weather)
                    city_location.setText(city)
                    temp_weather.setText("${getCelcius(temp!!)} ⁰C")
                    city_location_detail.setText("${city}, ${country}")
                    time_detail.setText(getTime(Date()))
                    sunrise_time_detail.setText(getTime(Date(sunriseTime!!.toLong()*1000)))
                    sunset_time_detail.setText(getTime(Date(sunsetTime!!.toLong()*1000)))
                    pressure_detail.setText("${pressure} hpa")
                    wind_detail.setText("${windSpeed} m/s")
                    humidity_detail.setText("${humidity} %")
                    max_temp_detail.setText("${getCelcius(maxTemp!!)} ⁰C")
                    min_temp_detail.setText("${getCelcius(minTemp!!)} ⁰C")
                    clouds_detail.setText(allClouds.toString())
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {

                weather_content.visibility = View.GONE
                weather_loading.visibility = View.GONE
                weather_error.visibility = View.VISIBLE

            }

        })
    }

    @SuppressLint("MissingPermission")
    private fun currentLocation(){
        val mFusedLocation = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocation.lastLocation.addOnSuccessListener { location ->
            val latInput = location.latitude
            val lonInput = location.longitude

            lat = latInput.toString()
            lon = lonInput.toString()
        }
    }

    private fun getTime(time: Date) : String {
        val format = SimpleDateFormat("hh:mm a")
        val result = format.format(time)
        return result
    }

    private fun getIconBackgroundWeather(weather : String){
        Log.i("wadaw", "${weather}")
        when(weather){
            "Rain" -> {
                weather_icon.setImageResource(R.drawable.ic_rain)
                wallpaper_background.setBackgroundResource(R.drawable.gambar_hujan)
            }
            "Clouds" ->{
                weather_icon.setImageResource(R.drawable.ic_cloudy)
                wallpaper_background.setBackgroundResource(R.drawable.gambar_awan)
            }
            "Thunderstrom" ->{
                weather_icon.setImageResource(R.drawable.ic_thunderstorm)
                wallpaper_background.setBackgroundResource(R.drawable.gambar_hujan)
            }
            "Haze"->{
                weather_icon.setImageResource(R.drawable.ic_haze)
                wallpaper_background.setBackgroundResource(R.drawable.gambar_haze)
            }
            else ->{
                weather_icon.setImageResource(R.drawable.ic_planet)
                wallpaper_background.setBackgroundResource(R.drawable.gambar_lain)
            }
        }
    }
    
    private fun getCelcius(kelvin : Double) : Int{
        val celcius = kelvin - 273.15
        val result = celcius.toInt()
        return result
    }
}





