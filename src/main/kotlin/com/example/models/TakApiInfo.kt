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
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private const val BASE_URL = "http://api.ntjp.se/coop/api/v1/"

/**
 * Api client. Object which own and store the client.
 *
 * @constructor Create empty Api client
 */
@OptIn(ExperimentalSerializationApi::class)
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
                        explicitNulls = false
                    }
                )
            }
        }
    }
}

/**
 * Kotlin class representing deserialized JSON data from TAK-api ConnectionPoint endpoint. Represent a platform.
 *
 * @param id API id.
 * @param platform Owner of the platform ("NTJP", "SLL"...)
 * @param environment Type of platform ("PROD", "QA", "TEST")
 * @param snapshotTime The last time this information was updated in the TAK-api.
 */
@Serializable
data class ConnectionPoint(
    val id: Int,
    val platform: String,
    val environment: String,
    val snapshotTime: String
) {
    companion object {

        lateinit var plattforms: List<ConnectionPoint>

        suspend fun load() {
            val url = BASE_URL + "connectionPoints"
            val client = ApiClient.client

            val response: HttpResponse = client.get(url)
            println(response.status)

            plattforms = response.body()

            println("In load connectionPoints")
        }
    }
}

/**
 * Represent the response from a call to TAK-api InstalledContracts
 */
@Serializable
data class InstalledContract(
    val id: Int,
    val connectionPoint: ConnectionPoint,
    val serviceContract: ServiceContract
) {
    companion object {
        suspend fun load(connectionPointId: Int): List<InstalledContract> {
            val url = BASE_URL + "installedContracts?connectionPointId=$connectionPointId"
            val client = ApiClient.client

            val response: HttpResponse = client.get(url)
            println(response.status)

            return response.body()
        }
    }
}

/**
 * Represent ServiceContract in the TAK-api (part of InstalledContract)
 *
 * @param answer A list of [InstalledContract]
 * @param lastChangeTime Time when the server cache was last updated.
 */
@Serializable
data class ServiceContract(
    val id: Int,
    val name: String?,
    val namespace: String,
    val major: Int,
    val minor: Int
)

/**
 * Represent the response from a call to TAK-api LogicalAddresss
 */
@Serializable
data class LogicalAddress(
    val id: Int,
    val logicalAddress: String,
    val description: String
) {
    companion object {
        suspend fun load(connectionPointId: Int): List<LogicalAddress> {
            val url = BASE_URL + "logicalAddresss?connectionPointId=$connectionPointId"
            val client = ApiClient.client

            val response: HttpResponse = client.get(url)
            println(response.status)

            return response.body()
        }
    }
}

/**
* Represent the response from a call to TAK-api ServiceConsumers
*/
@Serializable
data class ServiceConsumer(
    val id: Int,
    val hsaId: String,
    val description: String
) {
    companion object {
        suspend fun load(connectionPointId: Int): List<ServiceConsumer> {
            val url = BASE_URL + "serviceConsumers?connectionPointId=$connectionPointId"
            val client = ApiClient.client

            val response: HttpResponse = client.get(url)
            println(response.status)

            return response.body()
        }
    }
}

/**
 * Represent the response from a call to TAK-api ServiceProducers
 */
@Serializable
data class ServiceProducer(
    val id: Int,
    val hsaId: String,
    val description: String
) {
    companion object {
        suspend fun load(connectionPointId: Int): List<ServiceProducer> {
            val url = BASE_URL + "serviceProducers?connectionPointId=$connectionPointId"
            val client = ApiClient.client

            val response: HttpResponse = client.get(url)
            println(response.status)

            return response.body()
        }
    }
}