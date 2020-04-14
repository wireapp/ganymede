package com.wire.ganymede.swisscom

import ai.blindspot.ktoolz.extensions.hashWith256

/**
 * Creates hash from given resource.
 */
fun Any.hashResource(documentName: String): String {
    val bytes = this::class.java.getResourceAsStream(documentName).readBytes()
    return hashWith256(bytes)
}
