package com.example.models

/**
 * Copyright (C) 2020 Lars Erik Röjerås
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private const val BASE_URL = "http://api.ntjp.se/coop/api/v1/"

object ApiClient {
    val client: HttpClient

    init {
        println("Client is being initialized")
        client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        isLenient = true
                    }
                )
            }
        }
    }
}

/**
 * Holds the cached answer from TAK-api. Will be populated as part of the deserialization in [takApiLoad].
 *
 * @param answer A list of [InstalledContracts]
 * @param lastChangeTime Time when the server cache was last updated.
 */
@Serializable
data class InstalledContract(
    val id: Int,
    val connectionPoint: Plattform,
    val serviceContract: TakServiceContract,
)


/**
 * Kotlin class representing deserialized JSON data from TAK-api ConnectionPoint endpoint. Represent a platform.
 *
 * @param id API id.
 * @param platform Owner of the platform ("NTJP", "SLL"...)
 * @param environment Type of platform ("PROD", "QA", "TEST")
 * @param snapshotTime The last time this information was updated in the TAK-api.
 */

// var plattforms: List<Plattform> = mutableListOf()

@Serializable
data class Plattform(
    val id: Int,
    val platform: String,
    val environment: String,
    val snapshotTime: String,
) {
    init {
        println("init called for id = ${id}")
    }

    companion object {

        lateinit var plattforms: List<Plattform>

        suspend fun load() {
            val url = BASE_URL + "connectionPoints.json"

            val client = ApiClient.client

            val response: HttpResponse = client.get(url)
            println(response.status)

            plattforms = response.body()

            println("In load")
        }
    }
}

/**
 * Kotlin class representing deserialized JSON data from TAK-api. Tak service contract.
 *
 * @property id API id.
 * @property name Contract name.
 * @property namespace Contract namespace.
 * @property major Major version.
 * @property minor Minor version.
 * @constructor Create empty Tak service contract
 */
@Serializable
data class TakServiceContract(
    val id: Int,
    val name: String = "",
    val namespace: String,
    val major: Int,
    val minor: Int,
) {
    init {
        // takInstalledContractNamespace.add(namespace)
        // println("Saving contract $namespace")
    }
}

/**
 * Tak api load
 *
 */
/*
fun takApiLoad() {
    val url = "${getBaseUrl()}/http://api.ntjp.se/coop/api/v1/installedContracts"

    getAsync(url) { response ->
        println("Size of TAK-api InstalledContracts are: ${response.length}")
        val json = Json { allowStructuredMapKeys = true }
        val takApiDto: TakApiDto = json.decodeFromString(TakApiDto.serializer(), response)
        console.log(takApiDto)
        console.log(takInstalledContractNamespace)
        RivManager.refresh()
    }
}

val takInstalledContractNamespace = mutableSetOf<String>()
*/
