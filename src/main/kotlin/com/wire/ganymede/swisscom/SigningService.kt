package com.wire.ganymede.swisscom

import com.wire.ganymede.internal.WireInternalClient
import com.wire.ganymede.internal.model.SignResponse
import com.wire.ganymede.internal.model.Signature
import com.wire.ganymede.internal.model.User
import com.wire.ganymede.setup.exceptions.SwisscomDataValidationException
import mu.KLogging
import java.util.UUID

/**
 * Service used for signing the documents.
 */
class SigningService(private val swisscomClient: SwisscomClient, private val wireClient: WireInternalClient) {

    private companion object : KLogging()

    /**
     * Signs the provided document. Throws exception if received data are malformed.
     */
    suspend fun sign(userId: UUID, documentId: String, documentHash: String, documentName: String): SignResponse {
        logger.debug { "Sign request received for user $userId. Obtaining user from Wire" }
        val user = wireClient.getUser(userId)
        logger.debug { "User obtained." }
        return sign(user, documentId, documentHash, documentName)
    }

    /**
     * Signs the provided document. Throws exception if received data are malformed.
     */
    suspend fun sign(user: User, documentId: String, documentHash: String, documentName: String): SignResponse {
        logger.debug { "Sign request to swisscom." }
        val signResponse = swisscomClient
            .sign(user, documentId = documentId, hash = documentHash, name = documentName)
        logger.debug { "Response is ${if (signResponse != null) "present" else "null!"}" }

        val outputs = assureNotNull(signResponse?.optionalOutputs) {
            signResponse?.result?.minor ?: "No data received from Swisscom!"
        }

        logger.debug { "Building sign response." }
        return SignResponse(
            responseId = assureNotNull(outputs.responseId) { "Response ID was not set! This is not valid request." },
            consentURL = outputs.stepUpAuthorisationInfo?.result?.url
        )
    }

    /**
     * Polls the swisscom API. Throws the exception when received data are incorrect.
     */
    suspend fun pending(responseId: UUID): Signature {
        logger.debug { "Pending request to swisscom." }
        val response = swisscomClient.pending(responseId)
        logger.debug { "Response received. Response is ${if (response != null) "present" else "null!"}" }

        val signature = response?.signature ?: throw SwisscomDataValidationException("Signature was not set!")
        logger.debug { "Signature exists." }
        val signatureObject = signature.other?.signatureObjects?.extendedSignatureObject

        logger.debug { "SignatureObject ${if (signatureObject != null) "exists" else "is null!"}" }
        return Signature(
            documentId = assureNotNull(signatureObject?.documentId) { "Document id from signature object can not be null!" },
            cms = assureNotNull(signatureObject?.base64Signature?.value) { "CMS or base64Signature can not be null!" }
        )
    }

    /**
     * Throws [SwisscomDataValidationException] on null input.
     */
    private inline fun <T : Any> assureNotNull(value: T?, lazyMessage: () -> Any): T {
        if (value == null) {
            val message = lazyMessage()
            throw SwisscomDataValidationException(message.toString())
        } else {
            return value
        }
    }
}


