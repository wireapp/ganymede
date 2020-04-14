package com.wire.ganymede.setup

import ai.blindspot.ktoolz.extensions.getEnv
import ai.blindspot.ktoolz.extensions.whenNull
import com.wire.ganymede.dto.KeyStoreConfiguration
import com.wire.ganymede.internal.model.WireAPIConfig
import com.wire.ganymede.swisscom.model.SwisscomAPIConfig
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
fun MainBuilder.bindConfiguration(defaultProperties: Properties? = null) {
    val props = defaultProperties ?: loadProperties(getEnv("PROPS_PATH"))

    bind<KeyStoreConfiguration>() with singleton {
        val storePass = loadConfiguration(EnvConfigVariables.STORE_PASS, props)
        KeyStoreConfiguration(
            storePath = loadConfiguration(EnvConfigVariables.STORE_PATH, props),
            storePass = storePass,
            storeType = loadConfiguration(EnvConfigVariables.STORE_TYPE, props),
            keyPass = loadConfiguration(EnvConfigVariables.KEY_PASS, props, storePass)
        )
    }

    bind<WireAPIConfig>() with singleton {
        WireAPIConfig(
            baseUrl = loadConfiguration(EnvConfigVariables.WIRE_API_BASE_URL, props),
            userPath = loadConfiguration(EnvConfigVariables.WIRE_API_USERS_PATH, props, "i/users")
        )
    }

    bind<SwisscomAPIConfig>() with singleton {
        SwisscomAPIConfig(
            baseUrl = loadConfiguration(
                EnvConfigVariables.SWISSCOM_API_BASE_URL, props,
                "https://ais.swisscom.com/AIS-Server/rs/v1.0"
            ),
            signPath = loadConfiguration(
                EnvConfigVariables.SWISSCOM_API_SIGN_PATH, props,
                "/sign"
            ),
            pendingPath = loadConfiguration(
                EnvConfigVariables.SWISSCOM_API_PENDING_PATH, props,
                "/pending"
            )
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
    (getEnv(env.name).whenNull {
        logger.info { "Env variable ${env.name} not set. Loading from props." }
    } ?: props.getProperty(env.name, defaultValue))
        .also {
            if (it.isBlank()) {
                logger.warn { "No value set for configuration ${env.name}!" }
            }
        }


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
