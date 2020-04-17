# Ganymede - Digital signatures Backend
![CI/CD](https://github.com/wireapp/ganymede/workflows/CI/CD/badge.svg)

Ganymede is a stateless microservice wrapper around Swisscom API and Wire internal API used for 
signing digital documents.

## Dev Stack
* HTTP Server - [Ktor](https://ktor.io/)
* HTTP Client - [Apache](https://ktor.io/clients/http-client/engines.html) under [Ktor](https://ktor.io/)
* Dependency Injection - [Kodein](https://github.com/Kodein-Framework/Kodein-DI)
* Build system - [Gradle](https://gradle.org/)

## Usage
* To run the application simply execute `make run` or `./gradlew run`.
* To run the application inside the docker compose environment run `make up`

For more details see [Makefile](Makefile).

## Configuration
Configuration can be loaded either from properties file or from the environment.
If the configuration is loaded from the properties file, it can still be overwritten by the env variables.

* To load properties file, set environment variable `PROPS_PATH` to full path to properties file.
* To load configuration from environment, one must set all environment variables.

Thanks to this, one can use common `.env` file for the Docker environment as well as for the local development.

<details><summary>Example .env</summary>
<p>

```bash
# Swisscom API
SWISSCOM_API_BASE_URL=https://ais.swisscom.com/AIS-Server/rs/v1.0
SWISSCOM_API_SIGN_PATH=/sign
SWISSCOM_API_PENDING_PATH=/pending

# Store configuration properties
STORE_PATH=/root/certificates/aves.jks
STORE_PASS=super-strong-password
STORE_TYPE=JKS

# Wire API
WIRE_API_BASE_URL=
WIRE_API_USERS_PATH=i/users
```

</p>
</details>

<details><summary>Complete List of configuration</summary>
<p>

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

See [complete list](src/main/kotlin/com/wire/ganymede/setup/EnvConfigVariables.kt).
</p>
</details>


## Docker Compose
To run bot inside docker compose environment,
please create `.env` file in the root directory with the variables described in the previous section.
