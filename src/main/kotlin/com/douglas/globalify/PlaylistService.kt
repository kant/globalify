package com.douglas.globalify

import com.google.gson.Gson
import khttp.get
import khttp.post
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.logging.Logger

@Service
class PlaylistService {

    val MINUTES_OFFSET = 60
    val logger = Logger.getLogger(PlaylistService::class.simpleName)
    var spotifyAuth: SpotifyAuth? = null

    fun getPlaylistForCity(city: String): PlaylistDTO {
        try {
            val respWeather = getTemperature(city)

            val genre = getGenre(respWeather.main.temp)
            logger.info("Genre: $genre")

            if (spotifyAuth == null || spotifyAuth!!.expiration < Instant.now().epochSecond) {
                logger.info("Spotify authentication nonexistant or expired")
                authorizeSpotify()
            }

            val reqSearch = khttp.get(url = SpotifyAPI.BASE_SEARCH_URL,
                    headers = mapOf(SpotifyAPI.AUTH_HEADER
                            to "${spotifyAuth!!.tokenType} ${spotifyAuth!!.token}"),
                    params = mapOf(SpotifyAPI.TYPE_PARAM to SpotifyAPI.TYPE_VALUE,
                            SpotifyAPI.LIMIT_PARAM to SpotifyAPI.LIMIT_VALUE,
                            SpotifyAPI.SEARCH_PARAM to genre))
            if (reqSearch.statusCode != 200 || reqSearch.text.isEmpty())
                throw ServiceException("Could not find playlist for genre $genre")
            logger.info("Spotify search status code ${reqSearch.statusCode}")
            logger.info(reqSearch.text)

            val respSearch = Gson().fromJson<SpotifyResponse>(reqSearch.text,
                    SpotifyResponse::class.java)
            if (respSearch?.playlists?.items == null
                    || respSearch.playlists.items.isEmpty()
                    || respSearch.playlists.items[0].href.isEmpty())
                throw ServiceException("Could not find playlist for genre $genre")

            val reqPlayl = khttp.get(url = respSearch.playlists.items[0].href,
                    headers = mapOf(SpotifyAPI.AUTH_HEADER
                            to "${spotifyAuth!!.tokenType} ${spotifyAuth!!.token}"))

            logger.info("Spotify tracks status code ${reqPlayl.statusCode}")
            logger.info(reqPlayl.text)
            if (reqPlayl.statusCode != 200 || reqPlayl.text.isEmpty())
                throw ServiceException("Could not find tracks for playlist")

            val respPlayl = Gson().fromJson<SpotifyPlaylistResponse>(reqPlayl.text,
                    SpotifyPlaylistResponse::class.java)

            return PlaylistDTO(respWeather.name, respWeather.main.temp, genre, respPlayl)
        } catch (e: Exception) {
            throw ServiceException("Error processing request: ${e.message}")
        }
    }

    private fun getTemperature(city: String): WeatherTempResponse {
        val reqWeather = get(url = WeatherAPI.BASE_URL, params = mapOf(
                WeatherAPI.LOCATION_PARAM to city,
                WeatherAPI.APPID_PARAM to WeatherAPI.APPID_VALUE,
                WeatherAPI.UNITS_PARAM to WeatherAPI.UNITS_VALUE))
        logger.info("Weather status code ${reqWeather.statusCode}")
        logger.info(reqWeather.text)
        if (reqWeather.statusCode != 200 || reqWeather.text.isEmpty())
            throw ServiceException("Location not found")

        val respWeather = Gson().fromJson<WeatherTempResponse>(reqWeather.text,
                WeatherTempResponse::class.java)
        if (respWeather?.main?.temp == null) throw ServiceException("Location not found")
        return respWeather
    }

    private fun authorizeSpotify() {
        val reqAuth = post(url = SpotifyAPI.BASE_AUTH_URL,
                headers = mapOf(SpotifyAPI.AUTH_HEADER to SpotifyAPI.AUTH_VALUE),
                data = mapOf(SpotifyAPI.GRANT_TYPE_PARAM to SpotifyAPI.GRANT_TYPE_VALUE))
        logger.info("Spotify auth status code ${reqAuth.statusCode}")
        logger.info(reqAuth.text)

        if (reqAuth.statusCode != 200 || reqAuth.text.isEmpty())
            throw ServiceException("Could not communicate with Spotify")

        val respAuth = Gson().fromJson<SpotifyAuthResponse>(reqAuth.text,
                SpotifyAuthResponse::class.java)
        if (respAuth?.access_token == null || respAuth.access_token.isEmpty())
            throw ServiceException("Could not communicate with Spotify")

        spotifyAuth = SpotifyAuth(respAuth.access_token, respAuth.token_type,
                Instant.now().epochSecond.plus(respAuth.expires_in).minus(MINUTES_OFFSET))
    }

    fun getGenre(temp: Float) = when (temp) {
        in 31..Int.MAX_VALUE -> MusicGenre.PARTY
        in 15..30 -> MusicGenre.POP
        in 10..14 -> MusicGenre.ROCK
        else -> MusicGenre.CLASSICAL
    }

}

data class WeatherTempResponse(val name: String, val main: WeatherTemp, val cod: Int)
data class WeatherTemp(val temp: Float)

data class SpotifyResponse(val playlists: SpotifyPlaylistResponse)
data class SpotifyAuthResponse(val access_token: String, val token_type: String, val expires_in: Int)
data class SpotifyPlaylistResponse(val items: List<SpotifyPlaylist>, val tracks: SpotifyTrackResponse)
data class SpotifyTrackResponse(val href: String, val items: List<SpotifyTrack>)

data class SpotifyAuth(val token: String, val tokenType: String, val expiration: Long)
data class SpotifyPlaylist(val name: String, val external_urls: SpotifyUrl, val href: String)
data class SpotifyTrack(val track: SpotifyTrackInfo)
data class SpotifyTrackInfo(val name: String)
data class SpotifyUrl(val spotify: String)


class ServiceException(message: String?) : Exception(message)

object WeatherAPI {
    val BASE_URL = "https://api.openweathermap.org/data/2.5/weather"
    val LOCATION_PARAM = "q"
    val APPID_PARAM = "appid"
    val APPID_VALUE = "a5b5cbc0da6ddfb96bd03be9872d8405"
    val UNITS_PARAM = "units"
    val UNITS_VALUE = "metric"
}

object SpotifyAPI {
    val BASE_AUTH_URL = "https://accounts.spotify.com/api/token"
    val BASE_SEARCH_URL = "https://api.spotify.com/v1/search"
    val AUTH_HEADER = "Authorization"
    val AUTH_VALUE = "Basic Y2RhNmY5NWM4NjgyNGVkMWIwNDhiMWZmMzYxOWQ5Y2M6MTUwNjc4MTM0Y2E3NGJkNmI4ZTE3MzU1NDkzNjEwM2I="
    val GRANT_TYPE_PARAM = "grant_type"
    val GRANT_TYPE_VALUE = "client_credentials"
    val TYPE_PARAM = "type"
    val TYPE_VALUE = "playlist"
    val LIMIT_PARAM = "limit"
    val LIMIT_VALUE = "1"
    val SEARCH_PARAM = "q"
}

object MusicGenre {
    val PARTY = "party"
    val POP = "pop"
    val ROCK = "rock"
    val CLASSICAL = "classical"
}
