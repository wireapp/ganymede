package com.wire.ganymede.routing.auth

import ai.blindspot.ktoolz.extensions.isUUID
import com.wire.ganymede.setup.exceptions.UnauthorizedException
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.request.header
import io.ktor.util.pipeline.PipelineContext
import java.util.UUID

/**
 * Obtains UUID of the user from the header. If no user UUID is found, this function responds with unauthorized.
 */
fun PipelineContext<Unit, ApplicationCall>.userUuid(): UUID {
    val zuid = call.request.header("zuid")
    return when {
        zuid == null -> throw UnauthorizedException("Header zuid not present.")
        !isUUID(zuid) -> throw UnauthorizedException("Provided zuid is not valid UUID.")
        else -> UUID.fromString(zuid)
    }
}
