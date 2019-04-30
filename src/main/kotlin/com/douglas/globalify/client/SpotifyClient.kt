package com.douglas.globalify.client

import feign.Param
import feign.RequestLine
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@FeignClient(name = "spotify", url = "http://api.openweathermap.org/data/2.5")
interface SpotifyClient {

    @RequestMapping(method = [RequestMethod.GET], value = "/weather?appid=a5b5cbc0da6ddfb96bd03be9872d8405&units=metric&q={location}")
    fun getTemperature(@Param("location") location: String): ResponseEntity<String>
}