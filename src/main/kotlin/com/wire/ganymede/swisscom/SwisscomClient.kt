package com.wire.ganymede.swisscom

import ai.blindspot.ktoolz.extensions.parseJson
import com.wire.ganymede.internal.model.User
import com.wire.ganymede.setup.exceptions.SwisscomUnavailableException
import com.wire.ganymede.swisscom.model.RootPendingRequest
import com.wire.ganymede.swisscom.model.RootSignRequest
import com.wire.ganymede.swisscom.model.RootSignResponse
import com.wire.ganymede.swisscom.model.SignResponse
import com.wire.ganymede.swisscom.model.SwisscomAPIConfig
import com.wire.ganymede.swisscom.model.createSignRequestForName
import com.wire.ganymede.swisscom.model.hashDocument
import com.wire.ganymede.utils.appendPath
import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.HttpStatement
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import mu.KLogging
import java.util.UUID

/**
 * Client class which provides connection to Swisscom API.
 * This service can in some cases return invalid or nul data.
 * Second value of the pair are data received from the Swisscom API.
 */
class SwisscomClient(private val client: HttpClient, apiConfig: SwisscomAPIConfig) {

    private companion object : KLogging()

    private val signUrl = apiConfig.baseUrl appendPath apiConfig.signPath
    private val pendingUrl = apiConfig.baseUrl appendPath apiConfig.pendingPath

    /**
     * Issues sign request.
     */
    suspend fun sign(signer: User, documentId: String, documentHash: String, documentName: String)
            : Pair<SignResponse?, SwisscomApiResponse> {
        logger.debug { "Sign request - building object for the Swisscom API." }
        val request = RootSignRequest().apply {
            hashDocument(documentHash, documentId)
            createSignRequestForName(signer, documentName)
        }
        logger.debug { "Document prepared, executing." }

        return resolveRequest(request, signUrl)
            .also { logger.debug { "Sign request resolved." } }
    }

    /**
     * Issues pending request.
     */
    suspend fun pending(responseId: UUID): Pair<SignResponse?, SwisscomApiResponse> {
        logger.debug { "Sending pending request." }
        return resolveRequest(RootPendingRequest(responseId), pendingUrl)
            .also { logger.debug { "Pending request resolved." } }
    }

    private suspend fun <T : Any> resolveRequest(body: T, url: String): Pair<SignResponse?, SwisscomApiResponse> {
        logger.debug { "Executing request to Swisscom" }

        val result = client.post<HttpStatement>(body = body) {
            url(url)
            contentType(ContentType.Application.Json)
        }.execute()
        logger.debug { "Request status: ${result.status}. Parsing received data." }

        val (signResponse, apiResponse) = tryParse<RootSignResponse>(result)

        return signResponse?.signResponse to apiResponse
    }

    private suspend inline fun <reified T : Any> tryParse(response: HttpResponse): Pair<T?, SwisscomApiResponse> =
        when {
            // Swisscom is not using http codes to indicate result of the request
            response.status.isSuccess() -> {
                logger.debug { "Result was successful." }
                val receivedText = response.receive<String>()
                logger.debug { "Parsing JSON." }
                parseJson<T>(receivedText) to receivedText
            }
            else -> {
                throw SwisscomUnavailableException(response.status, runCatching { response.receive<String>() }.getOrNull())
            }
        }
}
