package com.example.view

import com.example.models.*
import com.example.plugins.UrlPathResource
import io.ktor.server.freemarker.*

suspend fun showSummaryView(platformId: PLATFORM_ID): FreeMarkerContent {
    val takInfo = obtainTakInfo(platformId)
    return FreeMarkerContent(
        "summary.ftl",
        mapOf(
            "platformName" to takInfo.platformName,
            "numOfConsumers" to takInfo.serviceConsumers.size,
            "numOfProducers" to takInfo.serviceProducers.size,
            "numOfContracts" to takInfo.contracts.size,
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

suspend fun showDataView(platformId: PLATFORM_ID, resource: UrlPathResource): FreeMarkerContent {
    val takInfo = obtainTakInfo(platformId)
    val platformName = takInfo.platformName

    val items: List<TakData>
    val heading: String
    var columnHeadings: List<String> = listOf()

    when (resource) {
        UrlPathResource.CONSUMERS -> {
            items = takInfo.serviceConsumers
            heading = "Tjänstekonsumenter i $platformName"
            columnHeadings = ServiceComponent.columnHeadingList()
        }

        UrlPathResource.PRODUCERS -> {
            items = takInfo.serviceProducers
            heading = "Tjänsteproducenter i $platformName"
            columnHeadings = ServiceComponent.columnHeadingList()
        }

        UrlPathResource.CONTRACTS -> {
            items = takInfo.contracts
            heading = "Tjänstekontrakt i $platformName"
            columnHeadings = Contract.columnHeadingList()
        }

        UrlPathResource.LOGICAL_ADDRESS -> {
            items = takInfo.logicalAddresses
            heading = "Logiska adresser i $platformName"
            columnHeadings = LogicalAddress.columnHeadingList()
        }

        UrlPathResource.AUTHORIZATION -> {
            items = takInfo.authorizations
            heading = "Anropsbehörigheter i $platformName"
            columnHeadings = Authorization.columnHeadingList()
        }

        UrlPathResource.ROUTING -> {
            items = takInfo.routings
            heading = "Vägval i $platformName"
            columnHeadings = Routing.columnHeadingList()
        }

        UrlPathResource.TKNOTPARTOFAUTHORIZATION -> {
            items = takInfo.tkNotPartOfAuthorization
            heading = "Tjänstekontrakt som inte ingår i någon anropsbehörighet i $platformName"
            columnHeadings = Contract.columnHeadingList()
        }

        UrlPathResource.TKNOTPARTOFROUTING -> {
            items = takInfo.tkNotPartOfRouting
            heading = "Tjänstekontrakt som inte ingår i något vägval i $platformName"
            columnHeadings = Contract.columnHeadingList()
        }

        UrlPathResource.LANOTPARTOFROUTING -> {
            items = takInfo.laNotPartOfRouting
            heading = "Logiska adresser som inte ingår i något vägval i $platformName"
            columnHeadings = LogicalAddress.columnHeadingList()
        }

        UrlPathResource.AUTHORIZATIONWITHOUTAMATCHINGROUTING -> {
            items = takInfo.authorizationWithoutAMatchingRouting
            heading = "Anropsbehörigheter som inte ingår i något vägval i $platformName"
            columnHeadings = Authorization.columnHeadingList()
        }
    }
    val content = mutableListOf<List<String>>()

    /*
    for (item in items) {
        content.add(item.tableRowList())
    }
     */

    return io.ktor.server.freemarker.FreeMarkerContent(
        "tableview.ftl",
        kotlin.collections.mapOf(
            "heading" to heading,
            "tableHeadings" to columnHeadings,
            "viewData" to content
        )
    )
}

suspend fun showTabulatorView(platformId: PLATFORM_ID, resource: UrlPathResource): FreeMarkerContent {
    val takInfo = obtainTakInfo(platformId)
    val platformName = takInfo.platformName

    val heading: String
    var ajaxUrl: String = "/api/tak/${platformId.name}/${resource.name.lowercase()}"
    var tabulatorRowSpecifications: List<TabulatorRowSpecification> = listOf<TabulatorRowSpecification>()

    when (resource) {
        UrlPathResource.CONSUMERS -> {
            // ajaxUrl = "/api/tak/$cpId/consumers"
            heading = "Tjänstekonsumenter i $platformName"
            tabulatorRowSpecifications = ServiceComponent.tabulatorRowSpecifications
        }

        UrlPathResource.PRODUCERS -> {
            ajaxUrl = "/api/tak/5/consumers"
            heading = "Tjänsteproducenter i $platformName"
        }

        UrlPathResource.CONTRACTS -> {
            ajaxUrl = "/api/tak/5/consumers"
            heading = "Tjänstekontrakt i $platformName"
        }

        UrlPathResource.LOGICAL_ADDRESS -> {
            ajaxUrl = "/api/tak/5/consumers"
            heading = "Logiska adresser i $platformName"
        }

        UrlPathResource.AUTHORIZATION -> {
            ajaxUrl = "/api/tak/5/consumers"
            heading = "Anropsbehörigheter i $platformName"
        }

        UrlPathResource.ROUTING -> {
            ajaxUrl = "/api/tak/5/consumers"
            heading = "Vägval i $platformName"
        }

        UrlPathResource.TKNOTPARTOFAUTHORIZATION -> {
            ajaxUrl = "/api/tak/5/consumers"
            heading = "Tjänstekontrakt som inte ingår i någon anropsbehörighet i $platformName"
        }

        UrlPathResource.TKNOTPARTOFROUTING -> {
            ajaxUrl = "/api/tak/5/consumers"
            heading = "Tjänstekontrakt som inte ingår i något vägval i $platformName"
        }

        UrlPathResource.LANOTPARTOFROUTING -> {
            ajaxUrl = "/api/tak/5/consumers"
            heading = "Logiska adresser som inte ingår i något vägval i $platformName"
        }

        UrlPathResource.AUTHORIZATIONWITHOUTAMATCHINGROUTING -> {
            ajaxUrl = "/api/tak/5/consumers"
            heading = "Anropsbehörigheter som inte ingår i något vägval i $platformName"
        }
    }

    return io.ktor.server.freemarker.FreeMarkerContent(
        "tabulatorview.ftl",
        kotlin.collections.mapOf(
            "heading" to heading,
            "tabulatorViewSpecifications" to tabulatorRowSpecifications,
            "ajaxUrl" to ajaxUrl
        )
    )
}
