package com.wire.ganymede.setup.exceptions

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import mu.KLogging

private val logger = KLogging().logger("ExceptionHandler")

/**
 * Registers exception handling.
 */
fun Application.registerExceptionHandlers() {
    install(StatusPages) {
        exception<ServiceUnavailableException> { cause ->
            logger.error { "Service Unavailable: $cause" }
            call.respond(HttpStatusCode.ServiceUnavailable)
        }

        exception<DataValidationException> { cause ->
            logger.error { "Malformed data received! ${cause.message}" }

            call.respond(status = HttpStatusCode.BadRequest) {
                mapOf("message" to cause.message)
            }
        }
    }
}
