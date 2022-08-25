package com.example.controller

import com.example.models.Authorization
import com.example.models.Contract
import com.example.models.LogicalAddress
import com.example.models.Routing

fun tkNotPartOfAuthorization(authorizations: List<Authorization>, contracts: List<Contract>): List<Contract> {
    val usedTkInAuth = authorizations.map { it.serviceContractId }.toSet()
    return contracts.filterNot { usedTkInAuth.contains(it.id) }
}

fun tkNotPartOfRouting(routings: List<Routing>, contracts: List<Contract>): List<Contract> {
    val usedTkInRouting = routings.map { it.serviceContractId }.toSet()
    return contracts.filterNot { usedTkInRouting.contains(it.id) }
}

fun laNotPartOfRouting(routings: List<Routing>, logicalAddresses: List<LogicalAddress>): List<LogicalAddress> {
    val usedLaInRouting = routings.map { it.logicalAddressId }.toSet()
    return logicalAddresses.filterNot { usedLaInRouting.contains(it.id) }
}

// fun componentsNotUsed(authorizations: List<Authorization>, routings: List<Routing>, )
