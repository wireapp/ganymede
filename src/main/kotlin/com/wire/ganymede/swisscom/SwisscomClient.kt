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

        val request = RootSignRequest().apply {
            hashDocument(hash, documentId)
            createSignRequestForName(signer, name)
        }

        return resolveRequest(request, signUrl)
    }

    /**
     * Issues pending request.
     */
    suspend fun pending(responseId: UUID): SignResponse? = resolveRequest(
        RootPendingRequest(responseId), pendingUrl
    )

    private suspend fun <T : Any> resolveRequest(body: T, url: String): SignResponse? {
        // Swisscom is not using http codes to indicate result of the request
        val result = client.post<HttpStatement>(body = body) {
            url(url)
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }.execute()

        val (signResponse, _) = tryParse<RootSignResponse>(result)
        return signResponse?.signResponse
    }

    private suspend inline fun <reified T : Any> tryParse(response: HttpResponse): Pair<T?, String> =
        when {
            response.status.isSuccess() -> {
                val receivedText = response.receive<String>()
                parseJson<T>(receivedText) to receivedText
            }
            else -> {
                val receivedText = response.receive<String>()
                logger.error { "Request was not successful! Status code: ${response.status}, payload: $receivedText" }
                null to receivedText
            }
        }
}
