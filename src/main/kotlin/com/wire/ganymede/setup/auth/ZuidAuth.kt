package com.wire.ganymede.setup.auth

import com.wire.ganymede.setup.exceptions.UnauthorizedException
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.request.header
import io.ktor.util.pipeline.PipelineContext
import pw.forst.tools.katlib.isUUID
import java.util.UUID

/**
 * Obtains UUID of the user from the header. If no user UUID is found, this function responds with unauthorized.
 */
fun PipelineContext<Unit, ApplicationCall>.userUuid(): UUID {
    val userId = call.request.header(AUTH_HEADER)
    return when {
        userId == null -> throw UnauthorizedException("Header $AUTH_HEADER not present.")
        !isUUID(userId) -> throw UnauthorizedException("Provided $AUTH_HEADER is not valid UUID.")
        else -> UUID.fromString(userId)
    }
}
