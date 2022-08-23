package com.example.plugins

import com.example.models.*
import com.example.view.mkAuthorizationViewData
import com.example.view.mkComponentViewData
import com.example.view.mkContractViewData
import com.example.view.mkLogicalAddressViewData
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

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
            get("{tpId}") {
                val id = call.parameters.getOrFail<Int>("tpId").toInt()
                call.respond(mkSummaryView(id))
            }

            get("{tpId}/consumers") {
                val id = call.parameters.getOrFail<Int>("tpId").toInt()
                call.respond(mkComponentView(ComponentType.CONSUMER, id))
            }

            get("{tpId}/producers") {
                val id = call.parameters.getOrFail<Int>("tpId").toInt()
                call.respond(mkComponentView(ComponentType.PRODUCER, id))
            }

            get("{tpId}/logicaladdress") {
                val id = call.parameters.getOrFail<Int>("tpId").toInt()
                call.respond(mkLogicalAddressView(id))
            }

            get("{tpId}/contracts") {
                val id = call.parameters.getOrFail<Int>("tpId").toInt()
                call.respond(mkContractView(id))
            }

            get("{tpId}/authorizations") {
                val id = call.parameters.getOrFail<Int>("tpId").toInt()
                call.respond(mkAuthorizationView(id))
            }

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
        }

        // The articles should be removed
        route("articles") {
            get {
                // Show a list of articles
                println("In articles")
                call.respond(FreeMarkerContent("index.ftl", mapOf("articles" to articles)))
            }
            get("new") {
                // Show a page with fields for creating a new article
                call.respond(FreeMarkerContent("new.ftl", model = null))
            }
            post {
                // Save an article
                val formParameters = call.receiveParameters()
                val title = formParameters.getOrFail("title")
                val body = formParameters.getOrFail("body")
                val newEntry = Article.newEntry(title, body)
                articles.add(newEntry)
                call.respondRedirect("/articles/${newEntry.id}")
            }
            get("{id}") {
                // Show an article with a specific id
                val id = call.parameters.getOrFail<Int>("id").toInt()
                call.respond(FreeMarkerContent("show.ftl", mapOf("article" to articles.find { it.id == id })))
            }
            get("{id}/edit") {
                // Show a page with fields for editing an article
                val id = call.parameters.getOrFail<Int>("id").toInt()
                call.respond(FreeMarkerContent("edit.ftl", mapOf("article" to articles.find { it.id == id })))
            }
            post("{id}") {
                // Update or delete an article
                val id = call.parameters.getOrFail<Int>("id").toInt()
                val formParameters = call.receiveParameters()
                when (formParameters.getOrFail("_action")) {
                    "update" -> {
                        val index = articles.indexOf(articles.find { it.id == id })
                        val title = formParameters.getOrFail("title")
                        val body = formParameters.getOrFail("body")
                        articles[index].title = title
                        articles[index].body = body
                        call.respondRedirect("/articles/$id")
                    }

                    "delete" -> {
                        articles.removeIf { it.id == id }
                        call.respondRedirect("/articles")
                    }
                }
            }
        }
    }
}

suspend fun mkSummaryView(id: Int): FreeMarkerContent {
    val takInfo = obtainTakInfo(id)
    return FreeMarkerContent(
        "summary.ftl",
        mapOf(
            "cpId" to id,
            "plattform" to ConnectionPoint.getPlattform(id),
            "plattforms" to com.example.models.ConnectionPoint.plattforms,
            "numOfConsumers" to takInfo.serviceConsumers.size,
            "numOfProducers" to takInfo.serviceProducers.size,
            "numOfContracts" to takInfo.contracts.size,
            "numOfLogicalAddresses" to takInfo.logicalAddresses.size
        )
    )
}

suspend fun mkComponentView(componentType: ComponentType, id: Int): FreeMarkerContent {
    val plattformName = ConnectionPoint.getPlattform(id)!!.getPlattformName()

    val heading = when (componentType) {
        ComponentType.CONSUMER -> "Tjänstekonsumenter i $plattformName"
        ComponentType.PRODUCER -> "Tjänsteproducenter i $plattformName"
    }

    val componentViewData = mkComponentViewData(id, componentType)

    return io.ktor.server.freemarker.FreeMarkerContent(
        "tableview.ftl",
        kotlin.collections.mapOf(
            "heading" to heading,
            "tableHeadings" to componentViewData.headings,
            "viewData" to componentViewData.content
        )
    )
}

suspend fun mkLogicalAddressView(id: Int): FreeMarkerContent {
    val plattformName = ConnectionPoint.getPlattform(id)!!.getPlattformName()

    val heading = "Logiska adresser i $plattformName"

    val laViewData = mkLogicalAddressViewData(id)

    return io.ktor.server.freemarker.FreeMarkerContent(
        "tableview.ftl",
        kotlin.collections.mapOf(
            "heading" to heading,
            "tableHeadings" to laViewData.headings,
            "viewData" to laViewData.content
        )
    )
}

suspend fun mkContractView(id: Int): FreeMarkerContent {
    val plattformName = ConnectionPoint.getPlattform(id)!!.getPlattformName()

    val heading = "Tjänstekontrakt i $plattformName"

    val contractViewData = mkContractViewData(id)

    return io.ktor.server.freemarker.FreeMarkerContent(
        "tableview.ftl",
        kotlin.collections.mapOf(
            "heading" to heading,
            "tableHeadings" to contractViewData.headings,
            "viewData" to contractViewData.content
        )
    )
}

suspend fun mkAuthorizationView(id: Int): FreeMarkerContent {
    val plattformName = ConnectionPoint.getPlattform(id)!!.getPlattformName()

    val heading = "Anropsbehörigheter i $plattformName"

    val authViewData = mkAuthorizationViewData(id)

    return io.ktor.server.freemarker.FreeMarkerContent(
        "tableview.ftl",
        kotlin.collections.mapOf(
            "heading" to heading,
            "tableHeadings" to authViewData.headings,
            "viewData" to authViewData.content
        )
    )
}
