package com.wire.ganymede.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.jetbrains.annotations.NotNull
import java.util.ArrayList
import java.util.UUID

/**
 * Code taken from Java repo. Maybe to be cleaned up in the future.
 * */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class User {
    @NotNull
    var id: UUID? = null

    @NotNull
    var email: String? = null

    @NotNull
    var name: String? = null

    @NotNull
    var phone: String? = null
    var firstname: String? = null
    var lastname: String? = null
    var country: String? = null

    @JsonProperty("accent_id")
    var accent = 0
    var locale = "en-US"

    @JsonProperty("managed_by")
    var managed = "scim"

    @get:JsonProperty
    val handle: String
        get() = name!!.toLowerCase().replace(" ", "")

    @JsonProperty
    fun setHandle() {
    }

    var assets = ArrayList<UserAsset>()

    @JsonProperty("picture")
    var dummy = ArrayList<String>()

    class UserAsset {
        var size: String? = null
        var key: String? = null
        var type = "image"
    }
}
