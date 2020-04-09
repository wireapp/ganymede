package com.wire.ganymede.internal

import com.wire.ganymede.internal.model.User
import com.wire.ganymede.internal.model.WireAPIConfig
import com.wire.ganymede.setup.exceptions.WireInternalAPIException
import com.wire.ganymede.utils.appendPath
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.url
import mu.KLogging
import java.util.UUID

/**
 * Client for internal Wire API.
 */
class WireInternalClient(private val client: HttpClient, apiConfig: WireAPIConfig) {

    private companion object : KLogging()

    private val userUrl = apiConfig.baseUrl appendPath apiConfig.userPath

    /**
     * Obtains user information for the given userId.
     */
    suspend fun getUser(userId: UUID): User {
        logger.debug { "Fetching data for the userId $userId " }
        return runCatching {
            // TODO get current user
            client.get<User> {
                url(userUrl)
            }
        }.onSuccess {
            logger.debug { "User request was successful." }
        }.onFailure {
            logger.error { "Request for userId $userId failed." }
        }.getOrElse {
            throw WireInternalAPIException("It was not possible to fetch user for id $userId", it)
        }
    }
}
