package com.wire.signatures.backend.routing

import ai.blindspot.ktoolz.extensions.newLine
import com.wire.signatures.backend.dao.DatabaseSetup
import io.ktor.application.call
import io.ktor.content.TextContent
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import org.kodein.di.generic.instance
import org.kodein.di.ktor.kodein

/**
 * Register routes to the KTor.
 */
fun Routing.registerRoutes() {

    val k by kodein()
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

    /**
     * More complex API for indication of all resources.
     */
    get("/status/health") {
        if (DatabaseSetup.isConnected()) {
            call.respond("healthy")
        } else {
            call.respond(HttpStatusCode.ServiceUnavailable, "DB connection is not working")
        }
    }
}
