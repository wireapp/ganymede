package com.wire.ganymede.setup

import io.ktor.client.features.logging.Logger
import org.slf4j.LoggerFactory

/**
 * Debug logger for HTTP requests.
 */
val Logger.Companion.DEBUG: Logger
    get() = object : Logger {
        private val delegate = LoggerFactory.getLogger("com.wire.HttpCallsLogging")!!
        override fun log(message: String) {
            delegate.debug(message)
        }
    }

