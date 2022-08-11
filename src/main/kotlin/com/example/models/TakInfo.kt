package com.example.models

/**
 * Tak info. Store tak information for a certain TAK.
 *
 * @property id
 * @constructor Create empty Tak info
 */
data class TakInfo(
    val cpId: Int
) {
    val contracts = mutableListOf<Contract>()
    var logicalAddress: List<LogicalAddress> = listOf()

    suspend fun load() {
        // Contracts are created based on a subset of the information from InstalledContracts.
        val installedContracts = InstalledContract.load(cpId)
        for (ic in installedContracts) {
            contracts.add(
                Contract(
                    ic.serviceContract.id,
                    ic.serviceContract.namespace,
                    ic.serviceContract.major
                )
            )
        }

        // Logical addresses from TAK-api are used as-is
        logicalAddress = LogicalAddress.load(cpId)
    }
}

suspend fun obtainTakInfo(cpId: Int): TakInfo {
    val takInfo = TakInfo(cpId)
    takInfo.load()
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
    val id: Int,
    val namespace: String,
    val major: Int
)
