package com.example.view

import com.example.models.*
import com.example.plugins.UrlPathResource
import io.ktor.server.freemarker.*

suspend fun showSummaryView(id: Int): FreeMarkerContent {
    val takInfo = obtainTakInfo(id)
    return FreeMarkerContent(
        "summary.ftl",
        mapOf(
            "cpId" to id,
            "plattform" to ConnectionPoint.getPlattform(id),
            "plattforms" to com.example.models.ConnectionPoint.plattforms,
            "numOfConsumers" to takInfo.serviceConsumers.size,
            "numOfProducers" to takInfo.serviceProducers.size,
            "numOfContracts" to takInfo.serviceContracts.size,
            "numOfLogicalAddresses" to takInfo.logicalAddresses.size,
            "numOfAuthorizations" to takInfo.authorizations.size,
            "numOfRoutings" to takInfo.routings.size,
            "numOfTkNotPartOfAuthorization" to takInfo.tkNotPartOfAuthorization.size,
            "numOfTkNotPartOfRouting" to takInfo.tkNotPartOfRouting.size,
            "numOfLaNotPartOfRouting" to takInfo.laNotPartOfRouting.size,
            "authorizationWithoutAMatchingRouting" to takInfo.authorizationWithoutAMatchingRouting.size
        )
    )
}

suspend fun showDataView(cpId: Int, resource: UrlPathResource): FreeMarkerContent {
    val takInfo = obtainTakInfo(cpId)
    val plattformName = takInfo.getPlattformName()

    val items: List<TakData>
    val heading: String
    var columnHeadings: List<String> = listOf()

    when (resource) {
        UrlPathResource.CONSUMERS -> {
            items = takInfo.serviceConsumers
            heading = "Tjänstekonsumenter i $plattformName"
            columnHeadings = ServiceComponent.columnHeadingList()
        }

        UrlPathResource.PRODUCERS -> {
            items = takInfo.serviceProducers
            heading = "Tjänsteproducenter i $plattformName"
            columnHeadings = ServiceComponent.columnHeadingList()
        }

        UrlPathResource.CONTRACTS -> {
            items = takInfo.serviceContracts
            heading = "Tjänstekontrakt i $plattformName"
            columnHeadings = ServiceContract.columnHeadingList()
        }

        UrlPathResource.LOGICAL_ADDRESS -> {
            items = takInfo.logicalAddresses
            heading = "Logiska adresser i $plattformName"
            columnHeadings = LogicalAddress.columnHeadingList()
        }

        UrlPathResource.AUTHORIZATION -> {
            items = takInfo.authorizations
            heading = "Anropsbehörigheter i $plattformName"
            columnHeadings = Authorization.columnHeadingList()
        }

        UrlPathResource.ROUTING -> {
            items = takInfo.routings
            heading = "Vägval i $plattformName"
            columnHeadings = Routing.columnHeadingList()
        }

        UrlPathResource.TKNOTPARTOFAUTHORIZATION -> {
            items = takInfo.tkNotPartOfAuthorization
            heading = "Tjänstekontrakt som inte ingår i någon anropsbehörighet i $plattformName"
            columnHeadings = ServiceContract.columnHeadingList()
        }

        UrlPathResource.TKNOTPARTOFROUTING -> {
            items = takInfo.tkNotPartOfRouting
            heading = "Tjänstekontrakt som inte ingår i något vägval i $plattformName"
            columnHeadings = ServiceContract.columnHeadingList()
        }

        UrlPathResource.LANOTPARTOFROUTING -> {
            items = takInfo.laNotPartOfRouting
            heading = "Logiska adresser som inte ingår i något vägval i $plattformName"
            columnHeadings = LogicalAddress.columnHeadingList()
        }

        UrlPathResource.AUTHORIZATIONWITHOUTAMATCHINGROUTING -> {
            items = takInfo.authorizationWithoutAMatchingRouting
            heading = "Anropsbehörigheter som inte ingår i något vägval i $plattformName"
            columnHeadings = Authorization.columnHeadingList()
        }
    }
    val content = mutableListOf<List<String>>()

    for (item in items) {
        content.add(item.tableRowList())
    }

    return io.ktor.server.freemarker.FreeMarkerContent(
        "tableview.ftl",
        kotlin.collections.mapOf(
            "heading" to heading,
            "tableHeadings" to columnHeadings,
            "viewData" to content
        )
    )
}
