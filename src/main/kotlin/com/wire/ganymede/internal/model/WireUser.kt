package com.wire.ganymede.internal.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import java.util.UUID

/**
 * Code taken from Java repo. Maybe to be cleaned up in the future.
 * */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class User(
    val id: UUID,
    val email: String,
    val name: String,
    val phone: String,

    val firstname: String? = null,
    val lastname: String? = null,
    val country: String? = null,
    val locale: String = "en-US"
)
