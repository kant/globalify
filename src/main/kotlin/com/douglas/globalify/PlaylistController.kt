package com.douglas.globalify

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.logging.Level
import java.util.logging.Logger

@RestController()
class PlaylistController {
    val logger = Logger.getLogger(PlaylistController::class.simpleName)

    @Autowired
    lateinit var playlistService: PlaylistService

    @RequestMapping("/globalify")
    fun globalify(@RequestParam(value = "location") location: String?): ResposeDTO {
        try {
            if (location.isNullOrEmpty()) return error("Please choose type a valid location")
            return playlistService.getPlaylistForCity(location!!)
        } catch (e: ServiceException) {
            return error(e.message!!)
        } catch (e: Exception) {
            logger.log(Level.SEVERE, "Internal server error!", e)
            return error("Internal server error, please contact page admin.")
        }
    }

    fun error(message: String) = ResposeDTO(false, message)
}

@JsonInclude(JsonInclude.Include.NON_NULL)
open class ResposeDTO(val success: Boolean, val message: String?)

data class PlaylistDTO(val location: String, val temperature: Float, val genre: String,
                       val spotify: SpotifyPlaylistResponse)
    : ResposeDTO(true, null)

