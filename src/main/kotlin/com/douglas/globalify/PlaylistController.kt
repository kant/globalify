package com.douglas.globalify

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.logging.Level
import java.util.logging.Logger

const val COORD_SEP = ","

@RestController()
class PlaylistController {

    val logger = Logger.getLogger(PlaylistController::class.simpleName)!!

    @Autowired
    lateinit var playlistService: PlaylistService

    @RequestMapping("/getPlaylist")
    fun getPlaylist(@RequestParam(value = "location") location: String?,
                  @RequestParam(value = "coord") coord: String?): ResposeDTO {
        return try {
            when {
                !location.isNullOrEmpty() -> playlistService.getPlaylistForLocation(location!!)
                isCoord(coord) -> playlistService.getPlaylistForLocation(parseCoord(coord!!))
                else -> error("Please type a valid city or coordinates")
            }
        } catch (e: ServiceException) {
            error(e.message!!)
        } catch (e: Exception) {
            logger.log(Level.SEVERE, "Internal server error!", e)
            error("Internal server error, please contact page admin.")
        }
    }

    private fun isCoord(coord: String?): Boolean {
        if (coord.isNullOrEmpty()) return false
        val split = coord!!.split(COORD_SEP)
        if (split.size != 2 || split[0].toDoubleOrNull() == null
                || split[1].toDoubleOrNull() == null) return false

        return true
    }

    private fun parseCoord(coord: String): Pair<Double, Double> {
        val split = coord.split(COORD_SEP)
        return Pair(split[0].toDouble(), split[1].toDouble())
    }

    fun error(message: String) = ResposeDTO(false, message)
}