

plugins {
    application
    kotlin("jvm") version "1.3.72"
    id("com.github.johnrengelman.shadow") version "6.0.0"
    id("com.github.ben-manes.versions") version "0.29.0"
    id("org.jlleitschuh.gradle.ktlint") version "9.3.0"
}

group = "pt.pedro"

application {
    mainClassName = "ApplicationKt"
}

repositories {
    mavenLocal()
    jcenter()
    maven { url = uri("https://jitpack.io") }
}

val kotlinVersion: String by project
val koinVersion: String by project
val hopliteVersion: String by project
val javalinVersion: String by project
val slf4jVersion: String by project
val exposedVersion: String by project
val h2Version: String by project
val jodaDataTypeVersion: String by project
val auth0Version: String by project

val kotestVersion: String by project
val mockkVersion: String by project
val restAssuredVersion: String by project

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("io.javalin:javalin:$javalinVersion")
    implementation("io.javalin:javalin-openapi:$javalinVersion")
    implementation("org.slf4j:slf4j-simple:$slf4jVersion")
    implementation("com.auth0:java-jwt:$auth0Version")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-joda:$jodaDataTypeVersion")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jodatime:$exposedVersion")
    implementation("com.h2database:h2:$h2Version")
    implementation("com.zaxxer:HikariCP:3.4.5")
    implementation("at.favre.lib:bcrypt:0.9.+")
    implementation("com.sksamuel.hoplite:hoplite-core:$hopliteVersion")
    implementation("com.sksamuel.hoplite:hoplite-json:$hopliteVersion")

    /* Tests */
    testImplementation("io.kotest:kotest-runner-console-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion") // for kotest framework
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion") // for kotest core jvm assertions
    testImplementation("io.kotest:kotest-assertions-json:$kotestVersion") // for kotest json assertions
    testImplementation("io.kotest:kotest-property-jvm:$kotestVersion") // for kotest property test
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("com.github.javafaker:javafaker:1.0.2")
    testImplementation("io.rest-assured:rest-assured:$restAssuredVersion")
    testImplementation("io.rest-assured:kotlin-extensions:$restAssuredVersion")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

ktlint {
    disabledRules.set(setOf("no-wildcard-imports", "indent"))
}
