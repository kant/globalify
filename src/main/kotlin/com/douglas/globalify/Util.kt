package com.douglas.globalify

import com.fasterxml.jackson.annotation.JsonInclude

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

object SpotifyAPI {
   const val BASE_AUTH_URL = "https://accounts.spotify.com/api/token"
   const val BASE_SEARCH_URL = "https://api.spotify.com/v1/search"
   const val AUTH_HEADER = "Authorization"
   const val AUTH_VALUE = "Basic Y2RhNmY5NWM4NjgyNGVkMWIwNDhiMWZmMzYxOWQ5Y2M6MTUwNjc4MTM0Y2E3NGJkNmI4ZTE3MzU1NDkzNjEwM2I="
   const val GRANT_TYPE_PARAM = "grant_type"
   const val GRANT_TYPE_VALUE = "client_credentials"
   const val TYPE_PARAM = "type"
   const val TYPE_VALUE = "playlist"
   const val LIMIT_PARAM = "limit"
   const val LIMIT_VALUE = "1"
   const val SEARCH_PARAM = "q"
}

object MusicGenre {
   const val PARTY = "party"
   const val POP = "pop"
   const val ROCK = "rock"
   const val CLASSICAL = "classical"
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

