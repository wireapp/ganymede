package com.wire.signatures.backend.setup

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.client.features.websocket.WebSockets
import io.ktor.util.KtorExperimentalAPI
import mu.KLogger
import mu.KLogging
import org.kodein.di.Kodein.MainBuilder
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton

@KtorExperimentalAPI
fun MainBuilder.configureContainer() {

    bind<HttpClient>() with singleton {
        HttpClient(CIO) {
            install(WebSockets)

            install(JsonFeature) {
                serializer = JacksonSerializer()
            }

            install(Logging) {
                this.level
                logger = Logger.DEBUG
                level = LogLevel.ALL
            }
        }
    }

    bind<KLogger>("routing-logger") with singleton { KLogging().logger("Routing") }
    bind<KLogger>("install-logger") with singleton { KLogging().logger("KtorStartup") }
}
