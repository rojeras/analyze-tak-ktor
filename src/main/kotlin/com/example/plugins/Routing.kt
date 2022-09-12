package com.example.plugins

import com.example.models.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.serialization.json.Json

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
            /*
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
            */

            get("{platformName}/{takData}") {
                val platformName = call.parameters.getOrFail<String>("platformName")
                val takInfo = obtainTakInfoBasedOnName(platformName)

                val takData = call.parameters.getOrFail<String>("takData")
                val viewDataType: ViewDataType? = ViewDataType.getByName(takData)

                val viewContent = takInfo.viewDefinition(viewDataType!!)!!

                call.respond(
                    FreeMarkerContent(
                        "tabulatorview.ftl",
                        kotlin.collections.mapOf(
                            "heading" to viewContent.heading,
                            "tabulatorViewSpecifications" to viewContent.tabulatorRowSpecification,
                            "ajaxUrl" to "/api/tak/$platformName/$takData"
                        )
                    )
                )
            }
            /*

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

             */
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
            get("tak/{platformName}/{takData}") {
                val platformName = call.parameters.getOrFail<String>("platformName")
                val takData = call.parameters.getOrFail<String>("takData")
                val viewDataType = ViewDataType.getByName(takData)!!
                val takInfo = obtainTakInfoBasedOnName(platformName)
                // call.respond(takInfo.consumers)
                call.respond(takInfo.viewData[viewDataType]!!)
            }
        }
    }
}
