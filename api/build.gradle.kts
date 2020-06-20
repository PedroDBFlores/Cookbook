val logbackVersion: String by project
val javalinVersion: String by project
val kotlinVersion: String by project
val exposedVersion: String by project
val h2Version: String by project
val kodeinVersion: String by project

val kotestVersion: String by project
val mockkVersion: String by project

plugins {
    application
    kotlin("jvm") version "1.3.72"
    id ("com.github.ben-manes.versions") version "0.28.0"
}

group = "pt.pedro"
version = "1.0.0"

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}

repositories {
    mavenLocal()
    jcenter()
    maven { url = uri("https://kotlin.bintray.com/ktor") }
    maven { url = uri("https://dl.bintray.com/kodein-framework/Kodein-DI") }
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("io.javalin:javalin:$javalinVersion")
    implementation("io.javalin:javalin-openapi:$javalinVersion")
    implementation("io.swagger.core.v3:swagger-core:2.1.+")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-joda:2.10.3")

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jodatime:$exposedVersion")
    implementation("com.h2database:h2:$h2Version")
    implementation("com.zaxxer:HikariCP:3.4.5")

    /* Tests */
    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion") // for kotest framework
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion") // for kotest core jvm assertions
    testImplementation("io.kotest:kotest-assertions-json:$kotestVersion") // for kotest json assertions
    testImplementation("io.kotest:kotest-property-jvm:$kotestVersion") // for kotest property test
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("com.github.javafaker:javafaker:1.0.2")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform {}
}

