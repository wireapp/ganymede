package com.wire.ganymede.setup

import ai.blindspot.ktoolz.extensions.getEnv
import ai.blindspot.ktoolz.extensions.whenNull
import com.wire.ganymede.dto.KeyStoreConfiguration
import mu.KLogging
import org.kodein.di.Kodein.MainBuilder
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton
import java.io.File
import java.util.Properties

private val logger = KLogging().logger("EnvironmentLoaderLogger")

/**
 * Loads the DI container with configuration from the system environment.
 */
fun MainBuilder.bindConfiguration() {
    val props = loadProperties(getEnv("PROPS_PATH"))

    bind<KeyStoreConfiguration>() with singleton {
        KeyStoreConfiguration(
            storePath = loadConfiguration(EnvConfigVariables.STORE_PATH, props),
            storePass = loadConfiguration(EnvConfigVariables.STORE_PASS, props),
            storeType = loadConfiguration(EnvConfigVariables.STORE_TYPE, props)
        )
    }

    // The default values used in this configuration are for the local development.
    bind<String>("version") with singleton {
        loadVersion("development")
    }
}

/**
 * Loads configuration either from the env variable or from the properties.
 */
private fun loadConfiguration(
    env: EnvConfigVariables,
    props: Properties = Properties(),
    defaultValue: String = ""
): String =
    getEnv(env.name).whenNull {
        logger.info { "Env variable ${env.name} not set. Loading from props." }
    } ?: props.getProperty(env.name, defaultValue)


@Suppress("SameParameterValue") // we don't care...
private fun loadVersion(defaultVersion: String): String = runCatching {
    getEnv("RELEASE_FILE_PATH")
        ?.let { File(it).readText().trim() }
        ?: defaultVersion
}.getOrNull() ?: defaultVersion

/**
 * Reads properties from the given path.
 */
private fun loadProperties(path: String?): Properties = Properties().apply {
    if (path != null) {
        File(path)
            .takeIf { it.exists() }
            ?.let {
                load(it.inputStream()).also {
                    logger.info { "Properties file found, props loaded." }
                }
            }
    }
}
