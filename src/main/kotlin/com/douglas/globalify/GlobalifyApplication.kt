package com.douglas.globalify

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GlobalifyApplication

fun main(args: Array<String>) {
    runApplication<GlobalifyApplication>(*args)
}
