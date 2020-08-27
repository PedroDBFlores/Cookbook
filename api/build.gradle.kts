plugins {
    application
    kotlin("jvm") version "1.4.0"
    id("com.github.johnrengelman.shadow") version "6.0.0"
    id("com.github.ben-manes.versions") version "0.29.0"
    id("com.adarshr.test-logger") version "2.1.0"
//    id("org.jlleitschuh.gradle.ktlint") version "9.3.0"
}

group = "pt.pedro"

application {
    mainClassName = "ApplicationKt"
}

repositories {
    mavenLocal()
    jcenter()
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://kotlin.bintray.com/ktor") }
}

val kotlinVersion: String by project
val hopliteVersion: String by project
val ktorVersion: String by project
val kodeinVersion: String by project
val logbackVersion: String by project
val exposedVersion: String by project
val h2Version: String by project
val jodaDataTypeVersion: String by project
val auth0Version: String by project

val kotestVersion: String by project
val mockkVersion: String by project
val restAssuredVersion: String by project

dependencies {
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-auth:$ktorVersion")
    implementation("io.ktor:ktor-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-jackson:$ktorVersion")
    implementation("io.ktor:ktor-server-host-common:$ktorVersion")
    implementation("org.kodein.di:kodein-di-framework-ktor-server-jvm:$kodeinVersion")
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

    implementation("org.flywaydb:flyway-core:6.5.5")

    /* Tests */
    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion") // for kotest framework
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion") // for kotest core jvm assertions
    testImplementation("io.kotest:kotest-assertions-json:$kotestVersion") // for kotest json assertions
    testImplementation("io.kotest:kotest-property-jvm:$kotestVersion") // for kotest property test
    testImplementation("io.kotest:kotest-assertions-ktor-jvm:$kotestVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
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

//ktlint {
//    disabledRules.set(setOf("no-wildcard-imports", "indent"))
//}
