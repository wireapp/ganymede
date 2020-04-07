package com.wire.bots.polls.setup

import com.wire.bots.polls.dao.PollRepository
import com.wire.bots.polls.parser.InputParser
import com.wire.bots.polls.parser.PollFactory
import com.wire.bots.polls.parser.PollValidation
import com.wire.bots.polls.services.AuthService
import com.wire.bots.polls.services.ConversationService
import com.wire.bots.polls.services.MessagesHandlingService
import com.wire.bots.polls.services.PollService
import com.wire.bots.polls.services.ProxySenderService
import com.wire.bots.polls.services.StatsFormattingService
import com.wire.bots.polls.services.UserCommunicationService
import com.wire.bots.polls.websockets.PollWebSocket
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
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

@KtorExperimentalAPI
fun MainBuilder.configureContainer() {

    bind<PollValidation>() with singleton { PollValidation() }

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

    bind<PollWebSocket>() with singleton {
        PollWebSocket(
            client = instance(),
            config = instance(),
            handler = instance()
        )
    }

    bind<ProxySenderService>() with singleton {
        ProxySenderService(
            client = instance(),
            config = instance()
        )
    }

    bind<InputParser>() with singleton { InputParser() }

    bind<PollFactory>() with singleton { PollFactory(instance(), instance()) }

    bind<PollRepository>() with singleton { PollRepository() }

    bind<PollService>() with singleton { PollService(instance(), instance(), instance(), instance(), instance(), instance()) }

    bind<UserCommunicationService>() with singleton { UserCommunicationService(instance(), instance("version")) }

    bind<ConversationService>() with singleton { ConversationService(instance(), instance()) }

    bind<MessagesHandlingService>() with singleton { MessagesHandlingService(instance(), instance()) }

    bind<AuthService>() with singleton {
        AuthService(proxyToken = instance("proxy-auth"))
    }

    bind<StatsFormattingService>() with singleton { StatsFormattingService(instance()) }

    bind<KLogger>("routing-logger") with singleton { KLogging().logger("Routing") }
    bind<KLogger>("install-logger") with singleton { KLogging().logger("KtorStartup") }
}
