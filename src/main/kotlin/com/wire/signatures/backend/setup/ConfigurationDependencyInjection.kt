package com.wire.signatures.backend.setup

import ai.blindspot.ktoolz.extensions.getEnv
import ai.blindspot.ktoolz.extensions.whenNull
import com.wire.signatures.backend.dto.conf.DatabaseConfiguration
import com.wire.signatures.backend.setup.EnvConfigVariables.DB_PASSWORD
import com.wire.signatures.backend.setup.EnvConfigVariables.DB_URL
import com.wire.signatures.backend.setup.EnvConfigVariables.DB_USER
import mu.KLogging
import org.kodein.di.Kodein.MainBuilder
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton
import java.io.File

private val logger = KLogging().logger("EnvironmentLoaderLogger")

private fun getEnvOrLogDefault(env: String, defaultValue: String) = getEnv(env).whenNull {
    logger.warn { "Env variable $env not set! Using default value - $defaultValue" }
} ?: defaultValue


@Suppress("SameParameterValue") // we don't care...
private fun loadVersion(defaultVersion: String): String = runCatching {
    getEnv("RELEASE_FILE_PATH")
        ?.let { File(it).readText().trim() }
        ?: defaultVersion
}.getOrNull() ?: defaultVersion

/**
 * Loads the DI container with configuration from the system environment.
 */
fun MainBuilder.bindConfiguration() {

    // The default values used in this configuration are for the local development.

    bind<DatabaseConfiguration>() with singleton {
        DatabaseConfiguration(
            userName = getEnvOrLogDefault(DB_USER, "wire-digital-signatures"),
            password = getEnvOrLogDefault(DB_PASSWORD, "super-secret-wire-pwd"),
            url = getEnvOrLogDefault(
                DB_URL,
                "jdbc:postgresql://localhost:5432/digi-sign"
            )
        )
    }

    bind<String>("version") with singleton {
        loadVersion("development")
    }
}
