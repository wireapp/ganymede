package com.wire.ganymede.swisscom

import ai.blindspot.ktoolz.extensions.hashWith256
import kotlin.test.Test
import kotlin.test.assertNotNull

/**
 * Creates hash from given resource.
 */
fun Any.hashResource(documentName: String): String {
    val bytes = this::class.java.getResourceAsStream(documentName).readBytes()
    return hashWith256(bytes)
}

class ExtensionsTests {
    @Test
    fun `load file to hash`() {
        assertNotNull(hashResource("TestingFile.pdf"))
    }
}
