package com.wire.ganymede.internal

import com.wire.ganymede.internal.model.User
import mu.KLogging
import java.util.Properties
import java.util.UUID

/**
 * Mock mock client used for testing, every time returns same user.
 */
class DisconnectedWireClient : WireClient {

    private companion object : KLogging()

    /**
     * Returns predefined user.
     */
    override suspend fun getUser(userId: UUID): User {
        logger.warn { "Using mocked wire client! This configuration can not be used in the production environment!" }
        if (userId != testingUser.id) {
            throw IllegalArgumentException("Testing user id was expected!")
        }
        return testingUser
    }

    private val testingUser by lazy {
        User(
            id = UUID.fromString(getProp("user.id")),
            name = getProp("user.name"),
            email = getProp("user.email"),
            phoneNo = getProp("user.phoneNo"),
            locale = getProp("user.locale"),
            country = getProp("user.country")
        )
    }

    private fun getProp(name: String) = requireNotNull(props.getProperty(name)) { "Missing property $name." }

    private val props by lazy {
        Properties().also {
            val resource = this::class.java.getResourceAsStream("testing.properties")
            // as testing.properties are in the gitignore and we are deploying JAR using pipelines,
            // this should throw an exception when running in production with this mocked client
            requireNotNull(resource) { "File testing.properties must be present for correct integration testing!" }
            it.load(resource)
        }
    }

}
