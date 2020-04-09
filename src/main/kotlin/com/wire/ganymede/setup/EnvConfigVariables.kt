package com.wire.ganymede.setup

/**
 * Contains variables that are loaded from the environment.
 */
enum class EnvConfigVariables {
    /**
     * Password for the key store.
     */
    STORE_PASS,

    /**
     * Path to the keystore.
     */
    STORE_PATH,

    /**
     * Type of the store, JKS for example
     */
    STORE_TYPE,

    /**
     * Password for key.
     */
    KEY_PASS,

    /**
     * Base URL for the internal Wire BE.
     */
    WIRE_API_BASE_URL,

    /**
     * Path to users API.
     */
    WIRE_API_USERS_PATH,

    /**
     * Base URL for Swisscom API.
     */
    SWISSCOM_API_BASE_URL,

    /**
     * Swisscom API sign path.
     */
    SWISSCOM_API_SIGN_PATH,

    /**
     * Swisscom pending path.
     */
    SWISSCOM_API_PENDING_PATH
}
