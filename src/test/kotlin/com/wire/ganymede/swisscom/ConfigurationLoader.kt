package com.wire.ganymede.swisscom

import com.wire.ganymede.internal.model.User
import com.wire.ganymede.setup.bindConfiguration
import com.wire.ganymede.setup.configureContainer
import org.kodein.di.Kodein
import java.util.Properties
import java.util.UUID

class ConfigurationLoader {

    val k = Kodein.direct {
        bindConfiguration()
        configureContainer()
    }

    val testingUser by lazy {
        User(
            id = UUID.fromString(getProp("user.id")),
            name = getProp("user.name"),
            email = getProp("user.email"),
            phoneNo = getProp("user.phoneNo"),
            locale = getProp("user.locale"),
            country = getProp("user.country")
        )
    }

    val documentId: String by lazy {
        UUID.randomUUID().toString()
    }

    private val props by lazy {
        Properties().also {
            val resource = ConfigurationLoader::class.java.getResourceAsStream("testing.properties")
            requireNotNull(resource) { "File testing.properties must be present for correct integration testing!" }
            it.load(resource)
        }
    }

    private fun getProp(name: String) = requireNotNull(props.getProperty(name)) { "Missing property $name." }
}
