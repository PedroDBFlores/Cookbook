plugins {
    application
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.serialization") version "1.9.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("com.github.ben-manes.versions") version "0.47.0"
    id("com.adarshr.test-logger") version "3.2.0"
}

group = "pt.pedro"

application {
    this.mainClass.set("ApplicationKt")
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

val logbackVersion = "1.4.9"
val hopliteVersion = "2.7.4"
val ktorVersion = "2.3.3"
val exposedVersion = "0.42.0"
val h2Version = "2.2.220"
val postgresVersion = "42.6.0"
val flywayVersion = "9.21.1"

val kotestVersion = "5.6.2"
val kotestAssertionsKtorVersion = "2.0.0"
val mockkVersion = "1.13.5"

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-host-common:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-server-default-headers:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-default-headers:$ktorVersion")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("com.h2database:h2:$h2Version")
    implementation("org.postgresql:postgresql:$postgresVersion")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("com.sksamuel.hoplite:hoplite-core:$hopliteVersion")
    implementation("com.sksamuel.hoplite:hoplite-hocon:$hopliteVersion")
    implementation("org.flywaydb:flyway-core:$flywayVersion")

    /* Tests */
    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion") // for kotest framework
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion") // for kotest core jvm assertions
    testImplementation("io.kotest:kotest-assertions-json:$kotestVersion") // for kotest json assertions
    testImplementation("io.kotest:kotest-property-jvm:$kotestVersion") // for kotest property test
    testImplementation("io.kotest.extensions:kotest-assertions-ktor:$kotestAssertionsKtorVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
}

sourceSets {
    create("testIntegration") {
        kotlin.srcDir("src/testIntegration/kotlin")
        resources.srcDir("src/testIntegration/resources")
        compileClasspath += sourceSets["main"].output + configurations["testRuntimeClasspath"]
        runtimeClasspath += output + compileClasspath + sourceSets["test"].runtimeClasspath
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

task<Test>("testIntegration") {
    description = "Runs the integration tests"
    group = "verification"
    testClassesDirs = sourceSets["testIntegration"].output.classesDirs
    classpath = sourceSets["testIntegration"].runtimeClasspath
}

tasks.check {
    dependsOn("testIntegration")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    mergeServiceFiles()
}
