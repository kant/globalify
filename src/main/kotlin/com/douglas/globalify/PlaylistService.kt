package com.douglas.globalify

import com.google.gson.GsonBuilder
import org.springframework.stereotype.Service
import java.util.logging.Logger

@Service
class PlaylistService {
    val logger = Logger.getLogger(PlaylistService::class.simpleName)

    val BASE_URL = "https://api.openweathermap.org/data/2.5/weather"
    val LOCATION_PARAM = "q"
    val APPID_PARAM = "appid"
    val APPID_VALUE = "a5b5cbc0da6ddfb96bd03be9872d8405"
    val UNITS_PARAM = "units"
    val UNITS_VALUE = "metric"

    fun getPlaylistForCity(city: String): PlaylistDTO {
        try {
            val resp = khttp.get(url = BASE_URL, params = mapOf(LOCATION_PARAM to city,
                    APPID_PARAM to APPID_VALUE, UNITS_PARAM to UNITS_VALUE))

            if (resp.statusCode == 200 && !resp.text.isEmpty()) {
                logger.info("Response: ${resp.text}")

                val responseObj = GsonBuilder().setPrettyPrinting().create()
                        .fromJson<WeatherResponse>(resp.text, WeatherResponse::class.java)
                logger.info(responseObj.toString())

                return PlaylistDTO(responseObj.name, responseObj.main.temp)
            } else {
                throw ServiceException("Location not found")
            }
        } catch (e: Exception) {
            throw ServiceException("Error processing request: ${e.message}")
        }
    }
}

data class WeatherResponse(val name: String, val main: WeatherResponseMain, val cod: Int)
data class WeatherResponseMain(val temp: Float)

class ServiceException(message: String?) : Exception(message)
