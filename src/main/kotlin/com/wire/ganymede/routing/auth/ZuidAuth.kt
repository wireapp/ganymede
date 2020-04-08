package com.wire.ganymede.routing.auth

import ai.blindspot.ktoolz.extensions.isUUID
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.header
import io.ktor.response.respond
import io.ktor.util.pipeline.PipelineContext
import java.util.UUID

/**
 * Obtains UUID of the user from the header. If no user UUID is found, this function responds with unauthorized.
 */
suspend fun PipelineContext<Unit, ApplicationCall>.userUuid(): UUID? {
    val zuid = call.request.header("zuid")
    when {
        zuid == null ->
            call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "Header zuid not present."))
        !isUUID(zuid) ->
            call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "Provided zuid is not valid UUID."))
        else -> return UUID.fromString(zuid)
    }
    return null
}
