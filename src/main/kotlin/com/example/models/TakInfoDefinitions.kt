package com.example.models

import kotlinx.serialization.Serializable
import java.util.NoSuchElementException

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
) : ViewDataSuper() {

    init {
        mapped[namespace] = this

        val parts = namespace.split(":")
        val endOfDomainIx = parts.size - 1 - 2
        parts.slice(2..endOfDomainIx).joinToString(":").also { this.domainName = it }

        val parts2 = namespace.split(":")
        val endOfDomainIx2 = parts.size - 1 - 2
        parts2[parts.size - 2].also { this.contractName = it }
    }

    var domainName: String
    var contractName: String

    companion object {
        val mapped = mutableMapOf<String, Contract>()

        val tabulatorRowSpecifications: List<TabulatorRowSpecification> = listOf(
            TabulatorRowSpecification("Domain", "domainName", "string"),
            TabulatorRowSpecification("Kontrakt", "contractName", "string"),
            TabulatorRowSpecification("Major", "major", "string")
        )
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
) : ViewDataSuper() {
    init {
        mapped[hsaId] = this
    }

    companion object {
        val mapped = mutableMapOf<String, Component>()

        val tabulatorRowSpecifications: List<TabulatorRowSpecification> = listOf(
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
) : ViewDataSuper() {
    init {
        mapped[logicalAddress] = this
    }

    companion object {
        val mapped = mutableMapOf<String, LogAdr>()

        val tabulatorRowSpecifications: List<TabulatorRowSpecification> = listOf(
            TabulatorRowSpecification("Logisk adress", "logicalAddress", "string"),
            TabulatorRowSpecification("Beskrivning", "description", "string")
        )
    }
}

@Serializable
data class Authorization(
    val idInSource: Int,
    val serviceComponentId: Int,
    val logicalAddressId: Int,
    val serviceContractId: Int
) : ViewDataSuper() {
    init {
        mapped[idInSource] = this

        // Verify that the logical address is part of loaded logical addresses
        LogicalAddress.mapped.values.find { it.id == this.logicalAddressId }
            ?: throw NoSuchElementException("Cooperation with id $idInSource refer to a non-existing logical address id $logicalAddressId")

        // Verify that the contract is part of loaded contracts
        Contract.mapped.values.find { it.idInSource == this.serviceContractId }
            ?: throw NoSuchElementException("Cooperation with id $idInSource refer to a non-existing contract id $serviceContractId")
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
    val idInSource: Int,
    val serviceComponentId: Int,
    val logicalAddressId: Int,
    val serviceContractId: Int,
    val rivtaProfile: String
) : ViewDataSuper() {
    init {
        mapped[idInSource] = this

        // Verify that the logical address is part of loaded logical addresses
        LogicalAddress.mapped.values.find { it.id == this.logicalAddressId }
            ?: throw NoSuchElementException("Routing with id $idInSource refer to a non-existing logical address id $logicalAddressId")

        // Verify that the contract is part of loaded contracts
        Contract.mapped.values.find { it.idInSource == this.serviceContractId }
            ?: throw NoSuchElementException("Routing with id $idInSource refer to a non-existing contract id $serviceContractId")
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
