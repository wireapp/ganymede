package com.wire.ganymede.internal.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.UUID


/**
 * Code taken from Java repo. Maybe to be cleaned up in the future.
 * */

@JsonIgnoreProperties(ignoreUnknown = true)
data class User(
    val id: UUID,

    val name: String?,

    val email: String,

    val phoneNo: String?,

    val handle: String?,

    val locale: String = "en-US",

    val country: String = "CH"
) {
    val firstName: String?
        get() = name?.split(' ')?.first()

    val lastName: String?
        get() = name?.split(' ')?.last()
}
