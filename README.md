# Ganymede - Digital signatures Backend
![CI/CD](https://github.com/wireapp/ganymede/workflows/CI/CD/badge.svg)
![Release Pipeline](https://github.com/wireapp/ganymede/workflows/Release%20Pipeline/badge.svg)


Ganymede is a stateless microservice wrapper around Swisscom API and Wire internal API used for 
signing digital documents.

## Dev Stack
* HTTP Server - [Ktor](https://ktor.io/)
* HTTP Client - [Apache](https://ktor.io/clients/http-client/engines.html) under [Ktor](https://ktor.io/)
* Dependency Injection - [Kodein](https://github.com/Kodein-Framework/Kodein-DI)
* Build system - [Gradle](https://gradle.org/)

## Usage
Ganymede can be deployed on the bare metal running JVM or as Docker container.

### Bare metal
Prerequisite is JVM >= 8.

#### Production
Release pipeline creates production ready tar archive which contains everything necessary.
One can find the latest releases [here](https://github.com/wireapp/ganymede/releases) with artifacts `ganymede.tar.gz`.
To use them, unpack them and execute:
```bash
./run.sh -p <keystore password>
```
for the default configuration. If there's need to edit the configuration, one can edit `.env.template`.
The run script takes this template file, generates runtime configuration and executes Ganymede.

For more information see [README.md](deployment/ganymede/README.md).

#### Development
For more details see [Makefile](Makefile).
* To run the application simply execute `make run` or `./gradlew run`.
* To run the application inside the docker compose environment run `make up`

### Docker container
Docker container is build without the configuration and without Swisscom certificate so one must provide own settings.
The easiest way how to do that is to use docker-compose.
An [example one](docker-compose.yml) is in the repository.

## Configuration
If one is using `run.sh` generate by the release pipeline, following configuration is automatic.

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
