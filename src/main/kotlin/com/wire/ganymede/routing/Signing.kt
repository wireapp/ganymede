package com.wire.ganymede.routing

import ai.blindspot.ktoolz.extensions.isUUID
import com.wire.ganymede.routing.requests.SignRequest
import com.wire.ganymede.setup.auth.userUuid
import com.wire.ganymede.swisscom.SigningService
import io.ktor.application.call
import io.ktor.features.BadRequestException
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.post
import org.kodein.di.LazyKodein
import org.kodein.di.generic.instance
import java.util.UUID

/**
 * Register routes to the KTor.
 */
fun Routing.signingRoute(k: LazyKodein) {

    val service by k.instance<SigningService>()

    /**
     * Sign request.
     */
    post("/request") {
        val userId = userUuid()

        runCatching {
            call.receive<SignRequest>()
        }.onFailure {
            routingLogger.warn(it) { "It was not possible tor received SignRequest." }
            throw BadRequestException("Malformed SignRequest.")
        }.onSuccess {
            val response = service.sign(
                userId = userId,
                documentId = it.documentId,
                documentHash = it.hash,
                documentName = it.name
            )
            call.respond(response)
        }
    }

    /**
     * Sign request.
     */
    post("/pending/{responseId}") {
        userUuid()

        val responseId = call.parameters["responseId"]
        when {
            responseId == null -> throw BadRequestException("No response ID provided.")
            !isUUID(responseId) -> throw BadRequestException("Provided responseId ($responseId) is not valid UUID.")
            else -> {
                val response = service.pending(UUID.fromString(responseId))
                call.respond { response }
            }
        }
    }
}
