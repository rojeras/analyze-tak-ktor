package com.example.view

import com.example.models.*
import com.example.plugins.TakViewResurces
import io.ktor.server.freemarker.*
import kotlin.system.exitProcess

data class ViewData(
    val headings: List<String>,
    val content: List<List<String>>,
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

suspend fun mkContractView(id: Int, contractResource: TakViewResurces = TakViewResurces.CONTRACTS): FreeMarkerContent {
    val plattformName = ConnectionPoint.getPlattform(id)!!.getPlattformName()

    // val contractViewData = mkContractViewData(id, contractResource)
    val takInfo = obtainTakInfo(id)

    // val contracts = takInfo.contracts
    val contracts: List<Contract>
    val heading: String

    when (contractResource) {
        TakViewResurces.CONTRACTS -> {
            contracts = takInfo.contracts
            heading = "Tjänstekontrakt i $plattformName"
        }

        TakViewResurces.TKNOTPARTOFAUTHORIZATION -> {
            contracts = takInfo.tkNotPartOfAuthorization
            heading = "Tjänstekontrakt som inte ingår i någon anropsbehörighet i $plattformName"
        }

        else -> {
            println("ERROR: mkContractViewData() called with unknown contractResource: $contractResource")
            exitProcess(1)
        }
    }

    val content = mutableListOf<List<String>>()

    for (contract in contracts) {
        content.add(listOf<String>(contract.id.toString(), contract.namespace, contract.major.toString()))
    }
    val contractViewData = ViewData(listOf("Id", "Tjänstekontraktets namnrymd", "Major"), content)

    return io.ktor.server.freemarker.FreeMarkerContent(
        "tableview.ftl",
        kotlin.collections.mapOf(
            "heading" to heading,
            "tableHeadings" to contractViewData.headings,
            "viewData" to contractViewData.content
        )
    )
}
/*
suspend fun mkContractViewData(cpId: Int, contractResource: TakViewResurces): ViewData {
    val takInfo = obtainTakInfo(cpId)

    // val contracts = takInfo.contracts
    val contracts = when (contractResource) {
        TakViewResurces.CONTRACTS -> takInfo.contracts
        TakViewResurces.TKNOTPARTOFAUTHORIZATION -> takInfo.tkNotPartOfAuthorization
        else -> {
            println("ERROR: mkContractViewData() called with unknown contractResource: $contractResource")
            exitProcess(1)
        }
    }

    val content = mutableListOf<List<String>>()

    for (contract in contracts) {
        content.add(listOf<String>(contract.id.toString(), contract.namespace, contract.major.toString()))
    }
    return ViewData(listOf("Id", "Tjänstekontraktets namnrymd", "Major"), content)
}
 */

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
    return ViewData(listOf("Id", "Konsument", "Kontrakt", "Logisk adress"), content)
}

suspend fun mkRoutingViewData(cpId: Int): ViewData {
    val takInfo = obtainTakInfo(cpId)

    val content = mutableListOf<List<String>>()

    for (routing in takInfo.routings) {
        content.add(
            listOf<String>(
                routing.id.toString(),
                ServiceComponent.mapped[routing.serviceComponentId]!!.description,
                ServiceContract.mapped[routing.serviceContractId]!!.namespace,
                LogicalAddress.mapped[routing.logicalAddressId]!!.description
            )
        )
    }
    return ViewData(listOf("Id", "Tjänsteproducent", "Kontrakt", "Logisk adress"), content)
}
