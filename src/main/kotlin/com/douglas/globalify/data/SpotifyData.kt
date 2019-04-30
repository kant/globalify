package com.douglas.globalify.data

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

object LogMessage {
    const val LOCATION_NOT_FOUND = "Location not found"
    const val SPOTIFY_COMM_ERROR = "Could not communicate with Spotify"
    const val SPOTIFY_AUTH_ERROR = "Spotify authentication does not exist or expired"
    const val SPOTIFY_TRACKS_NOT_FOUND = "Could not find tracks for playlist"
    const val SPOTIFY_PLAYLIST_NOT_FOUND = "Could not find playlist for genre"
}

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

const val MINUTES_OFFSET = 60