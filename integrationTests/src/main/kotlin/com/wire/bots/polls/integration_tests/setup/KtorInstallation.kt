package com.wire.bots.polls.integration_tests.setup

import com.fasterxml.jackson.databind.SerializationFeature
import com.wire.bots.polls.integration_tests.routing.registerRoutes
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.http.cio.websocket.pingPeriod
import io.ktor.http.cio.websocket.timeout
import io.ktor.jackson.jackson
import io.ktor.routing.routing
import io.ktor.util.KtorExperimentalAPI
import io.ktor.websocket.WebSockets
import mu.KLogger
import org.kodein.di.generic.instance
import org.kodein.di.ktor.kodein
import java.text.DateFormat
import java.time.Duration


/**
 * Loads the application.
 */
@KtorExperimentalAPI
fun Application.init() {
    setupKodein()
    // now kodein is running and can be used
    val k by kodein()
    val logger by k.instance<KLogger>("install-logger")
    logger.debug { "DI container started." }

    // configure Ktor
    installFrameworks()

    // register routing
    routing {
        registerRoutes()
    }
}

/**
 * Configure Ktor and install necessary extensions.
 */
fun Application.installFrameworks() {
    install(ContentNegotiation) {
        jackson {
            // enable pretty print for JSONs
            enable(SerializationFeature.INDENT_OUTPUT)
            dateFormat = DateFormat.getDateTimeInstance()
        }
    }

    install(DefaultHeaders)
    install(CallLogging)

    install(WebSockets) {
        // enable ping - to keep the connection alive
        pingPeriod = Duration.ofSeconds(30)
        timeout = Duration.ofSeconds(15)
        // disabled (max value) - the connection will be closed if surpassed this length.
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

}
