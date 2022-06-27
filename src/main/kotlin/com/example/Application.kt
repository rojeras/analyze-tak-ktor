package com.example

import com.example.models.ApiClient
import com.example.models.Plattform
import com.example.plugins.*
import io.ktor.server.application.*

suspend fun main(args: Array<String>) {

    /**
     * Defined as a global in TakApiInfo
     */

    val url = "http://api.ntjp.se/coop/api/v1/connectionPoints.json"

    val client = ApiClient.client

    Plattform.load()

    for (cp in Plattform.plattforms) {
        val id = cp.id
        println("$id ${cp.platform}-${cp.environment}")
    }

    io.ktor.server.netty.EngineMain.main(args)
}

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    configureRouting()
    configureTemplating()
}
