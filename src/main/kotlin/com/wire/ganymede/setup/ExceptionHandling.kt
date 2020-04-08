package com.wire.ganymede.setup

import com.wire.ganymede.internal.DataValidationException
import com.wire.ganymede.internal.ServiceUnavailableException
import com.wire.ganymede.utils.createJson
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import mu.KLogging

private val logger = KLogging().logger("ExceptionHandler")

fun Application.registerExceptionHandlers() {
    install(StatusPages) {
        exception<ServiceUnavailableException> { cause ->
            logger.error { "Service Unavailable: $cause" }
            call.respond(HttpStatusCode.ServiceUnavailable)
        }

        exception<DataValidationException> { cause ->
            logger.error { "Malformed data received! ${cause.message}" }

            call.respondText(
                contentType = ContentType.Application.Json,
                status = HttpStatusCode.BadRequest
            ) {
                createJson(mapOf("message" to cause.message))
            }
        }
    }
}
