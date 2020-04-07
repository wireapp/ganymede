package com.wire.signatures.backend.setup

/**
 * Contains variables that are loaded from the environment.
 */
object EnvConfigVariables {
    /**
     * Username for the database.
     */
    const val DB_USER = "DB_USER"

    /**
     * Password for the database.
     */
    const val DB_PASSWORD = "DB_PASSWORD"

    /**
     * URL for the database.
     *
     * Example:
     * `jdbc:postgresql://localhost:5432/bot-database`
     */
    const val DB_URL = "DB_URL"
}
