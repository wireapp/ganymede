package com.wire.signatures.backend.dto

/**
 * Configuration for the key store.
 */
data class KeyStoreConfiguration(
    val storePass: String,
    val storePath: String,
    val storeType: String,
    val keyPass: String
)
