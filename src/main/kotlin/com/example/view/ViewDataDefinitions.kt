package com.example.view

import com.example.models.*

data class ViewData(
    val headings: List<String>,
    val content: List<List<String>>
)

// fun mkComponentViewData(components: List<ServiceComponent>): ViewData {
suspend fun mkComponentViewData(cpId: Int, componentType: ComponentType): ViewData {
    val takInfo = obtainTakInfo(cpId)

    val components = when (componentType) {
        ComponentType.CONSUMER -> takInfo.serviceConsumers
        ComponentType.PRODUCER -> takInfo.serviceProducers
    }

    val content = mutableListOf<List<String>>()

    for (component in components) {
        content.add(listOf<String>(component.id.toString(), component.hsaId, component.description))
    }
    return ViewData(listOf("Id", "HsaId", "Beskrivning"), content)
}

suspend fun mkLogicalAddressViewData(cpId: Int): ViewData {
    val takInfo = obtainTakInfo(cpId)

    val logicalAddresses = takInfo.logicalAddresses

    val content = mutableListOf<List<String>>()

    for (la in logicalAddresses) {
        content.add(listOf<String>(la.id.toString(), la.logicalAddress, la.description))
    }
    return ViewData(listOf("Id", "Logisk adress", "Beskrivning"), content)
}

suspend fun mkContractViewData(cpId: Int): ViewData {
    val takInfo = obtainTakInfo(cpId)

    val contracts = takInfo.contracts

    val content = mutableListOf<List<String>>()

    for (contract in contracts) {
        content.add(listOf<String>(contract.id.toString(), contract.namespace, contract.major.toString()))
    }
    return ViewData(listOf("Id", "Tjänstekontraktets namnrymd", "Major"), content)
}

suspend fun mkAuthorizationViewData(cpId: Int): ViewData {
    val takInfo = obtainTakInfo(cpId)

    val authorizations = takInfo.authorizations

    val content = mutableListOf<List<String>>()

    for (auth in authorizations) {
        content.add(
            listOf<String>(
                auth.id.toString(),
                ServiceComponent.mapped[auth.serviceComponentId]!!.description,
                ServiceContract.mapped[auth.serviceContractId]!!.namespace,
                LogicalAddress.mapped[auth.logicalAddressId]!!.description
            )
        )
    }
    return ViewData(listOf("Id", "Konsument-id", "Kontrakt-id", "Logisk adress-id"), content)
}
