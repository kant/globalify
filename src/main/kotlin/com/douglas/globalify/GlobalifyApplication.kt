package com.douglas.globalify

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GlobalifyApplication

/**
 * Standard main class that runs application as a SpringBootApplication
 */
fun main(args: Array<String>) {
    runApplication<GlobalifyApplication>(*args)
}
