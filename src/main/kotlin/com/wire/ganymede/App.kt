package com.wire.ganymede

import com.wire.ganymede.setup.init
import io.ktor.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.util.KtorExperimentalAPI

@KtorExperimentalAPI
fun main() {
    embeddedServer(Netty, 8080, module = Application::init).start()
}
