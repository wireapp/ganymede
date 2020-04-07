package com.wire.ganymede.setup

import ai.blindspot.ktoolz.extensions.getEnv
import ai.blindspot.ktoolz.extensions.whenNull
import com.wire.ganymede.dto.KeyStoreConfiguration
import mu.KLogging
import org.kodein.di.Kodein.MainBuilder
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton
import java.io.File

private val logger = KLogging().logger("EnvironmentLoaderLogger")

private fun loadConfiguration(env: EnvConfigVariables, defaultValue: String = "") = getEnv(env.name).whenNull {
    logger.warn { "Env variable ${env.name} not set! Using default value - $defaultValue" }
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

    bind<KeyStoreConfiguration>("key-store-config") with singleton {
        KeyStoreConfiguration(
            storePath = loadConfiguration(EnvConfigVariables.STORE_PATH),
            storePass = loadConfiguration(EnvConfigVariables.STORE_PASS),
            storeType = loadConfiguration(EnvConfigVariables.STORE_TYPE),
            keyPass = loadConfiguration(EnvConfigVariables.KEY_PASS)
        )
    }

    // The default values used in this configuration are for the local development.
    bind<String>("version") with singleton {
        loadVersion("development")
    }
}
