package com.wire.ganymede.internal

import com.wire.ganymede.internal.model.User
import com.wire.ganymede.internal.model.WireAPIConfig
import com.wire.ganymede.setup.exceptions.WireInternalMalformedDataException
import com.wire.ganymede.setup.exceptions.WireInternalUnavailableException
import com.wire.ganymede.utils.appendPath
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.http.parametersOf
import mu.KLogging
import java.util.UUID

/**
 * Client for internal Wire API.
 */
class WireInternalClient(private val client: HttpClient, apiConfig: WireAPIConfig) : WireClient {

    private companion object : KLogging()

    private val userUrl = apiConfig.baseUrl appendPath apiConfig.userPath

    /**
     * Obtains user information for the given userId.
     */
    override suspend fun getUser(userId: UUID): User =
        runCatching {
            logger.debug { "Fetching data for the userId $userId " }

            client.get<Collection<User>> {
                url(userUrl)
                parametersOf("ids", userId.toString())
            }
        }.onSuccess {
            logger.error { "Request for userId $userId was successful." }
        }.onFailure {
            logger.error { "Request for userId $userId failed." }
        }.getOrElse {
            throw WireInternalUnavailableException("It was not possible to fetch user for id $userId", it)
        }.let {
            logger.debug { "Received user collection of count: ${it.size}" }
            it.singleOrNull()
                ?: throw WireInternalMalformedDataException(
                    "Received user collection for userId $userId contain ${it.size}, single user is expected.",
                    null
                )
        }
}
