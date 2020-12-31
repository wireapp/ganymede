package com.wire.ganymede.swisscom

import pw.forst.tools.katlib.whenNull
import com.wire.ganymede.internal.WireClient
import com.wire.ganymede.internal.model.SignResponse
import com.wire.ganymede.internal.model.Signature
import com.wire.ganymede.internal.model.User
import com.wire.ganymede.setup.exceptions.SwisscomDataValidationException
import mu.KLogging
import java.util.UUID

/**
 * Service used for signing the documents.
 * This service returns always valid data, otherwise exception is thrown.
 */
class SigningService(private val swisscomClient: SwisscomClient, private val wireClient: WireClient) {

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
        val (parsedResponse, rawData) = swisscomClient
            .sign(user, documentId = documentId, documentHash = documentHash, documentName = documentName)
        logger.debug { "Response received" }
        val signResponse = rawData.assureNotNull(parsedResponse) { "It was not possible to parse RootSignResponse!" }

        val outputs = rawData.assureNotNull(signResponse.optionalOutputs) {
            signResponse.result?.minor ?: "No data received from Swisscom!"
        }

        logger.debug { "Building sign response." }
        return SignResponse(
            responseId = rawData.assureNotNull(outputs.responseId) {
                "Response ID was not set! This is not valid request."
            },
            consentURL = rawData.assureNotNull(outputs.stepUpAuthorisationInfo?.result?.url) {
                "Consent URL not set! Request might be invalid"
            }
        )
    }

    /**
     * Polls the swisscom API. Throws the exception when received data are incorrect.
     */
    suspend fun pending(responseId: UUID): Signature {
        logger.debug { "Pending request to swisscom." }
        val (parsedResponse, rawData) = swisscomClient.pending(responseId)
        logger.debug { "Response received" }
        val pendingResponse = rawData.assureNotNull(parsedResponse) { "It was not possible to parse RootSignResponse!" }

        val signature = rawData.assureNotNull(pendingResponse.signature) { "Signature was not set!" }
        logger.debug { "Signature exists." }
        val signatureObject = signature.other?.signatureObjects?.extendedSignatureObject

        logger.debug { "SignatureObject ${if (signatureObject != null) "exists" else "is null!"}" }
        return Signature(
            documentId = rawData.assureNotNull(signatureObject?.documentId) { "Document id from signature object can not be null!" },
            cms = rawData.assureNotNull(signatureObject?.base64Signature?.value) { "CMS or base64Signature can not be null!" },
            serialNumber = pendingResponse.optionalOutputs?.stepUpAuthorisationInfo?.result?.serialNumber.whenNull {
                logger.warn { "Serial number for response $responseId was not set." }
            }
        )
    }

    /**
     * Throws [SwisscomDataValidationException] on null input.
     */
    private inline fun <T : Any> SwisscomApiResponse.assureNotNull(value: T?, lazyMessage: () -> Any): T {
        if (value == null) {
            val message = lazyMessage()
            throw SwisscomDataValidationException(message.toString(), this)
        } else {
            return value
        }
    }
}


