package com.douglas.globalify

import com.fasterxml.jackson.annotation.JsonInclude

object WeatherAPI {
    val BASE_URL = "https://api.openweathermap.org/data/2.5/weather"
    val LOCATION_PARAM = "q"
    val APPID_PARAM = "appid"
    val APPID_VALUE = "a5b5cbc0da6ddfb96bd03be9872d8405"
    val UNITS_PARAM = "units"
    val UNITS_VALUE = "metric"
    val LAT_PARAM = "lat"
    val LON_PARAM = "lon"
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

data class WeatherTempResponse(val name: String, val main: WeatherTemp, val cod: Int)
data class WeatherTemp(val temp: Float)

data class SpotifyResponse(val playlists: SpotifyPlaylistResponse)
data class SpotifyAuthResponse(val access_token: String, val token_type: String, val expires_in: Int)
data class SpotifyPlaylistResponse(val external_urls: SpotifyUrl, val items: List<SpotifyPlaylist>,
                                   val tracks: SpotifyTrackResponse, val name: String)

data class SpotifyTrackResponse(val href: String, val items: List<SpotifyTrack>)
data class SpotifyAuth(val token: String, val tokenType: String, val expiration: Long)
data class SpotifyPlaylist(val name: String, val external_urls: SpotifyUrl, val href: String)
data class SpotifyTrack(val track: SpotifyTrackInfo)
data class SpotifyTrackInfo(val name: String, val artists: List<SpotifyArtistInfo>)
data class SpotifyArtistInfo(val name: String)
data class SpotifyUrl(val spotify: String)


@JsonInclude(JsonInclude.Include.NON_NULL)
open class ResposeDTO(val success: Boolean, val message: String?)
data class PlaylistDTO(val location: String, val temperature: Float, val genre: String,
                       val spotify: SpotifyPlaylistResponse)
    : ResposeDTO(true, null)

class ServiceException(message: String?) : Exception(message)

