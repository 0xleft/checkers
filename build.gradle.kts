plugins {
    java
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "uk.wwws"
version = "1.0.0"

repositories {
    mavenCentral()
}

val junitVersion = "5.12.1"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.register<JavaExec>("runClient") {
    group = "application"
    mainClass.set("uk.wwws.checkers.apps.entrypoints.launchers.ClientLauncher")
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register<JavaExec>("runServer") {
    group = "application"
    mainClass.set("uk.wwws.checkers.apps.entrypoints.ServerApp")
    classpath = sourceSets["main"].runtimeClasspath
    standardInput = System.`in`;
}

tasks.register<JavaExec>("runAI") {
    group = "application"
    mainClass.set("uk.wwws.checkers.apps.entrypoints.launchers.AILauncher")
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register<Jar>("jarClient") {
    group = "build"
    archiveBaseName.set("client")
    manifest {
        attributes("Main-Class" to "uk.wwws.checkers.apps.entrypoints.launchers.ClientLauncher")
    }
    from(
        sourceSets["main"].runtimeClasspath,
        configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.register<Jar>("jarAI") {
    group = "build"
    archiveBaseName.set("ai")
    manifest {
        attributes("Main-Class" to "uk.wwws.checkers.apps.entrypoints.launchers.AILauncher")
    }
    from(
        sourceSets["main"].runtimeClasspath,
        configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.register<Jar>("jarServer") {
    group = "build"
    archiveBaseName.set("server")
    manifest {
        attributes("Main-Class" to "uk.wwws.checkers.apps.entrypoints.ServerApp")
    }
    from(
        sourceSets["main"].runtimeClasspath,
        configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.register("jarAll") {
    group = "build"
    dependsOn("clean", "jarAI", "jarServer", "jarClient")
}

javafx {
    version = "21.0.6"
    modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
    implementation("org.controlsfx:controlsfx:11.2.1")
    implementation("org.jetbrains:annotations:23.0.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
    implementation("org.apache.logging.log4j:log4j-api:2.22.0")
    implementation("org.apache.logging.log4j:log4j-core:2.22.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}