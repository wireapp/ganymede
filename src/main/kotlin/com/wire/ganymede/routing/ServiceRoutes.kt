package com.wire.ganymede.routing

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondTextWriter
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.micrometer.prometheus.PrometheusMeterRegistry
import org.kodein.di.LazyKodein
import org.kodein.di.generic.instance

/**
 * Registers prometheus data.
 */
fun Routing.serviceRoutes(k: LazyKodein) {
    val version by k.instance<String>("version")
    val registry by k.instance<PrometheusMeterRegistry>()

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

    /**
     * More complex API for indication of all resources.
     */
    get("/status/health") {
        // TODO maybe try to ping wire and swisscom API?
        call.respond(mapOf("health" to "healthy"))
    }

    /**
     * Prometheus endpoint.
     */
    get("/metrics") {
        call.respondTextWriter(status = HttpStatusCode.OK) {
            @Suppress("BlockingMethodInNonBlockingContext") // sadly this is synchronous API
            registry.scrape(this)
        }
    }
}
