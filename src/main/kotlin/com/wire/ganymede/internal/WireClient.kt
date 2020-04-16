package com.wire.ganymede.internal

import com.wire.ganymede.internal.model.User
import java.util.UUID

/**
 * Client for internal Wire API.
 */
interface WireClient {

    /**
     * Obtains user information for the given userId.
     */
    suspend fun getUser(userId: UUID): User
}
