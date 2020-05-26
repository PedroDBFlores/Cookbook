import com.moowork.gradle.node.npm.NpmTask

val logback_version: String by project
val ktor_version: String by project
val kotlin_version: String by project
val exposed_version: String by project
val h2_version: String by project
val kodein_version: String by project

val kotest_version: String by project
val mockk_version: String by project

plugins {
    application
    kotlin("jvm") version "1.3.72"
    id("com.moowork.node") version "1.3.1"
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
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-host-common:$ktor_version")
    implementation("io.ktor:ktor-auth:$ktor_version")
    implementation("io.ktor:ktor-auth-jwt:$ktor_version")
    implementation("io.ktor:ktor-jackson:$ktor_version")
    implementation("io.ktor:ktor-metrics:$ktor_version")
    implementation("org.kodein.di:kodein-di-generic-jvm:$kodein_version")
    implementation("org.kodein.di:kodein-di-framework-ktor-server-controller-jvm:$kodein_version")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-joda:2.10.3")

    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jodatime:$exposed_version")
    implementation("com.h2database:h2:$h2_version")
    implementation("com.zaxxer:HikariCP:3.4.5")

    implementation("com.github.papsign:Ktor-OpenAPI-Generator:-SNAPSHOT")

    /* Tests */
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotest_version") // for kotest framework
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotest_version") // for kotest core jvm assertions
    testImplementation("io.kotest:kotest-property-jvm:$kotest_version") // for kotest property test
    testImplementation("io.mockk:mockk:$mockk_version")
    testImplementation("com.github.javafaker:javafaker:1.0.2")
}

kotlin.sourceSets["main"].kotlin.srcDirs("src")
kotlin.sourceSets["test"].kotlin.srcDirs("test")

sourceSets["main"].resources.srcDirs("resources")
sourceSets["test"].resources.srcDirs("testresources")

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform {}
}

//Webapp

node {
    download = false
    workDir = file("${project.buildDir}/webapp")
}

tasks {
    register<NpmTask>("appNpmInstall") {
        group = "webapp"
        description = "Installs all dependencies from package.json"
        setWorkingDir(file("${project.projectDir}/webapp"))
        setArgs(listOf("install"))
    }

    register<NpmTask>("appNpmTest") {
        dependsOn("appNpmInstall")
        group = "webapp"
        description = "Tests the webapp application for errors"
        setWorkingDir(file("${project.projectDir}/webapp"))
        setArgs(listOf("test"))
    }

    register<NpmTask>("appNpmBuild") {
        dependsOn("appNpmTest")
        group = "webapp"
        description = "Builds production version of the webapp"
        setWorkingDir(file("${project.projectDir}/webapp"))
        setArgs(listOf("run", "build"))
    }

    register<Copy>("appCopy") {
        dependsOn("appNpmBuild")
        group = "webapp"
        from("${project.projectDir}/webapp/dist")
        into("build/resources/main/static/.")
    }
}

