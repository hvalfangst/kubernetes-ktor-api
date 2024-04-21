val ktor_version = "2.2.2"
val kotlin_version: String by project
val logback_version: String by project
val exposedVersion = "0.41.1"
val hikariCpVersion = "5.0.1"
val flywayVersion = "9.10.2"
val postgresVersion = "42.2.18"

plugins {
    application
    kotlin("jvm") version "1.7.22"
    id("io.ktor.plugin") version "2.2.1"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.7.22"
}

group = "hvalbanken"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}


repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

dependencies {
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.1")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("com.zaxxer:HikariCP:$hikariCpVersion")
    implementation("org.flywaydb:flyway-core:$flywayVersion")
    implementation("org.postgresql:postgresql:$postgresVersion")
    implementation("io.ktor:ktor-server-auth:$ktor_version")
    implementation("redis.clients:jedis:3.6.0")
    implementation ("com.google.code.gson:gson:2.8.9")
    implementation("at.favre.lib:bcrypt:0.10.2")
    implementation("io.ktor:ktor-server-auth:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt:$ktor_version")
    testImplementation("io.mockk:mockk:1.10.0")
    testImplementation("io.ktor:ktor-server-test-host:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}