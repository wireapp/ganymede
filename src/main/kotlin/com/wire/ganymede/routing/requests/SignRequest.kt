package com.wire.ganymede.routing.requests

/**
 * Request to sign document.
 */
data class SignRequest(
    val documentId: String,
    val name: String,
    val hash: String
)
