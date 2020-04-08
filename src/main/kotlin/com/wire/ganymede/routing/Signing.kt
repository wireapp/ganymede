package com.wire.ganymede.routing

import ai.blindspot.ktoolz.extensions.isUUID
import com.wire.ganymede.routing.requests.SignRequest
import com.wire.ganymede.swisscom.SigningService
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
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
        runCatching {
            call.receive<SignRequest>()
        }.onFailure {
            call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Malformed SignRequest."))
        }.onSuccess {
            val response = service.sign(
                userId = UUID.fromString(""), // TODO load that from cookie
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
        val responseId = call.parameters["responseId"]

        when {
            responseId == null -> call.respond(status = HttpStatusCode.BadRequest) {
                mapOf("message" to "No response ID provided.")
            }
            !isUUID(responseId) -> call.respond(status = HttpStatusCode.BadRequest) {
                mapOf("message" to "Provided responseId ($responseId) is not valid UUID.")
            }
            else -> {
                val response = service.pending(UUID.fromString(responseId))
                call.respond { response }
            }
        }
    }
}
