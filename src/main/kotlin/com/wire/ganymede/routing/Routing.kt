package com.wire.ganymede.routing

import com.wire.ganymede.utils.createLogger
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import org.kodein.di.LazyKodein
import org.kodein.di.generic.instance

internal val routingLogger by lazy { createLogger("RoutingLogger") }

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
        call.respond(mapOf("version" to version))
    }

    /**
     * Responds only 200 for ingres.
     */
    get("/status") {
        call.respond(HttpStatusCode.OK)
    }

    prometheusRoute(k)
    signingRoute(k)
}
