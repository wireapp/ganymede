package com.wire.ganymede.internal

class ServiceUnavailableException(message: String?) : Exception(message)

/**
 * Indicates that data received from the API were malformed.
 */
class DataValidationException(message: String?) : Exception(message)


/**
 * Throws [DataValidationException] on null input.
 */
inline fun <T : Any> validateNotNull(value: T?, lazyMessage: () -> Any): T {
    if (value == null) {
        val message = lazyMessage()
        throw DataValidationException(message.toString())
    } else {
        return value
    }
}
