package com.wire.ganymede.routing

import ai.blindspot.ktoolz.extensions.newLine
import io.ktor.application.call
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.content.TextContent
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import mu.KLogging
import org.kodein.di.LazyKodein
import org.kodein.di.generic.instance

internal val routingLogger by lazy { KLogging().logger("RoutingLogger") }

/**
 * Register routes to the KTor.
 */
fun Routing.registerRoutes(k: LazyKodein) {

    val version by k.instance<String>("version")

    /**
     * Information about service.
     */
    get("/") {
        call.respond("Server running version: \"$version\".")
    }

    /**
     * Send data about version.
     */
    get("/version") {
        call.respond(TextContent("{\"version\": \"$version\"}$newLine", ContentType.Application.Json))
    }

    /**
     * Responds only 200 for ingres.
     */
    get("/status") {
        call.respond(HttpStatusCode.OK)
    }

    /**
     * Used only for testing certificate validity.
     *
     * TODO delete this
     */
    get("/certificate-test") {
        val client by k.instance<HttpClient>()
        val response = client.get<String>("https://server.cryptomix.com/secure/")

        routingLogger.info { response }

        call.respond(TextContent("ok", ContentType.Text.Plain))
    }

    prometheusRoute(k)
}
