plugins {
    application
    kotlin("jvm") version "1.4.20"
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("com.github.ben-manes.versions") version "0.36.0"
    id("com.adarshr.test-logger") version "2.1.1"
    id("org.jlleitschuh.gradle.ktlint") version "9.4.1"
    // id("com.github.node-gradle.node") version "3.0.0-rc7"
}

group = "pt.pedro"

application {
    @Suppress("Deprecation")
    mainClassName = "ApplicationKt"
}

repositories {
    mavenLocal()
    jcenter()
    maven { url = uri("https://jitpack.io") }
}

val logbackVersion = "1.2.1"
val kotlinVersion = "1.4.20"
val hopliteVersion = "1.3.12"
val kodeinVersion = "7.1.0"
val ktorVersion = "1.5.0"
val exposedVersion = "0.28.1"
val h2Version = "1.4.200"
val mySQLVersion = "8.0.22"
val flywayVersion = "7.3.2"
val auth0Version = "3.11.0"
val jodaDataTypeVersion = "2.12.0"
val kotestVersion = "4.3.2"
val mockkVersion = "1.10.4"

dependencies {
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-jackson:$ktorVersion")
    implementation("io.ktor:ktor-server-host-common:$ktorVersion")
    implementation("org.kodein.di:kodein-di-framework-ktor-server-jvm:$kodeinVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-joda:$jodaDataTypeVersion")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jodatime:$exposedVersion")
    implementation("com.h2database:h2:$h2Version")
    implementation("mysql:mysql-connector-java:$mySQLVersion")
    implementation("com.zaxxer:HikariCP:3.4.5")
    implementation("at.favre.lib:bcrypt:0.9.+")
    implementation("com.sksamuel.hoplite:hoplite-core:$hopliteVersion")
    implementation("com.sksamuel.hoplite:hoplite-hocon:$hopliteVersion")

    implementation("org.flywaydb:flyway-core:$flywayVersion")

    /* Tests */
    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion") // for kotest framework
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion") // for kotest core jvm assertions
    testImplementation("io.kotest:kotest-assertions-json:$kotestVersion") // for kotest json assertions
    testImplementation("io.kotest:kotest-property-jvm:$kotestVersion") // for kotest property test
    testImplementation("io.kotest:kotest-assertions-ktor-jvm:$kotestVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
}

sourceSets {
    create("testIntegration") {
        withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class) {
            kotlin.srcDir("src/testIntegration/kotlin")
            resources.srcDir("src/testIntegration/resources")
            compileClasspath += sourceSets["main"].output + configurations["testRuntimeClasspath"]
            runtimeClasspath += output + compileClasspath + sourceSets["test"].runtimeClasspath
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
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

ktlint {
    disabledRules.set(setOf("no-wildcard-imports"))
}
