package com.example.plugins

import com.example.models.*
import com.example.view.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.serialization.json.Json

enum class UrlPathResource(urlName: String) {
    CONTRACTS("contracts"),
    CONSUMERS("consumers"),
    PRODUCERS("producers"),
    LOGICAL_ADDRESS("logicaladdress"),
    AUTHORIZATION("authorizations"),
    ROUTING("routings"),
    TKNOTPARTOFAUTHORIZATION("tkNotPartOfAuthorization"),
    TKNOTPARTOFROUTING("tkNotPartOfRouting"),
    LANOTPARTOFROUTING("laNotPartOfRouting"),
    AUTHORIZATIONWITHOUTAMATCHINGROUTING("authorizationWithoutAMatchingRouting")
}

fun Application.configureRouting() {
    routing {
        static("/static") {
            resources("files")
        }

        get("/") {
            call.respondRedirect("tak/select")
        }

        get("hello") {
            call.respondText("Hello World!")
        }

        route("tak") {
            get("") {
                println("In tak/select")
                call.respond(
                    io.ktor.server.freemarker.FreeMarkerContent(
                        "select.ftl",
                        kotlin.collections.mapOf(
                            "plattforms" to com.example.models.ConnectionPoint.plattforms,
                            "cpId" to 0
                        )
                    )
                )
            }

            get("{tpId}") {
                val id = call.parameters.getOrFail<Int>("tpId").toInt()
                call.respond(showSummaryView(id))
            }
            /*
            get("{tpId}/consumers") {
                val id = call.parameters.getOrFail<Int>("tpId").toInt()
                // call.respond(showComponentView(id, UrlPathResource.CONSUMERS))
                call.respond(showDataView(id, UrlPathResource.CONSUMERS))
            }
            */
            get("{tpId}/consumers") {
                val cpId = call.parameters.getOrFail<Int>("tpId").toInt()
                call.respond(
                    FreeMarkerContent(
                        "tabulatorview.ftl",
                        kotlin.collections.mapOf(
                            "heading" to "Tjänstekonsumenter i ${getPlattformName(cpId)}",
                            "tabulatorViewSpecifications" to ServiceComponent.tabulatorRowSpecifications,
                            "ajaxUrl" to "/api/tak/$cpId/consumers"
                        )
                    )
                )
                // call.respond(showTabulatorView(id, UrlPathResource.CONSUMERS))
            }

            get("{tpId}/producers") {
                val id = call.parameters.getOrFail<Int>("tpId").toInt()
                call.respond(showDataView(id, UrlPathResource.PRODUCERS))
                // call.respond(showComponentView(id, UrlPathResource.PRODUCERS))
            }

            get("{tpId}/logicaladdress") {
                val id = call.parameters.getOrFail<Int>("tpId").toInt()
                call.respond(showDataView(id, UrlPathResource.LOGICAL_ADDRESS))
                // call.respond(mkLogicalAddressView(id))
            }

            get("{tpId}/contracts") {
                val id = call.parameters.getOrFail<Int>("tpId").toInt()
                call.respond(showDataView(id, UrlPathResource.CONTRACTS))
                // call.respond(mkContractView(id))
            }

            get("{tpId}/authorizations") {
                val id = call.parameters.getOrFail<Int>("tpId").toInt()
                call.respond(showDataView(id, UrlPathResource.AUTHORIZATION))
                // call.respond(mkAuthorizationView(id))
            }

            get("{tpId}/routings") {
                val id = call.parameters.getOrFail<Int>("tpId").toInt()
                call.respond(showDataView(id, UrlPathResource.ROUTING))
                // call.respond(mkRoutingView(id))
            }

            get("{tpId}/tknotpartofauthorization") {
                val id = call.parameters.getOrFail<Int>("tpId").toInt()
                call.respond(showDataView(id, UrlPathResource.TKNOTPARTOFAUTHORIZATION))
            }

            get("{tpId}/tknotpartofrouting") {
                val id = call.parameters.getOrFail<Int>("tpId").toInt()
                call.respond(showDataView(id, UrlPathResource.TKNOTPARTOFROUTING))
            }

            get("{tpId}/lanotpartofrouting") {
                val id = call.parameters.getOrFail<Int>("tpId").toInt()
                call.respond(showDataView(id, UrlPathResource.LANOTPARTOFROUTING))
            }

            get("{tpId}/authorizationwithoutamatchingrouting") {
                val id = call.parameters.getOrFail<Int>("tpId").toInt()
                call.respond(showDataView(id, UrlPathResource.AUTHORIZATIONWITHOUTAMATCHINGROUTING))
            }
        }

        route("api") {
            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        isLenient = true
                    }
                )
            }
            get("tak/{tpId}/consumers") {
                val id = call.parameters.getOrFail<Int>("tpId").toInt()
                // call.respond(showComponentView(id, UrlPathResource.CONSUMERS))
                val takInfo = obtainTakInfo(id)

                call.respond(takInfo.serviceConsumers)
            }
        }
    }
}
