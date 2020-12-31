plugins {
    kotlin("jvm") version "1.4.21"
    application
    distribution
    id("net.nemerosa.versioning") version "2.14.0"
}

group = "com.wire.ganymede"
version = versioning.info?.tag ?: versioning.info?.lastTag ?: versioning.info?.build ?: "development"

val mClass = "com.wire.ganymede.AppKt"

application {
    mainClass.set(mClass)
}

repositories {
    jcenter()
}

dependencies {
    // extension functions
    implementation("pw.forst.tools", "katlib", "1.2.1")

    // Ktor server dependencies
    val ktorVersion = "1.5.0"
    implementation("io.ktor", "ktor-server-core", ktorVersion)
    implementation("io.ktor", "ktor-server-netty", ktorVersion)
    implementation("io.ktor", "ktor-jackson", ktorVersion)

    // Prometheus metrics
    implementation("io.ktor", "ktor-metrics-micrometer", ktorVersion)
    implementation("io.micrometer", "micrometer-registry-prometheus", "1.6.2")

    // Ktor client dependencies
    implementation("io.ktor", "ktor-client-json", ktorVersion)
    implementation("io.ktor", "ktor-client-jackson", ktorVersion)
    implementation("io.ktor", "ktor-client-apache", ktorVersion)
    implementation("io.ktor", "ktor-client-logging-jvm", ktorVersion)

    // logging
    implementation("io.github.microutils", "kotlin-logging", "2.0.4")
    implementation("ch.qos.logback", "logback-classic", "1.3.0-alpha5")

    // DI
    val kodeinVersion = "6.5.5"
    implementation("org.kodein.di", "kodein-di-generic-jvm", kodeinVersion)
    implementation("org.kodein.di", "kodein-di-framework-ktor-server-jvm", kodeinVersion)

    // tests
    testImplementation("io.mockk", "mockk", "1.10.4")
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit5"))
    val junitVersion = "5.7.0"
    testImplementation("org.junit.jupiter", "junit-jupiter-params", junitVersion)
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", junitVersion)
}

sourceSets {
    test {
        resources {
            srcDir("test")
        }
    }
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    distTar {
        archiveFileName.set("app.tar")
    }

    withType<Test> {
        useJUnitPlatform()
    }

    register<Jar>("fatJar") {
        manifest {
            attributes["Main-Class"] = mClass
        }
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        archiveFileName.set("app.jar")
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
        from(sourceSets.main.get().output)
    }

    register("resolveDependencies") {
        doLast {
            project.allprojects.forEach { subProject ->
                with(subProject) {
                    buildscript.configurations.forEach { if (it.isCanBeResolved) it.resolve() }
                    configurations.compileClasspath.get().resolve()
                    configurations.testCompileClasspath.get().resolve()
                }
            }
        }
    }
}
