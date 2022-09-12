package com.example.controller

import com.example.models.*

fun tkNotPartOfAuthorization(
    authorizations: List<Authorization>,
    contracts: List<Contract>
): List<Contract> {
    val usedTkInAuth = authorizations.map { it.serviceContractId }.toSet()
    return contracts.filterNot { usedTkInAuth.contains(it.idInSource) }
}

fun tkNotPartOfRouting(routings: List<Routing>, contracts: List<Contract>): List<Contract> {
    val usedTkInRouting = routings.map { it.serviceContractId }.toSet()
    return contracts.filterNot { usedTkInRouting.contains(it.idInSource) }
}

fun laNotPartOfRouting(routings: List<Routing>, logicalAddresses: List<LogicalAddress>): List<LogicalAddress> {
    val usedLaInRouting = routings.map { it.logicalAddressId }.toSet()
    return logicalAddresses.filterNot { usedLaInRouting.contains(it.id) }
}

// Will probably not work with the TAK-api as source

// Version which checks if an auth is not part of any integration
/*
fun authorizationWithoutAMatchingRouting(
    authorizations: List<Authorization>,
    integrations: List<Integration>
): List<Authorization> {
    val usedAuthsInIntegrations = integrations.map { it.authorization.id }.toSet()
    return authorizations.filterNot { usedAuthsInIntegrations.contains(it.idInSource) }
}
 */
