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

/**
 * This file serve to access and fetch data from the TAK-api. The classes match
 * the data as it is returned from the API.
 * No application specific information should be included in these classes. Such should
 * be put in TakInfo.kt.
 * A new information source (for example direct access to the TAK-database can be
 * implemented in parallell to the logic in this file.
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
            install(HttpTimeout) { requestTimeoutMillis = 60000 }
            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        isLenient = true
                        explicitNulls = false
                        ignoreUnknownKeys = true
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
    fun getPlattformName(): String = "${this.platform}-${this.environment}"

    companion object {

        suspend fun load(): List<ConnectionPoint> {
            val url = BASE_URL + "connectionPoints"
            val client = ApiClient.client

            val response: HttpResponse = client.get(url)
            println("Loading ConnectionPoints: ${response.status}")

            return response.body()
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
            println("Loaded InstalledContracts: ${response.status}")

            return response.body()
        }
    }
}

/**
 * Represent the ServiceContract (part of InstalledContract) in the TAK-api
 */
@Serializable
data class ServiceContract(
    val id: Int,
    val name: String? = null,
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
    init {
        mapped[id] = this
    }

    val htmlString: String
        get() {
            return "<i>${this.description}</i><br>${this.logicalAddress}"
        }

    // override fun tableRowList(): List<String> =
    //    listOf<String>(this.id.toString(), this.logicalAddress, this.description)

    companion object {

        val mapped = mutableMapOf<Int, LogicalAddress>()

        suspend fun load(connectionPointId: Int): List<LogicalAddress> {
            println("Will load logical addresses")
            val url = BASE_URL + "logicalAddresss?connectionPointId=$connectionPointId"
            val client = ApiClient.client

            val response: HttpResponse = client.get(url)
            println("Loaded LogicalAddresses: ${response.status}")

            return response.body()
        }

        fun columnHeadingList(): List<String> = listOf("Id", "Logisk adress", "Beskrivning")
    }
}

/**
 * A service component can be of one of two types.
 */
enum class ComponentType(val label: String) {
    CONSUMER("consumer"),
    PRODUCER("producer")
}

/**
 * Represent the response from a call to TAK-api ServiceConsumer or ServiceProducer
 */
@Serializable
data class ServiceComponent(
    val id: Int,
    val hsaId: String,
    val description: String = "-"
) {
    init {
        mapped[id] = this
    }

    val htmlString: String
        get() {
            return "<i>${this.description}</i><br>${this.hsaId}"
        }

    // override fun tableRowList(): List<String> = listOf<String>(this.id.toString(), this.hsaId, this.description)

    companion object {

        val mapped = mutableMapOf<Int, ServiceComponent>()
        val listed: List<ServiceComponent> = mapped.values.toList()

        suspend fun load(componentType: ComponentType, connectionPointId: Int): List<ServiceComponent> {
            val resource: String = when (componentType) {
                ComponentType.CONSUMER -> "serviceConsumers"
                ComponentType.PRODUCER -> "serviceProducers"
            }

            val client = ApiClient.client

            val url = "$BASE_URL$resource?connectionPointId=$connectionPointId"

            val response: HttpResponse = client.get(url)
            println("Loading ServiceComponents: ${response.status}")

            return response.body()
        }

        fun columnHeadingList(): List<String> = listOf("Id", "HsaId", "Beskrivning")
    }
}

/**
 * Represent the response from a call to TAK-api Cooperation (anropsbehörighet)
 */
@Serializable
data class Cooperation(
    val id: Int,
    val serviceConsumer: ServiceComponent,
    val logicalAddress: LogicalAddress,
    val serviceContract: ServiceContract
) {
    init {
        mapped[id] = this
    }

    companion object {

        val mapped = mutableMapOf<Int, Cooperation>()

        suspend fun load(connectionPointId: Int): List<Cooperation> {
            val url =
                BASE_URL + "cooperations?connectionPointId=$connectionPointId&include=logicalAddress%2CserviceContract%2CserviceConsumer"
            val client = ApiClient.client

            val response: HttpResponse = client.get(url) {
                timeout {
                    requestTimeoutMillis = 30000
                }
            }
            println("Loading Cooperations: ${response.status}")

            return response.body()
        }
    }
}

/**
 * Authorization
 * Represent a consumer authorization.
 *
 * @property id
 * @property serviceComponentId
 * @property logicalAddressId
 * @property serviceContractId
 * @constructor Create empty Authorization
 */

@Serializable
data class ServiceProduction(
    val id: Int,
    val rivtaProfile: String,
    val serviceProducer: ServiceComponent,
    val logicalAddress: LogicalAddress,
    val serviceContract: ServiceContract
) {

    companion object {
        suspend fun load(connectionPointId: Int): List<ServiceProduction> {
            val url =
                BASE_URL + "serviceProductions?connectionPointId=$connectionPointId&include=logicalAddress%2CserviceContract%2CserviceProducer"
            val client = ApiClient.client

            val response: HttpResponse = client.get(url) {
                timeout {
                    requestTimeoutMillis = 30000
                }
            }
            println("Loading ServiceProductions: ${response.status}")

            return response.body()
        }
    }
}

data class TabulatorRowSpecification(
    val title: String,
    val field: String,
    val sorter: String
)
