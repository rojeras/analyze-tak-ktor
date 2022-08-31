package com.example.models

import com.example.controller.authorizationWithoutAMatchingRouting
import com.example.controller.laNotPartOfRouting
import com.example.controller.tkNotPartOfAuthorization
import com.example.controller.tkNotPartOfRouting
import kotlinx.serialization.Serializable
import java.util.NoSuchElementException

interface TakData {
    fun tableRowList(): List<String>

    companion object {
        fun columnHeadingList(): List<String> = listOf()
    }
}

/**
 * Tak info. Store tak information for a certain TAK.
 *
 * @property id
 * @constructor Create empty Tak info
 */
data class TakInfo(
    val cpId: Int
) {
    // Define lists for the TAK information
    val contracts = mutableListOf<Contract>()
    var logicalAddresses: List<LogicalAddress> = listOf()
    var serviceConsumers: List<ServiceComponent> = listOf<ServiceComponent>()
    var serviceProducers: List<ServiceComponent> = listOf()
    val authorizations = mutableListOf<Authorization>()
    val routings = mutableListOf<Routing>()

    // Define the lists for test results for this TAK
    var tkNotPartOfAuthorization: List<Contract> = listOf()
    var tkNotPartOfRouting: List<Contract> = listOf()
    var laNotPartOfRouting: List<LogicalAddress> = listOf()
    var authorizationWithoutAMatchingRouting: List<Authorization> = listOf()

    fun getPlattformName() = ConnectionPoint.getPlattform(cpId)!!.getPlattformName()

    suspend fun load() {
        println("Loading TAK with id #${this.cpId}")
        // Contracts are created based on a subset of the information from InstalledContracts.
        val installedContracts = InstalledContract.load(cpId)
        for (ic in installedContracts) {
            contracts.add(
                Contract(
                    this,
                    ic.serviceContract.id,
                    ic.serviceContract.namespace,
                    ic.serviceContract.major
                )
            )
        }

        // Logical addresses from TAK-api are used as-is
        logicalAddresses = LogicalAddress.load(cpId)

        // Service consumers are used as-is
        // serviceConsumers = ServiceConsumer.load(cpId)
        serviceConsumers = ServiceComponent.load(ComponentType.CONSUMER, cpId)

        // Service producers are used as-is
        // serviceProducers = ServiceProducer.load(cpId)
        serviceProducers = ServiceComponent.load(ComponentType.PRODUCER, cpId)

        val cooperations = Cooperation.load(cpId)
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

        val productions = ServiceProduction.load(cpId)
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

        println("TakInfo loading of tak #${this.cpId} complete")

        // Time to perform the TAK checks
        tkNotPartOfAuthorization = tkNotPartOfAuthorization(this.authorizations, this.contracts)

        tkNotPartOfRouting = tkNotPartOfRouting(this.routings, this.contracts)

        laNotPartOfRouting = laNotPartOfRouting(this.routings, this.logicalAddresses)

        authorizationWithoutAMatchingRouting = authorizationWithoutAMatchingRouting(this.authorizations, this.routings)

        println("Number of authorizationWithoutAMatchingRouting = ${authorizationWithoutAMatchingRouting.size}")
        println("TakChecks created för tak #${this.cpId}")
    }

    companion object {
        val takStore = mutableMapOf<Int, TakInfo>()
    }
}

suspend fun obtainTakInfo(cpId: Int): TakInfo {
    if (TakInfo.takStore.containsKey(cpId)) {
        return TakInfo.takStore[cpId]!! // todo: Get rid of !!
    }

    val takInfo = TakInfo(cpId)
    takInfo.load()
    TakInfo.takStore[cpId] = takInfo
    return takInfo
}

/**
 * Contract information.
 * These objects are created based on information from tha TAK-api.
 *
 * @property id
 * @property namespace
 * @property major
 * @constructor Create empty Contract
 */

data class Contract(
    val takInfo: TakInfo,
    val id: Int,
    val namespace: String,
    val major: Int
) : TakData {

    val htmlString: String
        get() {
            return this.namespace
        }

    override fun tableRowList(): List<String> =
        listOf<String>(this.id.toString(), this.namespace, this.major.toString())

    companion object {
        fun columnHeadingList(): List<String> = listOf("Id", "Tjänstekontraktets namnrymd", "Major")
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
        ServiceContract.mapped.values.find { it.id == this.serviceContractId }
            ?: throw NoSuchElementException("Cooperation with id $id refer to a non-existing contract id $serviceContractId")
    }

    override fun tableRowList(): List<String> = listOf<String>(
        this.id.toString(),
        ServiceComponent.mapped[this.serviceComponentId]!!.description,
        ServiceContract.mapped[this.serviceContractId]!!.namespace,
        LogicalAddress.mapped[this.logicalAddressId]!!.description
    )

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
        ServiceContract.mapped.values.find { it.id == this.serviceContractId }
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

    override fun tableRowList(): List<String> = listOf<String>(
        this.id.toString(),
        ServiceComponent.mapped[this.serviceComponentId]!!.description,
        ServiceContract.mapped[this.serviceContractId]!!.namespace,
        LogicalAddress.mapped[this.logicalAddressId]!!.logicalAddress
    )

    companion object {
        val mapped = mutableMapOf<Int, Routing>()

        fun columnHeadingList(): List<String> = listOf("Id", "Producent", "Kontrakt", "Logisk adress")
    }
}
/*
data class Integration(
    val id: Int,
    val authorization: Authorization,
    val routing: Routing
) : TakData {
    override fun tableRowList(): List<String> = listOf<String>(
        this.id.toString(),
        ServiceComponent.mapped[this.authorization.serviceComponentId]!!.htmlString,
        ServiceContract.mapped[this.authorization.serviceContractId]!!.html,
        LogicalAddress.mapped[this.logicalAddressId]!!.logicalAddress
    )

    companion object {
        val mapped = mutableMapOf<Int, Integration>()

        fun columnHeadingList(): List<String> =
            listOf("Konsument", "Tjänstekontrakt", "Tjänsteproducent", "Logisk adress")
    }
}
*/
