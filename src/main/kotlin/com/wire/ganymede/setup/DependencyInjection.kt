package com.wire.ganymede.setup

import com.wire.ganymede.internal.DisconnectedWireClient
import com.wire.ganymede.internal.WireClient
import com.wire.ganymede.internal.WireInternalClient
import com.wire.ganymede.swisscom.SigningService
import com.wire.ganymede.swisscom.SwisscomClient
import com.wire.ganymede.utils.createLogger
import io.ktor.client.HttpClient
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import mu.KLogger
import org.kodein.di.Kodein.MainBuilder
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

fun MainBuilder.configureContainer() {

    bind<HttpClient>() with singleton {
        client(instance(), instance())
    }

    bind<PrometheusMeterRegistry>() with singleton {
        PrometheusMeterRegistry(PrometheusConfig.DEFAULT).apply {
            with(this.config()) {
                commonTags("application", "ganymede")
            }
        }
    }

    bind<WireInternalClient>() with singleton { WireInternalClient(instance(), instance()) }
    bind<DisconnectedWireClient>() with singleton { DisconnectedWireClient() }

    bind<WireClient>() with singleton { instance<WireInternalClient>() }

    bind<SigningService>() with singleton { SigningService(instance(), instance()) }
    bind<SwisscomClient>() with singleton { SwisscomClient(instance(), instance()) }

    bind<KLogger>("routing-logger") with singleton { createLogger("Routing") }
    bind<KLogger>("install-logger") with singleton { createLogger("KtorStartup") }
}
