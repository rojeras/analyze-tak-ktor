package com.example.models

/**
 * Loads all tak information for a certain TAK based on its id
 *
 * Can be extended with a cache function.
 *
 * @param id
 * @return
 */
suspend fun loadTakInformation(id: Int): TakInfo {
    val takInfo = TakInfo(id)

    // Load contracts
    val installedContracts = InstalledContract.load(id)
    for (ic in installedContracts) {
        takInfo.addContract(
            ic.serviceContract.id,
            ic.serviceContract.namespace,
            ic.serviceContract.major
        )
    }

    // Load logical addresses

    // Load consumers

    // Load producers

    return takInfo
}

/**
 * Tak info. Store tak information for a certain TAK.
 *
 * @property id
 * @constructor Create empty Tak info
 */
data class TakInfo(
    val id: Int,
) {
    val contracts = mutableListOf<Contract>()

    fun addContract(id: Int, namespace: String, major: Int) {
        contracts.add(Contract(id, namespace, major))
    }
}

/**
 * Contract information.
 *
 * @property id
 * @property namespace
 * @property major
 * @constructor Create empty Contract
 */
data class Contract(
    val id: Int,
    val namespace: String,
    val major: Int,
)
