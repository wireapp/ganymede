package com.wire.ganymede.swisscom

import ai.blindspot.ktoolz.extensions.hashWith256
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.runBlocking
import mu.KLogging
import org.kodein.di.generic.instance
import java.util.UUID
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertNotNull

@Ignore("integration tests")
class SwisscomClientTest {

    private companion object : KLogging()

    private val c = ConfigurationLoader()

    @Test
    fun `load file to hash`() {
        assertNotNull(hashResource("TestingFile.pdf"))
    }

    @Test
    fun `test certificate`() {
        val client = c.k.instance<HttpClient>()
        val response = runBlocking {
            client.get<String>("https://server.cryptomix.com/secure/")
        }
        assertNotNull(response)
        logger.info { response }
    }

    @Test
    fun `test pending method`() {
        val client = c.k.instance<SwisscomClient>()
        val id = UUID.fromString("84abdf22-ab5a-4805-b919-b5d331c5d7e6")
        val response = runBlocking {
            client.pending(id)
        }
        assertNotNull(response)
    }

    @Test
    fun `test signing document`() {
        val client = c.k.instance<SwisscomClient>()
        val user = c.testingUser
        val documentName = "TestingFile.pdf"
        val documentHash = hashResource(documentName)

        val response = runBlocking {
            client.sign(signer = user, documentId = c.documentId, documentHash = documentHash, documentName = documentName)
        }

        logger.info { response }
    }
}


fun Any.hashResource(documentName: String): String {
    val bytes = this::class.java.getResourceAsStream(documentName).readBytes()
    return hashWith256(bytes)
}
