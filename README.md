# Ganymede - Digital signatures Backend
![CI/CD](https://github.com/wireapp/ganymede/workflows/CI/CD/badge.svg)

## Dev Stack
* HTTP Server - [Ktor](https://ktor.io/)
* HTTP Client - [Apache](https://ktor.io/clients/http-client/engines.html) under [Ktor](https://ktor.io/)
* Dependency Injection - [Kodein](https://github.com/Kodein-Framework/Kodein-DI)
* Build system - [Gradle](https://gradle.org/)

## Usage
* To run the application simply execute `make run` or `./gradlew run`.
* To run the application inside the docker compose environment run `make up`

For more details see [Makefile](Makefile).


## Bot configuration
Configuration is currently being loaded from the environment variables.

```kotlin
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
```

Via the system variables - see [complete list](src/main/kotlin/com/wire/ganymede/setup/EnvConfigVariables.kt).

## Docker Compose
To run bot inside docker compose environment,
please create `.env` file in the root directory with the variables described in the previous section.
