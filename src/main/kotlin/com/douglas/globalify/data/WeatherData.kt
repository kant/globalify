package com.douglas.globalify.data

object WeatherAPI {
    const val BASE_URL = "https://api.openweathermap.org/data/2.5/weather"
    const val LOCATION_PARAM = "q"
    const val APPID_PARAM = "appid"
    const val APPID_VALUE = "a5b5cbc0da6ddfb96bd03be9872d8405"
    const val UNITS_PARAM = "units"
    const val UNITS_VALUE = "metric"
    const val LAT_PARAM = "lat"
    const val LON_PARAM = "lon"
}

data class WeatherTempResponse(val name: String, val main: WeatherTemp, val cod: Int)
data class WeatherTemp(val temp: Float)