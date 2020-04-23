package com.wire.ganymede.routing

import com.wire.ganymede.utils.createLogger
import io.ktor.routing.Routing
import org.kodein.di.LazyKodein

internal val routingLogger by lazy { createLogger("RoutingLogger") }

/**
 * Register routes to the KTor.
 */
fun Routing.registerRoutes(k: LazyKodein) {
    serviceRoutes(k)
    signingRoute(k)
}
