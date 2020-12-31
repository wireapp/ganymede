package com.wire.ganymede.swisscom.errors

import pw.forst.tools.katlib.newLine
import pw.forst.tools.katlib.prettyPrintJson
import pw.forst.tools.katlib.whenNull
import com.wire.ganymede.setup.exceptions.SwisscomDataValidationException
import com.wire.ganymede.setup.exceptions.errorResponse
import com.wire.ganymede.utils.countException
import com.wire.ganymede.utils.createLogger
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.micrometer.core.instrument.MeterRegistry
import kotlinx.coroutines.delay

private val logger = createLogger("DataValidationExceptionHandler")

/**
 * More complicated error handling when data received from the Swisscom are incorrect.
 */
fun StatusPages.Configuration.dataValidationExceptionHandler(registry: MeterRegistry) {
    exception<SwisscomDataValidationException> { cause ->
        cause.json?.let {
            val guess = guessError(it)
            call.respond(cause, guess)
            registry.countException(cause, mapOf("reason" to guess.response.reasoning))
        }.whenNull {
            logger.error(cause) { "No data received from Swisscom API.! ${cause.message}." }
            call.errorResponse(HttpStatusCode.ServiceUnavailable, cause.message)
            registry.countException(cause)
        }
    }
}

private suspend fun ApplicationCall.respond(exception: SwisscomDataValidationException, guessedError: GuessedError) {
    logger.error(exception) {
        "Handling exception for swisscom request id: ${guessedError.requestId}. Guessed reason: ${guessedError.response.reasoning}"
    }
    logger.debug { "Received JSON:$newLine${prettyPrintJson(guessedError.receivedJson)}" }
    val response = guessedError.response
    slowDownBuggedClients(response)
    respond(
        status = response.finalStatusCode,
        message = mapOf(
            "message" to response.reasoning,
            "details" to response
        )
    )
}

private suspend fun slowDownBuggedClients(response: SwisscomResponse) {
    if (response is ResourceStillInPendingState) {
        logger.warn { "Resource is still in pending state, sleeping for 1s to slow down clients." }
        // this is here because our frontend apps will retry instantly
        delay(1000)
    }
}
