package com.douglas.globalify

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
class GlobalifyApplication

/**
 * Standard main class that runs application as a SpringBootApplication
 */
fun main(args: Array<String>) {
    runApplication<GlobalifyApplication>(*args)
}
