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
    var logicalAddresses: List<LogicalAddress> = listOf()

    // var serviceConsumers: List<ServiceConsumer> = listOf()
    var serviceConsumers: List<ServiceComponent> = listOf<ServiceComponent>()

    // var serviceProducers: List<ServiceProducer> = listOf()
    var serviceProducers: List<ServiceComponent> = listOf()

    val authorizations = mutableListOf<Authorization>()

    val routings = mutableListOf<Routing>()

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

        println("Klar")

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

        println("Klar")
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
    val id: Int,
    val namespace: String,
    val major: Int
)
