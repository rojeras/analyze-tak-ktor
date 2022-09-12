package com.example.view // ktlint-disable filename

import com.example.models.*
import io.ktor.server.freemarker.*

suspend fun showSummaryView(takInfo: TakInfo): FreeMarkerContent {
    return FreeMarkerContent(
        "summary.ftl",
        mapOf(
            "platformName" to takInfo.platformName,
            "numOfConsumers" to takInfo.viewData[ViewDataType.CONSUMERS]!!.size,
            "numOfProducers" to takInfo.viewData[ViewDataType.PRODUCERS]!!.size,
            "numOfContracts" to takInfo.viewData[ViewDataType.CONTRACTS]!!.size,
            "numOfLogicalAddresses" to takInfo.viewData[ViewDataType.LOGICAL_ADDRESS]!!.size
            /*
            "numOfAuthorizations" to takInfo.authorizations.size,
            "numOfRoutings" to takInfo.routings.size,
            "numOfTkNotPartOfAuthorization" to takInfo.tkNotPartOfAuthorization.size,
            "numOfTkNotPartOfRouting" to takInfo.tkNotPartOfRouting.size,
            "numOfLaNotPartOfRouting" to takInfo.laNotPartOfRouting.size,
            "authorizationWithoutAMatchingRouting" to takInfo.authorizationWithoutAMatchingRouting.size
            */
        )
    )
}
