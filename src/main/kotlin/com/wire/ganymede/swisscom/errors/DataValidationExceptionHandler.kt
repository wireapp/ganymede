package com.wire.ganymede.swisscom.errors

import ai.blindspot.ktoolz.extensions.newLine
import ai.blindspot.ktoolz.extensions.prettyPrintJson
import ai.blindspot.ktoolz.extensions.whenNull
import com.wire.ganymede.setup.exceptions.SwisscomDataValidationException
import com.wire.ganymede.setup.exceptions.errorResponse
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import kotlinx.coroutines.delay
import mu.KLogging

private val logger = KLogging().logger("DataValidationExceptionHandler")

/**
 * More complicated error handling when data received from the Swisscom are incorrect.
 */
fun StatusPages.Configuration.dataValidationExceptionHandler() {
    exception<SwisscomDataValidationException> { cause ->
        cause.json?.let {
            call.respond(cause, guessError(it))
        }.whenNull {
            logger.error(cause) { "No data received from Swisscom API.! ${cause.message}." }
            call.errorResponse(HttpStatusCode.ServiceUnavailable, cause.message)
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