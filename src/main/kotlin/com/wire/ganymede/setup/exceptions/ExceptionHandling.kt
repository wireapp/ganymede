package com.wire.ganymede.setup.exceptions

import com.wire.ganymede.swisscom.errors.dataValidationExceptionHandler
import com.wire.ganymede.utils.countException
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.BadRequestException
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.micrometer.prometheus.PrometheusMeterRegistry
import mu.KLogging
import org.kodein.di.LazyKodein
import org.kodein.di.generic.instance

private val logger = KLogging().logger("ExceptionHandler")

/**
 * Registers exception handling.
 */
fun Application.registerExceptionHandlers(k: LazyKodein) {
    val registry by k.instance<PrometheusMeterRegistry>()

    install(StatusPages) {
        exception<UnauthorizedException> { cause ->
            logger.warn { "Unauthorized request: ${cause.message}" }
            call.errorResponse(HttpStatusCode.Unauthorized, cause.message)
            registry.countException(cause)
        }

        exception<BadRequestException> { cause ->
            logger.warn { "Bad request - ${cause.message}" }
            call.errorResponse(HttpStatusCode.BadRequest, cause.message)
            registry.countException(cause)
        }

        exception<ServiceUnavailableException> { cause ->
            logger.error(cause) { "Depend service is not available: ${cause.message}" }
            call.errorResponse(HttpStatusCode.ServiceUnavailable, cause.message)
            registry.countException(cause)
        }

        dataValidationExceptionHandler(registry)

        exception<SwisscomUnavailableException> { cause ->
            logger.error(cause) { "Swisscom API is not available. ${cause.message}" }
            call.errorResponse(HttpStatusCode.ServiceUnavailable, cause.message)
            registry.countException(cause)
        }

        exception<WireInternalUnavailableException> { cause ->
            logger.error(cause) { "Wire internal API call failed. ${cause.message}" }
            call.errorResponse(HttpStatusCode.ServiceUnavailable, cause.message)
            registry.countException(cause)
        }

        exception<WireInternalMalformedDataException> { cause ->
            logger.error(cause) { "Wire internal API returned invalid data. ${cause.message}" }
            call.errorResponse(HttpStatusCode.ServiceUnavailable, cause.message)
            registry.countException(cause)
        }

        exception<Exception> { cause ->
            logger.error(cause) { "Exception occurred in the application: ${cause.message}" }
            call.errorResponse(HttpStatusCode.InternalServerError, cause.message)
            registry.countException(cause)
        }
    }
}

suspend inline fun ApplicationCall.errorResponse(statusCode: HttpStatusCode, message: String?) {
    respond(status = statusCode, message = mapOf("message" to (message ?: "No details specified")))
}
