package com.wire.ganymede.setup

import com.fasterxml.jackson.databind.SerializationFeature
import com.wire.ganymede.routing.registerRoutes
import com.wire.ganymede.setup.auth.AUTH_HEADER
import com.wire.ganymede.setup.exceptions.registerExceptionHandlers
import com.wire.ganymede.setup.logging.USER_ID
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallId
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.callIdMdc
import io.ktor.jackson.jackson
import io.ktor.metrics.micrometer.MicrometerMetrics
import io.ktor.request.header
import io.ktor.routing.routing
import io.ktor.util.KtorExperimentalAPI
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import mu.KLogger
import mu.KLogging
import org.kodein.di.LazyKodein
import org.kodein.di.generic.instance
import org.kodein.di.ktor.kodein
import org.slf4j.event.Level
import java.text.DateFormat


/**
 * Loads the application.
 */
@KtorExperimentalAPI
fun Application.init() {
    setupKodein()
    // now kodein is running and can be used
    val k by kodein()
    val logger by k.instance<KLogger>("install-logger")
    logger.debug { "DI container started." }

    // configure Ktor
    installFrameworks(k)

    // register routing
    routing {
        registerRoutes(k)
    }
}

/**
 * Configure Ktor and install necessary extensions.
 */
fun Application.installFrameworks(k: LazyKodein) {
    install(ContentNegotiation) {
        jackson {
            // enable pretty print for JSONs
            enable(SerializationFeature.INDENT_OUTPUT)
            dateFormat = DateFormat.getDateTimeInstance()
        }
    }

    install(DefaultHeaders)

    install(CallLogging) {
        level = Level.DEBUG
        logger = KLogging().logger("com.wire.HttpCallLogger")
        callIdMdc(USER_ID)
    }

    install(CallId) {
        retrieve { call ->
            call.request.header(AUTH_HEADER)
        }
    }

    registerExceptionHandlers()

    val prometheusRegistry by k.instance<PrometheusMeterRegistry>()
    install(MicrometerMetrics) {
        registry = prometheusRegistry
        distributionStatisticConfig = DistributionStatisticConfig.Builder()
            .percentilesHistogram(true)
            .build()
    }
}
