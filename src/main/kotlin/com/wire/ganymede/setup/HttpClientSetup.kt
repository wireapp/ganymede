package com.wire.ganymede.setup

import com.wire.ganymede.dto.KeyStoreConfiguration
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.apache.Apache
import io.ktor.client.engine.apache.ApacheEngineConfig
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import mu.KLogging
import org.apache.http.ssl.SSLContextBuilder
import java.io.File
import java.security.KeyStore


private val logger = KLogging().logger("HttpClientConfiguration")

/**
 * Tries to read and create key store.
 */
fun readStore(config: KeyStoreConfiguration): KeyStore? =
    runCatching {
        File(config.storePath).inputStream().use {
            KeyStore.getInstance(config.storeType).apply {
                load(it, config.storePass.toCharArray())
            }
        }
    }.onFailure {
        logger.error(it) { "It was not possible to load key store!" }
    }.onSuccess {
        logger.debug { "KeyStore loaded." }
    }.getOrNull()


/**
 * Prepares client engine and sets certificates from [config].
 */
fun HttpClientConfig<ApacheEngineConfig>.configureCertificates(config: KeyStoreConfiguration) {
    engine {
        customizeClient {
            setSSLContext(
                SSLContextBuilder
                    .create()
                    .loadKeyMaterial(readStore(config), config.keyPass.toCharArray())
                    .build()
            )
        }
    }
}

/**
 * Prepares HTTP Client with given keystore.
 */
fun client(config: KeyStoreConfiguration? = null) =
    HttpClient(Apache) {
        install(JsonFeature) {
            serializer = JacksonSerializer()
        }

        install(Logging) {
            this.level
            logger = Logger.DEBUG
            level = LogLevel.ALL
        }

        if (config != null) configureCertificates(config)
    }
