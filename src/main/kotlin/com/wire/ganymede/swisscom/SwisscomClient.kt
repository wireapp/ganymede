package com.wire.ganymede.swisscom

import com.wire.ganymede.dto.User
import com.wire.ganymede.swisscom.model.RootPendingRequest
import com.wire.ganymede.swisscom.model.RootSignRequest
import com.wire.ganymede.swisscom.model.RootSignResponse
import com.wire.ganymede.swisscom.model.SignResponse
import com.wire.ganymede.swisscom.model.SwisscomAPIConfig
import com.wire.ganymede.swisscom.model.createSignRequestForName
import com.wire.ganymede.swisscom.model.hashDocument
import com.wire.ganymede.utils.appendPath
import com.wire.ganymede.utils.parseJson
import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.request.accept
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
 */
class SwisscomClient(private val client: HttpClient, apiConfig: SwisscomAPIConfig) {

    private companion object : KLogging()

    private val signUrl = apiConfig.baseUrl appendPath apiConfig.signPath
    private val pendingUrl = apiConfig.baseUrl appendPath apiConfig.pendingPath

    /**
     * Issues sign request.
     */
    suspend fun sign(signer: User, documentId: String, hash: String, name: String): SignResponse? {
        logger.debug { "Sign request - building object for the Swisscom API." }
        val request = RootSignRequest().apply {
            hashDocument(hash, documentId)
            createSignRequestForName(signer, name)
        }
        logger.debug { "Document prepared, executing." }
        return resolveRequest(request, signUrl)
            .also { logger.debug { "Sign request resolved." } }
    }

    /**
     * Issues pending request.
     */
    suspend fun pending(responseId: UUID): SignResponse? {
        logger.debug { "Sending pending request." }
        return resolveRequest(RootPendingRequest(responseId), pendingUrl)
            .also { logger.debug { "Pending request resolved." } }
    }

    private suspend fun <T : Any> resolveRequest(body: T, url: String): SignResponse? {
        logger.debug { "Executing request to Swisscom" }

        val result = client.post<HttpStatement>(body = body) {
            url(url)
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }.execute()
        logger.debug { "Request status: ${result.status}. Parsing received data." }

        val (signResponse, _) = tryParse<RootSignResponse>(result)
        return signResponse?.signResponse
    }

    private suspend inline fun <reified T : Any> tryParse(response: HttpResponse): Pair<T?, String> =
        when {
            // Swisscom is not using http codes to indicate result of the request
            response.status.isSuccess() -> {
                logger.debug { "Result was successful." }
                val receivedText = response.receive<String>()
                logger.debug { "Parsing JSON." }
                parseJson<T>(receivedText) to receivedText
            }
            else -> {
                logger.error { "Non 200 status code from Swisscom API! Status code: ${response.status}" }
                val receivedText = response.receive<String>()
                logger.error { "Payload: $receivedText" }
                null to receivedText
            }
        }
}
