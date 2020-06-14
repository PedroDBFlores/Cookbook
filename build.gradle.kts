import com.moowork.gradle.node.npm.NpmTask

val logbackVersion: String by project
val ktorVersion: String by project
val kotlinVersion: String by project
val exposedVersion: String by project
val h2Version: String by project
val kodeinVersion: String by project

val kotestVersion: String by project
val mockkVersion: String by project

plugins {
    application
    kotlin("jvm") version "1.3.72"
    id("com.moowork.node") version "1.3.1"
    id("org.unbroken-dome.test-sets") version "3.0.1"
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
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-host-common:$ktorVersion")
    implementation("io.ktor:ktor-auth:$ktorVersion")
    implementation("io.ktor:ktor-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-jackson:$ktorVersion")
    implementation("io.ktor:ktor-metrics:$ktorVersion")
    implementation("org.kodein.di:kodein-di:$kodeinVersion")
    implementation("org.kodein.di:kodein-di-framework-ktor-server-controller-jvm:$kodeinVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-joda:2.10.3")

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jodatime:$exposedVersion")
    implementation("com.h2database:h2:$h2Version")
    implementation("com.zaxxer:HikariCP:3.4.5")

    implementation("com.github.papsign:Ktor-OpenAPI-Generator:-SNAPSHOT")

    /* Tests */
    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
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

testSets {
    createTestSet("integrationTest") {
        dirName = "integration-test"
    }
}

tasks.withType<Test> {
    useJUnitPlatform {}
}

tasks{
    named("check") { dependsOn("integrationTest") }
    named("integrationTest", Test::class) {
        mustRunAfter("test")
    }
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

