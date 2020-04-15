package com.wire.ganymede.swisscom.errors

import io.ktor.http.HttpStatusCode

/**
 * Simple DTO which carries guessed data.
 */
data class GuessedError(
    val receivedJson: String,
    val requestId: String?,
    val response: SwisscomResponse
)

/**
 * Base class for error messages for swisscom API calling.
 */
sealed class SwisscomResponse {
    abstract val reasoning: String
    abstract val finalStatusCode: HttpStatusCode
    abstract val swisscomMessage: String?
}

data class UnknownResponse(
    override val swisscomMessage: String? = null,
    override val reasoning: String = "Swisscom send unknown response.",
    override val finalStatusCode: HttpStatusCode = HttpStatusCode.ServiceUnavailable
) : SwisscomResponse()

data class SignWithMalformedMail(
    override val swisscomMessage: String?,
    override val reasoning: String = "Malformed e-mail address was used to sign the document",
    override val finalStatusCode: HttpStatusCode = HttpStatusCode.BadRequest
) : SwisscomResponse()

data class SignWithExpiredCertificate(
    override val swisscomMessage: String?,
    override val reasoning: String = "Expired certificate was used to sign the request.",
    override val finalStatusCode: HttpStatusCode = HttpStatusCode.ServiceUnavailable
) : SwisscomResponse()

data class SignWithWrongSerialNumber(
    override val swisscomMessage: String?,
    override val reasoning: String = "UserId to PhoneNo mismatch, wrong serial number was used for the phone.",
    override val finalStatusCode: HttpStatusCode = HttpStatusCode.BadRequest
) : SwisscomResponse()

data class ResourceStillInPendingState(
    override val swisscomMessage: String?,
    override val reasoning: String = "Sign was not completed yet, still in pending state.",
    override val finalStatusCode: HttpStatusCode = HttpStatusCode.ServiceUnavailable
) : SwisscomResponse()

data class ExpiredRequestIdUsed(
    override val swisscomMessage: String?,
    override val reasoning: String = "Provided request id is expired.",
    override val finalStatusCode: HttpStatusCode = HttpStatusCode.BadRequest
) : SwisscomResponse()
