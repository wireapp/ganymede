package com.wire.ganymede.swisscom

import kotlinx.coroutines.runBlocking
import mu.KLogging
import pw.forst.tools.katlib.prettyPrintJson
import java.util.UUID
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertNotNull

@Ignore("integration tests")
class SwisscomClientTest : KodeinTestBase() {

    private companion object : KLogging()

    private val preparedData = PreparedTestingData()

    @Test
    fun `test signing document`() {
        val client = instance<SwisscomClient>()
        val user = preparedData.testingUser
        val documentName = "TestingFile.pdf"
        val documentHash = hashResource(documentName)

        val (signResponse, rawData) = runBlocking {
            client.sign(signer = user, documentId = preparedData.documentId, documentHash = documentHash, documentName = documentName)
        }

        logger.info { "\n\n${prettyPrintJson(rawData)}\n" }
        assertNotNull(signResponse)
    }

    @Test
    fun `test pending method`() {
        val client = instance<SwisscomClient>()
        // get id of the request from the second test
        val id = UUID.fromString("84abdf22-ab5a-4805-b919-b5d331c5d7e6")
        val (signResponse, rawData) = runBlocking {
            client.pending(id)
        }

        logger.info { "\n\n${prettyPrintJson(rawData)}\n" }
        assertNotNull(signResponse)
    }

}
