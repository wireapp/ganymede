package com.wire.ganymede.routing

import ai.blindspot.ktoolz.extensions.newLine
import io.ktor.application.call
import io.ktor.content.TextContent
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import org.kodein.di.LazyKodein
import org.kodein.di.generic.instance

/**
 * Register routes to the KTor.
 */
fun Routing.registerRoutes(k: LazyKodein) {

    val version by k.instance<String>("version")

    get("/") {
        call.respond("Server running version: \"$version\".")
    }

    get("/version") {
        call.respond(TextContent("{\"version\": \"$version\"}$newLine", ContentType.Application.Json))
    }

    /**
     * Responds only 200 for ingres.
     */
    get("/status") {
        call.respond(HttpStatusCode.OK)
    }

    prometheusRoute(k)
}
