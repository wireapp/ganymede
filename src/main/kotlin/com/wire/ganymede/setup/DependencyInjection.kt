package com.wire.ganymede.setup

import com.wire.ganymede.internal.WireInternalClient
import com.wire.ganymede.swisscom.SigningService
import com.wire.ganymede.swisscom.SwisscomClient
import io.ktor.client.HttpClient
import io.ktor.util.KtorExperimentalAPI
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import mu.KLogger
import mu.KLogging
import org.kodein.di.Kodein.MainBuilder
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

@KtorExperimentalAPI
fun MainBuilder.configureContainer() {

    bind<HttpClient>() with singleton {
        client(instance())
    }

    bind<PrometheusMeterRegistry>() with singleton {
        PrometheusMeterRegistry(PrometheusConfig.DEFAULT).apply {
            with(this.config()) {
                commonTags("application", "GANYMEDE")
            }
        }
    }

    bind<WireInternalClient>() with singleton { WireInternalClient(instance(), instance()) }

    bind<SigningService>() with singleton { SigningService(instance(), instance()) }
    bind<SwisscomClient>() with singleton { SwisscomClient(instance(), instance()) }

    bind<KLogger>("routing-logger") with singleton { KLogging().logger("Routing") }
    bind<KLogger>("install-logger") with singleton { KLogging().logger("KtorStartup") }
}
