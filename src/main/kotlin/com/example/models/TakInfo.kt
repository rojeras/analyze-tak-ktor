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

enum class PLATFORM_ID {
    `NTJP-PROD`, // ktlint-disable enum-entry-name-case
    `NTJP-QA`, // ktlint-disable enum-entry-name-case
    `NTJP-TEST`, // ktlint-disable enum-entry-name-case
    `SLL-PROD`, // ktlint-disable enum-entry-name-case
    `SLL-QA` // ktlint-disable enum-entry-name-case
}

enum class ViewDataType(val typeName: String) {
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
        fun getByName(aName: String) = ViewDataType.values().firstOrNull { it.typeName == aName }
    }
}

data class ViewContent(
    val heading: String,
    val tabulatorRowSpecification: List<TabulatorRowSpecification>,
    val ajaxUrl: String
)

@Serializable
abstract class ViewDataSuper()

fun platformName2platformId(name: String): PLATFORM_ID = PLATFORM_ID.valueOf(name)
fun platformId2platformName(pId: PLATFORM_ID): String = pId.name

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
    val connectionPointId = Platform.mapped[platformId]!!.idInSource

    // Define lists for the TAK information
    val viewData = mutableMapOf<ViewDataType, List<ViewDataSuper>>()

    // val authorizations = mutableListOf<Authorization>()
    // val routings = mutableListOf<Routing>()
    // val integrations = mutableListOf<Integration>()

    // Define the lists for test results for this TAK
    var tkNotPartOfAuthorization: List<Contract> = listOf()
    var tkNotPartOfRouting: List<Contract> = listOf()
    var laNotPartOfRouting: List<LogAdr> = listOf()
    var authorizationWithoutAMatchingRouting: List<Authorization> = listOf()

    val platformName = Platform.mapped[platformId]!!.platformName

    fun viewDefinition(viewDataType: ViewDataType): ViewContent? {
        return when (viewDataType) {
            ViewDataType.CONSUMERS ->
                ViewContent(
                    "Tjänstekonsumenter in ${this.platformName}",
                    Component.tabulatorRowSpecifications,
                    "/api/tak/$platformName/${viewDataType.typeName}"
                )

            ViewDataType.PRODUCERS ->
                ViewContent(
                    "Tjänsteproducenter i ${this.platformName}",
                    Component.tabulatorRowSpecifications,
                    "/api/tak/$platformName/${viewDataType.typeName}"
                )

            ViewDataType.CONTRACTS ->
                ViewContent(
                    "Tjänstekontrakt i ${this.platformName}",
                    Contract.tabulatorRowSpecifications,
                    "/api/tak/$platformName/${viewDataType.typeName}"
                )

            ViewDataType.LOGICAL_ADDRESS ->
                ViewContent(
                    "Logiska adresser i ${this.platformName}",
                    LogAdr.tabulatorRowSpecifications,
                    "/api/tak/$platformName/${viewDataType.typeName}"
                )

            else -> {
                println("ERROR in viewDefinition, viewDataType is $viewDataType")
                null
            }
        }
    }

    suspend fun load() {
        println("Loading $platformName")
        // Contracts are created based on a subset of the information from InstalledContracts.
        val installedContracts = InstalledContract.load(connectionPointId)
        val contracts = mutableListOf<Contract>()
        for (ic in installedContracts) {
            // Filter out contracts with illegal namespace
            if (ic.serviceContract.namespace.filter { it == ':' }.count() < 5) continue

            contracts.add(
                Contract(
                    ic.serviceContract.id,
                    ic.serviceContract.namespace,
                    ic.serviceContract.major,
                    ic.serviceContract.minor
                )
            )
        }

        viewData[ViewDataType.CONTRACTS] = contracts

        val laTakApi = LogicalAddress.load(connectionPointId)
        val logAdresses = mutableListOf<LogAdr>()
        for (la in laTakApi) {
            logAdresses.add(
                LogAdr(la.id, la.logicalAddress, la.description)
            )
        }
        viewData[ViewDataType.LOGICAL_ADDRESS] = logAdresses

        val cons = ServiceComponent.load(ComponentType.CONSUMER, connectionPointId)
        val consumers = mutableListOf<Component>()
        for (con in cons) {
            consumers.add(
                Component(con.id, con.hsaId, con.description)
            )
        }
        viewData[ViewDataType.CONSUMERS] = consumers

        val prods = ServiceComponent.load(ComponentType.PRODUCER, connectionPointId)
        val producers = mutableListOf<Component>()
        for (prod in prods) {
            producers.add(
                Component(prod.id, prod.hsaId, prod.description)
            )
        }
        viewData[ViewDataType.PRODUCERS] = producers

        val cooperations = Cooperation.load(connectionPointId)
        val authorizations = mutableListOf<Authorization>()
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
        viewData[ViewDataType.AUTHORIZATION] = authorizations

        val productions = ServiceProduction.load(connectionPointId)
        val routings = mutableListOf<Routing>()
        for (production in productions) {
            routings.add(
                Routing(
                    production.id,
                    production.serviceProducer.id,
                    production.logicalAddress.id,
                    production.serviceContract.id,
                    production.rivtaProfile
                )
            )
        }
        viewData[ViewDataType.ROUTING] = routings
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
