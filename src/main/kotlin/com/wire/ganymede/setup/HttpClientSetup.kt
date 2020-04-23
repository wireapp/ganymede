package com.wire.ganymede.setup

import com.wire.ganymede.dto.KeyStoreConfiguration
import com.wire.ganymede.utils.createLogger
import com.wire.ganymede.utils.httpCall
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.apache.Apache
import io.ktor.client.engine.apache.ApacheEngineConfig
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.client.features.observer.ResponseObserver
import io.micrometer.core.instrument.MeterRegistry
import org.apache.http.ssl.SSLContextBuilder
import java.io.File
import java.security.KeyStore


private val logger = createLogger("HttpClientConfiguration")

/**
 * Prepares HTTP Client with given keystore.
 */
fun client(config: KeyStoreConfiguration, meterRegistry: MeterRegistry) =
    HttpClient(Apache) {
        install(JsonFeature) {
            serializer = JacksonSerializer()
        }

// TODO check https://github.com/ktorio/ktor/issues/1813
//        install(ResponseObserver) {
//            onResponse {
//                meterRegistry.httpCall(it)
//            }
//        }

        install(Logging) {
            logger = Logger.TRACE
            level = LogLevel.ALL
        }

        configureCertificates(config)
    }

/**
 * Tries to read and create key store.
 */
private fun readStore(config: KeyStoreConfiguration): KeyStore? =
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
private fun HttpClientConfig<ApacheEngineConfig>.configureCertificates(config: KeyStoreConfiguration) {
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
 * Debug logger for HTTP requests.
 */
private val Logger.Companion.DEBUG: Logger
    get() = object : Logger, org.slf4j.Logger by createLogger("HttpCallsLogging") {
        override fun log(message: String) {
            debug(message)
        }
    }

/**
 * Trace logger for HTTP Requests.
 */
private val Logger.Companion.TRACE: Logger
    get() = object : Logger, org.slf4j.Logger by createLogger("HttpCallsLogging") {
        override fun log(message: String) {
            trace(message)
        }
    }

