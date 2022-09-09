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
 * The classes and functions in this file manage TAK information, but is
 * independent of the source. The source could be for example the TAK-api,
 * direct access to a TAK-database och reading the JSON export files.
 */

import com.example.controller.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.serialization.Serializable
import java.util.NoSuchElementException

enum class PLATFORM_ID {
    `NTJP-PROD`, // ktlint-disable enum-entry-name-case
    `NTJP-QA`, // ktlint-disable enum-entry-name-case
    `NTJP-TEST`, // ktlint-disable enum-entry-name-case
    `SLL-PROD`, // ktlint-disable enum-entry-name-case
    `SLL-QA` // ktlint-disable enum-entry-name-case
}

enum class ViewDataTypes(val typeName: String) {
    CONTRACTS("contracts"),
    CONSUMERS("consumers"),
    PRODUCERS("producers"),
    LOGICAL_ADDRESS("logicaladdress"),
    AUTHORIZATION("authorizations"),
    ROUTING("routings"),
    TKNOTPARTOFAUTHORIZATION("tkNotPartOfAuthorization"),
    TKNOTPARTOFROUTING("tkNotPartOfRouting"),
    LANOTPARTOFROUTING("laNotPartOfRouting"),
    AUTHORIZATIONWITHOUTAMATCHINGROUTING("authorizationWithoutAMatchingRouting");

    companion object {
        fun getByName(aName: String) = ViewDataTypes.values().firstOrNull { it.typeName == aName }
    }
}

fun platformName2platformId(name: String): PLATFORM_ID = PLATFORM_ID.valueOf(name)
fun platformId2platformName(pId: PLATFORM_ID): String = pId.name

interface TakData {
    // fun tableRowList(): List<String>

    companion object {
        // fun columnHeadingList(): List<String> = listOf()
    }
}

suspend fun loadAllPlatforms() {
    val connectionPoints = ConnectionPoint.load()

    for (cp in connectionPoints) {
        val platformId: PLATFORM_ID
        try {
            platformId = PLATFORM_ID.valueOf("${cp.platform}-${cp.environment}")
        } catch (e: IllegalArgumentException) {
            println("ERROR: INVALID PLATFORM_ID value $e")
            return
        }

        val pl = Platform(cp.id, platformId)
        println(pl.platformName)
    }
}

/**
 * Tak info. Store tak information for one certain TAK.
 *
 * @property id
 * @constructor Create empty Tak info
 */
data class TakInfo(
    val platformId: PLATFORM_ID
) {
    init {
        // Component.tabulatorRowSpecifications.also { this.tabulatorRowSpecification[ViewDataTypes.CONSUMERS] = it }
        tabulatorRowSpecification[ViewDataTypes.CONSUMERS] = Component.tabulatorRowSpecifications
    }

    val connectionPointId = Platform.mapped[platformId]!!.idInSource

    // Define lists for the TAK information
    var contracts = mutableListOf<Contract>()
    var logicalAddresses = mutableListOf<LogAdr>()
    var serviceConsumers = mutableListOf<Component>()
    var serviceProducers = mutableListOf<Component>()
    val authorizations = mutableListOf<Authorization>()
    val routings = mutableListOf<Routing>()

    // val integrations = mutableListOf<Integration>()

    // Define the lists for test results for this TAK
    var tkNotPartOfAuthorization: List<Contract> = listOf()
    var tkNotPartOfRouting: List<Contract> = listOf()
    var laNotPartOfRouting: List<LogAdr> = listOf()
    var authorizationWithoutAMatchingRouting: List<Authorization> = listOf()

    val platformName = Platform.mapped[platformId]!!.platformName


    suspend fun load() {
        println("Loading $platformName")
        // Contracts are created based on a subset of the information from InstalledContracts.
        val installedContracts = InstalledContract.load(connectionPointId)
        for (ic in installedContracts) {
            contracts.add(
                Contract(
                    ic.serviceContract.id,
                    ic.serviceContract.namespace,
                    ic.serviceContract.major,
                    ic.serviceContract.minor
                )
            )
        }
        // contracts = installedContracts.map { it.contract }.toMutableList()

        // Logical addresses from TAK-api are used as-is

        val laTakApi = LogicalAddress.load(connectionPointId)
        for (la in laTakApi) {
            logicalAddresses.add(
                LogAdr(la.id, la.logicalAddress, la.description)
            )
        }

        // Service consumers are used as-is
        // serviceConsumers = ServiceConsumer.load(cpId)
        val cons = ServiceComponent.load(ComponentType.CONSUMER, connectionPointId)
        for (con in cons) {
            serviceConsumers.add(
                Component(con.id, con.hsaId, con.description)
            )
        }

        // Service producers are used as-is
        // serviceProducers = ServiceProducer.load(cpId)
        val prods = ServiceComponent.load(ComponentType.PRODUCER, connectionPointId)
        for (prod in prods) {
            serviceProducers.add(
                Component(prod.id, prod.hsaId, prod.description)
            )
        }

        val cooperations = Cooperation.load(connectionPointId)
        for (coop in cooperations) {
            authorizations.add(
                Authorization(
                    coop.id,
                    coop.serviceConsumer.id,
                    coop.logicalAddress.id,
                    coop.serviceContract.id
                )
            )
        }

        val productions = ServiceProduction.load(connectionPointId)
        for (production in productions) {
            this.routings.add(
                Routing(
                    production.id,
                    production.serviceProducer.id,
                    production.logicalAddress.id,
                    production.serviceContract.id,
                    production.rivtaProfile
                )
            )
        }
        /*
                // Create the list of integrations by combining authorizations and routings
                for (auth in authorizations) {
                    for (rout in routings) {
                        if (rout.matchAuthorization(auth)) {
                            integrations.add(Integration(auth, rout))
                        }
                    }
                }

                println("Number of integrations: ${integrations.size}")
                println("TakInfo loading of tak #${this.cpId} complete")

                // Time to perform the TAK checks
                tkNotPartOfAuthorization = tkNotPartOfAuthorization(this.authorizations, this.contracts)

                tkNotPartOfRouting = tkNotPartOfRouting(this.routings, this.contracts)

                laNotPartOfRouting = laNotPartOfRouting(this.routings, this.logicalAddresses)

                authorizationWithoutAMatchingRouting = authorizationWithoutAMatchingRouting(authorizations, integrations)

                println("Number of authorizationWithoutAMatchingRouting = ${authorizationWithoutAMatchingRouting.size}")

                println("TakChecks created för tak #${this.cpId}")
         */
    }

    companion object {
        val takStore = mutableMapOf<PLATFORM_ID, TakInfo>()

        var tabulatorRowSpecification = mutableMapOf<ViewDataTypes, List<TabulatorRowSpecification>>()
    }
}

suspend fun obtainTakInfoBasedOnName(platformName: String): TakInfo {
    val platformId = platformName2platformId(platformName)
    return obtainTakInfoBasedOnId(platformId)
}

suspend fun obtainTakInfoBasedOnId(platformId: PLATFORM_ID): TakInfo {
    if (TakInfo.takStore.containsKey(platformId)) {
        return TakInfo.takStore[platformId]!! // todo: Get rid of !!
    }

    val takInfo = TakInfo(platformId)
    takInfo.load()
    TakInfo.takStore[platformId] = takInfo
    return takInfo
}

data class Platform(
    val idInSource: Int,
    val platformId: PLATFORM_ID
) {
    init {
        mapped[platformId] = this
    }

    val platformName: String = platformId.name

    companion object {
        val mapped = mutableMapOf<PLATFORM_ID, Platform>()
    }
}

/**
 * Represent a contract in the TAK
 *
 * @param answer A list of [InstalledContract]
 * @param lastChangeTime Time when the server cache was last updated.
 */

@Serializable
data class Contract(
    val idInSource: Int,
    val namespace: String,
    val major: Int,
    val minor: Int
) : TakData {
    init {
        mapped[namespace] = this
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

    // override fun tableRowList(): List<String> =
    //     listOf<String>(this.idInSource.toString(), this.domainName, this.contractName, this.major.toString())

    companion object {
        val mapped = mutableMapOf<String, Contract>()
        fun columnHeadingList(): List<String> = listOf("Id", "Tjänstedomän", "Tjänstekontrakt", "Major")
    }
}

/**
 * Represent a service component in a TAK
 */
@Serializable
data class Component(
    val idInSource: Int,
    val hsaId: String,
    val description: String = "-"
) : TakData {
    init {
        mapped[hsaId] = this
    }

    companion object {
        val mapped = mutableMapOf<String, Component>()

        val tabulatorRowSpecifications: List<TabulatorRowSpecification> = listOf(
            TabulatorRowSpecification("id", "id", "string"),
            TabulatorRowSpecification("HsaId", "hsaId", "string"),
            TabulatorRowSpecification("Beskrivning", "description", "string")
        )
    }
}

/**
 * Represent a LogicalAddresss in a TAK
 */
@Serializable
data class LogAdr(
    val idInSource: Int,
    val logicalAddress: String,
    val description: String
) : TakData {
    init {
        mapped[logicalAddress] = this
    }

    companion object {
        val mapped = mutableMapOf<String, LogAdr>()
    }
}

@Serializable
data class Authorization(
    val id: Int,
    val serviceComponentId: Int,
    val logicalAddressId: Int,
    val serviceContractId: Int
) : TakData {
    init {
        mapped[id] = this

        // Verify that the logical address is part of loaded logical addresses
        LogicalAddress.mapped.values.find { it.id == this.logicalAddressId }
            ?: throw NoSuchElementException("Cooperation with id $id refer to a non-existing logical address id $logicalAddressId")

        // Verify that the contract is part of loaded contracts
        Contract.mapped.values.find { it.idInSource == this.serviceContractId }
            ?: throw NoSuchElementException("Cooperation with id $id refer to a non-existing contract id $serviceContractId")
    }

    /*
    override fun tableRowList(): List<String> = listOf<String>(
        this.id.toString(),
        ServiceComponent.mapped[this.serviceComponentId]!!.htmlString,
        Contract.mapped[this.serviceContractId]!!.htmlString,
        LogicalAddress.mapped[this.logicalAddressId]!!.htmlString
    )
     */

    companion object {
        val mapped = mutableMapOf<Int, Authorization>()

        fun columnHeadingList(): List<String> = listOf("Id", "Konsument", "Kontrakt", "Logisk adress")
    }
}

@Serializable
data class Routing(
    val id: Int,
    val serviceComponentId: Int,
    val logicalAddressId: Int,
    val serviceContractId: Int,
    val rivtaProfile: String
) : TakData {
    init {
        mapped[id] = this

        // Verify that the logical address is part of loaded logical addresses
        LogicalAddress.mapped.values.find { it.id == this.logicalAddressId }
            ?: throw NoSuchElementException("Routing with id $id refer to a non-existing logical address id $logicalAddressId")

        // Verify that the contract is part of loaded contracts
        Contract.mapped.values.find { it.idInSource == this.serviceContractId }
            ?: throw NoSuchElementException("Routing with id $id refer to a non-existing contract id $serviceContractId")
    }

    fun matchAuthorization(auth: Authorization): Boolean {
        // If different contracts no match
        if (auth.serviceContractId != this.serviceContractId) return false

        // The existence of the logical address is verified as part of Authorization.init block -> !! is ok here
        val authLogicalAddress = LogicalAddress.mapped[auth.logicalAddressId]!!.logicalAddress

        // If standard authentication is used there is always a match
        if (
            (authLogicalAddress == "*") ||
            (authLogicalAddress == "SE") ||
            (auth.logicalAddressId == this.logicalAddressId)
        ) {
            return true
        }

        return false
    }

    /*
    override fun tableRowList(): List<String> = listOf<String>(
        this.id.toString(),
        ServiceComponent.mapped[this.serviceComponentId]!!.htmlString,
        Contract.mapped[this.serviceContractId]!!.htmlString,
        LogicalAddress.mapped[this.logicalAddressId]!!.htmlString
    )
     */

    companion object {
        val mapped = mutableMapOf<Int, Routing>()

        fun columnHeadingList(): List<String> = listOf("Id", "Producent", "Kontrakt", "Logisk adress")
    }
}
/*
data class Integration(
    val authorization: Authorization,
    val routing: Routing
) : TakData {
    fun tableRowList(): List<String> = listOf<String>(
        ServiceComponent.mapped[this.authorization.serviceComponentId]!!.htmlString,
        Contract.mapped[this.authorization.serviceContractId]!!.htmlString,
        ServiceComponent.mapped[this.routing.serviceComponentId]!!.htmlString,
        LogicalAddress.mapped[this.authorization.logicalAddressId]!!.htmlString
    )

    companion object {
        // val mapped = mutableMapOf<Int, Integration>()

        fun columnHeadingList(): List<String> =
            listOf("Konsument", "Tjänstekontrakt", "Tjänsteproducent", "Logisk adress")
    }
}

 */
