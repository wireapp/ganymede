package com.wire.ganymede.setup.exceptions

import io.ktor.http.HttpStatusCode

/**
 * Response when the dependency service is not available.
 */
class ServiceUnavailableException(message: String?) : Exception(message)

/**
 * Indicates that data received from the API were malformed.
 */
class SwisscomDataValidationException(message: String?) : Exception(message)

/**
 * Swisscom API didn't return success code.
 */
class SwisscomUnavailableException(statusCode: HttpStatusCode, receivedText: String?) :
    Exception("Swisscom status code: $statusCode with explanation: ${receivedText ?: "no text received"}.")

/**
 * Exception thrown when client does not received valid data.
 */
class WireInternalAPIException(message: String?, cause: Throwable) : Exception(message, cause)
