package com.wire.ganymede.swisscom

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.runBlocking
import mu.KLogging
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class OtherIntegrationTests : KodeinTestBase() {

    private companion object : KLogging()

    @Test
    @Ignore("integration test")
    fun `test certificate`() {
        val client = instance<HttpClient>()
        val response = runBlocking {
            client.get<String>("https://server.cryptomix.com/secure/")
        }
        assertNotNull(response)
        logger.info { response }
        assertTrue { response.contains("SSL Authentication OK!") }

    }
}
