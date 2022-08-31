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
            install(HttpTimeout) { requestTimeoutMillis = 10000 }
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

        lateinit var plattforms: List<ConnectionPoint>

        suspend fun load() {
            val url = BASE_URL + "connectionPoints"
            val client = ApiClient.client

            val response: HttpResponse = client.get(url)
            println("Loading ConnectionPoints: ${response.status}")

            plattforms = response.body()

            println("In load connectionPoints")
        }

        fun getPlattform(cpId: Int): ConnectionPoint? {
            return plattforms.filter { it.id == cpId }[0]
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
            println("Loading InstalledContracts: ${response.status}")

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
) : TakData {
    init {
        mapped[id] = this
    }

    val domainName: String
        get() {
            val parts = namespace.split(":")
            val endOfDomainIx = parts.size - 1 - 2
            return parts.slice(2..endOfDomainIx).joinToString(":")
        }

    val contractName: String
        get() {
            val parts = namespace.split(":")
            val endOfDomainIx = parts.size - 1 - 2
            return parts[parts.size - 2]
        }
    val htmlString: String
        get() {
            return "<i>${this.domainName}</i><br>${this.contractName} v${this.major}"
        }

    override fun tableRowList(): List<String> =
        listOf<String>(this.id.toString(), this.domainName, this.contractName, this.major.toString())

    companion object {
        val mapped = mutableMapOf<Int, ServiceContract>()
        fun columnHeadingList(): List<String> = listOf("Id", "Tjänstedomän", "Tjänstekontrakt", "Major")
    }
}

/**
 * Represent the response from a call to TAK-api LogicalAddresss
 */
@Serializable
data class LogicalAddress(
    val id: Int,
    val logicalAddress: String,
    val description: String
) : TakData {
    init {
        mapped[id] = this
    }

    val htmlString: String
        get() {
            return "<i>${this.description}</i><br>${this.logicalAddress}"
        }

    override fun tableRowList(): List<String> =
        listOf<String>(this.id.toString(), this.logicalAddress, this.description)

    companion object {

        val mapped = mutableMapOf<Int, LogicalAddress>()

        suspend fun load(connectionPointId: Int): List<LogicalAddress> {
            val url = BASE_URL + "logicalAddresss?connectionPointId=$connectionPointId"
            val client = ApiClient.client

            val response: HttpResponse = client.get(url)
            println("Loading LogicalAddresses: ${response.status}")

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
) : TakData {
    init {
        mapped[id] = this
    }

    val htmlString: String
        get() {
            return "<i>${this.description}</i><br>${this.hsaId}"
        }

    override fun tableRowList(): List<String> = listOf<String>(this.id.toString(), this.hsaId, this.description)

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
