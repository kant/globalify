package com.douglas.globalify

import com.google.gson.Gson
import khttp.get
import khttp.post
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.logging.Logger

const val MINUTES_OFFSET = 60

const val LOCATION_NOT_FOUND = "Location not found"
const val SPOTIFY_COMM_ERROR = "Could not communicate with Spotify"
const val SPOTIFY_AUTH_ERROR = "Spotify authentication nonexistant or expired"
const val SPOTIFY_TRACKS_NOT_FOUND = "Could not find tracks for playlist"
const val SPOTIFY_PLAYLIST_NOT_FOUND = "Could not find playlist for genre"

@Service
class PlaylistService {
    val logger = Logger.getLogger(PlaylistService::class.simpleName)!!
    var spotifyAuth: SpotifyAuth? = null

    /**
     * Get playlist suggestion for a city
     */
    fun getPlaylistForLocation(city: String): PlaylistDTO {
        try {
            val respWeather = getTemperature(city)
            logger.info("Temp: ${respWeather.main.temp}")

            val genre = getGenre(respWeather.main.temp)
            logger.info("Genre: $genre")

            val playlist = getPlaylist(genre)

            return PlaylistDTO(respWeather.name, respWeather.main.temp, genre, playlist)
        } catch (e: ServiceException) {
            throw e
        } catch (e: Exception) {
            throw ServiceException("Error processing request: ${e.message}")
        }
    }

    /**
     * Get playlist suggestion for gps coordinate
     */
    fun getPlaylistForLocation(coord: Pair<Double, Double>): PlaylistDTO {
        try {
            val respWeather = getTemperature(coord.first, coord.second)
            logger.info("Temp: ${respWeather.main.temp}")

            val genre = getGenre(respWeather.main.temp)
            logger.info("Genre: $genre")

            val playlist = getPlaylist(genre)

            return PlaylistDTO(respWeather.name, respWeather.main.temp, genre, playlist)
        } catch (e: ServiceException) {
            throw e
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
            throw ServiceException(LOCATION_NOT_FOUND)

        // parses response json to matching object
        val respWeather = Gson().fromJson<WeatherTempResponse>(reqWeather.text,
                WeatherTempResponse::class.java)
        if (respWeather?.main?.temp == null) throw ServiceException(LOCATION_NOT_FOUND)
        return respWeather
    }

    private fun getTemperature(lat: Double, lon: Double): WeatherTempResponse {
        val reqWeather = get(url = WeatherAPI.BASE_URL, params = mapOf(
                WeatherAPI.LAT_PARAM to lat.toString(),
                WeatherAPI.LON_PARAM to lon.toString(),
                WeatherAPI.APPID_PARAM to WeatherAPI.APPID_VALUE,
                WeatherAPI.UNITS_PARAM to WeatherAPI.UNITS_VALUE))

        logger.info("Weather status code ${reqWeather.statusCode}")
//        logger.info(reqWeather.text)

        if (reqWeather.statusCode != 200 || reqWeather.text.isEmpty())
            throw ServiceException(LOCATION_NOT_FOUND)

        // parses response json to matching object
        val respWeather = Gson().fromJson<WeatherTempResponse>(reqWeather.text,
                WeatherTempResponse::class.java)
        if (respWeather?.main?.temp == null) throw ServiceException(LOCATION_NOT_FOUND)
        return respWeather
    }

    private fun getGenre(temp: Float) = when (temp) {
        in 31.01..Double.MAX_VALUE -> MusicGenre.PARTY
        in 15.01..30.00 -> MusicGenre.POP
        in 10.01..15.00 -> MusicGenre.ROCK
        else -> MusicGenre.CLASSICAL
    }

    private fun authorizeSpotify() {
        val reqAuth = post(url = SpotifyAPI.BASE_AUTH_URL,
                headers = mapOf(SpotifyAPI.AUTH_HEADER to SpotifyAPI.AUTH_VALUE),
                data = mapOf(SpotifyAPI.GRANT_TYPE_PARAM to SpotifyAPI.GRANT_TYPE_VALUE))

        logger.info("Spotify auth status code ${reqAuth.statusCode}")
//        logger.info(reqAuth.text)

        if (reqAuth.statusCode != 200 || reqAuth.text.isEmpty())
            throw ServiceException(SPOTIFY_COMM_ERROR)

        val respAuth = Gson().fromJson<SpotifyAuthResponse>(reqAuth.text,
                SpotifyAuthResponse::class.java)
        if (respAuth?.access_token == null || respAuth.access_token.isEmpty())
            throw ServiceException(SPOTIFY_COMM_ERROR)

        spotifyAuth = SpotifyAuth(respAuth.access_token, respAuth.token_type,
                Instant.now().epochSecond.plus(respAuth.expires_in).minus(MINUTES_OFFSET))
    }

    private fun getPlaylist(genre: String): SpotifyPlaylistResponse {
        if (spotifyAuth == null || spotifyAuth!!.expiration < Instant.now().epochSecond) {
            logger.info(SPOTIFY_AUTH_ERROR)
            authorizeSpotify()
        }

        val reqSearch = get(url = SpotifyAPI.BASE_SEARCH_URL,
                headers = mapOf(SpotifyAPI.AUTH_HEADER
                        to "${spotifyAuth!!.tokenType} ${spotifyAuth!!.token}"),
                params = mapOf(SpotifyAPI.TYPE_PARAM to SpotifyAPI.TYPE_VALUE,
                        SpotifyAPI.LIMIT_PARAM to SpotifyAPI.LIMIT_VALUE,
                        SpotifyAPI.SEARCH_PARAM to genre))
        if (reqSearch.statusCode != 200 || reqSearch.text.isEmpty())
            throw ServiceException(SPOTIFY_PLAYLIST_NOT_FOUND)
        logger.info("Spotify search status code ${reqSearch.statusCode}")
//        logger.info(reqSearch.text)

        val respSearch = Gson().fromJson<SpotifyResponse>(reqSearch.text,
                SpotifyResponse::class.java)
        if (respSearch?.playlists?.items == null
                || respSearch.playlists.items.isEmpty()
                || respSearch.playlists.items[0].href.isEmpty())
            throw ServiceException(SPOTIFY_PLAYLIST_NOT_FOUND)

        val reqPlaylist = get(url = respSearch.playlists.items[0].href,
                headers = mapOf(SpotifyAPI.AUTH_HEADER
                        to "${spotifyAuth!!.tokenType} ${spotifyAuth!!.token}"))

        logger.info("Spotify playlist status code ${reqPlaylist.statusCode}")
//        logger.info(reqPlaylist.text)

        if (reqPlaylist.statusCode != 200 || reqPlaylist.text.isEmpty())
            throw ServiceException(SPOTIFY_TRACKS_NOT_FOUND)

        return Gson().fromJson(reqPlaylist.text, SpotifyPlaylistResponse::class.java)
    }
}
