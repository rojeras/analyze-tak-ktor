package com.example.controller

import com.example.models.*

fun tkNotPartOfAuthorization(
    authorizations: List<Authorization>,
    contracts: List<ServiceContract>
): List<ServiceContract> {
    val usedTkInAuth = authorizations.map { it.serviceContractId }.toSet()
    return contracts.filterNot { usedTkInAuth.contains(it.id) }
}

fun tkNotPartOfRouting(routings: List<Routing>, contracts: List<ServiceContract>): List<ServiceContract> {
    val usedTkInRouting = routings.map { it.serviceContractId }.toSet()
    return contracts.filterNot { usedTkInRouting.contains(it.id) }
}

fun laNotPartOfRouting(routings: List<Routing>, logicalAddresses: List<LogicalAddress>): List<LogicalAddress> {
    val usedLaInRouting = routings.map { it.logicalAddressId }.toSet()
    return logicalAddresses.filterNot { usedLaInRouting.contains(it.id) }
}

// Will probably not work with the TAK-api as source

// Version which checks if an auth is not part of any integration
fun authorizationWithoutAMatchingRouting(
    authorizations: List<Authorization>,
    integrations: List<Integration>
): List<Authorization> {
    val usedAuthsInIntegrations = integrations.map { it.authorization.id }.toSet()
    return authorizations.filterNot { usedAuthsInIntegrations.contains(it.id) }
}
