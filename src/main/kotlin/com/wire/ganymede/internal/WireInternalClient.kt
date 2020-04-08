package com.wire.ganymede.internal

import com.wire.ganymede.dto.User
import java.util.UUID

class WireInternalClient {
    //TODO implement this
    suspend fun getUser(id: UUID): User = User()
}
