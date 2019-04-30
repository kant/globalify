package com.douglas.globalify.data

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
open class ResposeDTO(val success: Boolean, val message: String?)

data class PlaylistDTO(val location: String, val temperature: Float, val genre: String,
                       val spotify: SpotifyPlaylistResponse)
    : ResposeDTO(true, null)

class ServiceException(message: String?) : Exception(message)