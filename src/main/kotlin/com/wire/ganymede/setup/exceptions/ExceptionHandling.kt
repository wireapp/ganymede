package com.wire.ganymede.setup.exceptions

import io.ktor.application.Application
import io.ktor.application.ApplicationCall
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
            logger.error(cause) { "Depend service is not available: ${cause.message}" }
            call.errorResponse(HttpStatusCode.ServiceUnavailable, cause.message)
        }

        exception<SwisscomDataValidationException> { cause ->
            logger.error(cause) { "Malformed data received from Swisscom API.! ${cause.message}" }
            call.errorResponse(HttpStatusCode.ServiceUnavailable, cause.message)
        }

        exception<SwisscomUnavailableException> { cause ->
            logger.error(cause) { "Swisscom API is not available. ${cause.message}" }
            call.errorResponse(HttpStatusCode.ServiceUnavailable, cause.message)
        }

        exception<WireInternalUnavailableException> { cause ->
            logger.error(cause) { "Wire internal API call failed. ${cause.message}" }
            call.errorResponse(HttpStatusCode.ServiceUnavailable, cause.message)
        }

        exception<WireInternalMalformedDataException> { cause ->
            logger.error(cause) { "Wire internal API returned invalid data. ${cause.message}" }
            call.errorResponse(HttpStatusCode.ServiceUnavailable, cause.message)
        }
    }
}

private suspend inline fun ApplicationCall.errorResponse(statusCode: HttpStatusCode, message: String?) {
    respond(status = statusCode) {
        mapOf("message" to (message ?: "No details specified"))
    }
}
