package com.wire.ganymede.internal.model

import java.util.UUID

data class SignResponse(
    val responseId: UUID,
    val consentURL: String
)

data class Signature(
    val documentId: String,
    val cms: String,
    val serialNumber: String?
)
