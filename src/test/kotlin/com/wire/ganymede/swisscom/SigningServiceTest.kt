package com.wire.ganymede.swisscom

import ai.blindspot.ktoolz.extensions.createJson
import ai.blindspot.ktoolz.extensions.prettyPrintJson
import kotlinx.coroutines.runBlocking
import mu.KLogging
import java.util.UUID
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore("integration tests")
class SigningServiceTest : KodeinTestBase() {

    private companion object : KLogging()

    private val preparedData = PreparedTestingData()

    @Test
    fun `test signing document`() {
        val service = instance<SigningService>()

        val user = preparedData.testingUser
        val documentName = "TestingFile.pdf"
        val documentHash = hashResource(documentName)

        val response = runBlocking {
            service.sign(
                user = user,
                documentId = preparedData.documentId,
                documentHash = documentHash,
                documentName = documentName
            )
        }

        logger.info { "\n\n${prettyPrintJson(createJson(response))}\n" }
    }

    @Test
    fun `test pending document`() {
        val service = instance<SigningService>()

        val operationId = UUID.fromString("") // fill operation Id from the previous test
        val response = runBlocking {
            service.pending(operationId)
        }

        logger.info { "\n\n${prettyPrintJson(createJson(response))}\n" }
    }
}
