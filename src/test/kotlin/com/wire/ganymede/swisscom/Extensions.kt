package com.wire.ganymede.swisscom

import ai.blindspot.ktoolz.extensions.hashWith256
import mu.KLogging
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Creates hash from given resource.
 */
fun Any.hashResource(documentName: String): String {
    val bytes = this::class.java.getResourceAsStream(documentName).readBytes()
    return hashWith256(bytes)
}

class ExtensionsTests {

    private companion object : KLogging()

    @Test
    fun `load file to hash`() {
        val hash = hashResource("TestingFile.pdf")
        assertTrue { hash.isNotEmpty() }
        logger.info { hash }
    }
}
