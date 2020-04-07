package com.wire.signatures.backend.routing

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondTextWriter
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.micrometer.prometheus.PrometheusMeterRegistry
import org.kodein.di.LazyKodein
import org.kodein.di.generic.instance

/**
 * Registers prometheus data.
 */
fun Routing.prometheusRoute(k: LazyKodein) {
    val registry by k.instance<PrometheusMeterRegistry>()

    get("/prometheus") {
        call.respondTextWriter(status = HttpStatusCode.OK) {
            @Suppress("BlockingMethodInNonBlockingContext") // sadly this is synchronous API
            registry.scrape(this)
        }
    }
}
